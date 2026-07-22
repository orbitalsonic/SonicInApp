package com.orbitalsonic.sonicinapp.domain

import android.util.Log
import com.android.billingclient.api.ProductDetails
import com.android.billingclient.api.ProductDetails.PricingPhase
import com.android.billingclient.api.ProductDetails.PricingPhases
import com.android.billingclient.api.Purchase
import com.orbitalsonic.sonicinapp.utilities.constants.Constants.TAG
import com.orbitalsonic.sonicinapp.data.repository.BillingRepository
import com.orbitalsonic.sonicinapp.data.entities.purchase.PurchaseDetail
import com.orbitalsonic.sonicinapp.presentation.enums.ProductType
import com.orbitalsonic.sonicinapp.presentation.states.BillingState
import com.orbitalsonic.sonicinapp.presentation.states.QueryResponse
import com.orbitalsonic.sonicinapp.utilities.extensions.toFormattedDate
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.withContext

/**
 * @Author: Muhammad Yaqoob
 * @Date: 18, February 2026
 * @Accounts
 *      -> https://github.com/orbitalsonic
 *      -> https://www.linkedin.com/in/myaqoob7
 */

internal class UseCaseQueryPurchases(private val repository: BillingRepository) {

    private val mutex = Mutex()

    /** Active subs + non‑consumed one‑time purchases (cache‑only). */
    suspend fun queryPurchases(): QueryResponse<List<PurchaseDetail>> = withContext(Dispatchers.Default) {

        if (!repository.isBillingClientReady) {
            repository.currentState = BillingState.CONNECTION_INVALID
            return@withContext QueryResponse.Error("Play Billing not ready.")
        }
        if (!mutex.tryLock()) return@withContext QueryResponse.Loading

        repository.currentState = BillingState.CONSOLE_PURCHASE_PROCESSING

        val result = runCatching {
            coroutineScope {

                /* ── 1. get raw purchases (parallel) ─────────────────── */
                val inAppAsync = async { repository.queryInAppPurchases() }
                val subsAsync = async { repository.querySubsPurchases() }

                val inApp = inAppAsync.await().filterNot { it.isSuspended }
                val subs = subsAsync.await().filterNot { it.isSuspended }

                /* ── 2. fetch product details (parallel) ─────────────── */
                val inAppIds = inApp.flatMap { it.products }
                val subsIds = subs.flatMap { it.products }

                /* ── 3. Conditionally query product details ─ */
                val inAppPdDeferred = inAppIds
                    .takeIf { it.isNotEmpty() }
                    ?.let { async { repository.queryInAppProductDetails(it) } }

                val subsPdDeferred = subsIds
                    .takeIf { it.isNotEmpty() }
                    ?.let { async { repository.querySubsProductDetails(it) } }

                val inAppPd = inAppPdDeferred?.await().orEmpty()
                val subsPd = subsPdDeferred?.await().orEmpty()

                /* ── 4. check for Acknowledgments ───────────────────────── */
                checkForAcknowledgedPurchases(inApp, subs)

                /* ── 5. map to domain objects ───────────────────────── */
                inApp.flatMap { it.toDomainList(inAppPd, ProductType.inapp) } +
                        subs.flatMap { it.toDomainList(subsPd, ProductType.subs) }
            }
        }

        mutex.unlock()

        result.fold(
            onSuccess = {
                repository.currentState = BillingState.CONSOLE_PURCHASE_COMPLETED
                QueryResponse.Success(it)
            },
            onFailure = {
                Log.e(TAG, "UseCaseQueryPurchases: queryPurchases: ", it)
                repository.currentState = BillingState.CONSOLE_PURCHASE_FAILED
                QueryResponse.Error(it.localizedMessage ?: "Unknown error")
            }
        )
    }

    private suspend fun checkForAcknowledgedPurchases(inApp: List<Purchase>, subs: List<Purchase>) {
        val list = inApp + subs
        Log.i(TAG, "BillingService: checkForAcknowledgements: ${list.count { it.isAcknowledged.not() }} purchase(s) needs to be acknowledge")

        val unAcknowledgeList = list.filter { it.isAcknowledged.not() }
        if (unAcknowledgeList.isNotEmpty()) {
            repository.currentState = BillingState.CONSOLE_PURCHASE_ACKNOWLEDGEMENT_CHECK
            repository.acknowledgePurchases(unAcknowledgeList)
        }
    }

    /* ───────────────────── mapping helpers ───────────────────────────── */

    private fun Purchase.toDomainList(details: List<ProductDetails>, type: ProductType): List<PurchaseDetail> = products.mapNotNull { id ->

        val productDetails = details.firstOrNull { it.productId == id }
        val offer = productDetails?.subscriptionOfferDetails?.firstOrNull()
        val planTitle = offer?.pricingPhases?.originalPhase()?.billingPeriod.toPlanTitle()

        PurchaseDetail(
            productId = id,
            planId = offer?.basePlanId,
            productTitle = productDetails?.title,
            planTitle = planTitle,
            purchaseToken = purchaseToken,
            productType = type,
            purchaseTime = purchaseTime.toFormattedDate(),
            purchaseTimeMillis = purchaseTime,
            isAutoRenewing = isAutoRenewing,
            isAcknowledged = isAcknowledged
        )
    }

    /* PricingPhases helpers */
    private fun PricingPhases.originalPhase(): PricingPhase? =
        pricingPhaseList.firstOrNull { it.priceAmountMicros > 0 && it.billingPeriod != "P0D" }

    private fun String?.toPlanTitle(): String = when (this) {
        "P1W" -> "Weekly"; "P4W" -> "Four weeks"; "P1M" -> "Monthly"
        "P2M" -> "2 months"; "P3M" -> "3 months"; "P4M" -> "4 months"
        "P6M" -> "6 months"; "P8M" -> "8 months"; "P1Y" -> "Yearly"
        else -> ""
    }
}
