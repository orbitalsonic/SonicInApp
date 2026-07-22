package com.orbitalsonic.sonicinapp

import android.app.Activity
import android.content.Context
import android.util.Log
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.PendingPurchasesParams
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.PurchasesUpdatedListener
import com.orbitalsonic.sonicinapp.data.dataSource.BillingService
import com.orbitalsonic.sonicinapp.data.repository.BillingRepository
import com.orbitalsonic.sonicinapp.domain.UseCaseConnection
import com.orbitalsonic.sonicinapp.domain.UseCasePurchase
import com.orbitalsonic.sonicinapp.domain.UseCaseQueryProducts
import com.orbitalsonic.sonicinapp.domain.UseCaseQueryPurchases
import com.orbitalsonic.sonicinapp.presentation.interfaces.BillingConnectionListener
import com.orbitalsonic.sonicinapp.presentation.interfaces.BillingProductDetailsListener
import com.orbitalsonic.sonicinapp.presentation.interfaces.BillingPurchaseHistoryListener
import com.orbitalsonic.sonicinapp.presentation.interfaces.BillingPurchaseListener
import com.orbitalsonic.sonicinapp.presentation.states.BillingState
import com.orbitalsonic.sonicinapp.presentation.states.QueryResponse
import com.orbitalsonic.sonicinapp.utilities.constants.Constants.TAG
import com.orbitalsonic.sonicinapp.utilities.extensions.setAll
import com.orbitalsonic.sonicinapp.utilities.responses.BillingResponse
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

/**
 * @Author: Muhammad Yaqoob
 * @Date: 18, February 2026
 * @Accounts
 *      -> https://github.com/orbitalsonic
 *      -> https://www.linkedin.com/in/myaqoob7
 */

class BillingManager(
    context: Context,
    private val scope: CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)
) {

    /* ▾ lazy‑built Play client ---------------------------------------------------- */
    private val purchasesUpdatedListener = PurchasesUpdatedListener { billingResult, purchases: List<Purchase>? ->
        Log.d(TAG, "BillingManager: purchasesUpdatedListener: ${billingResult.responseCode} -- ${billingResult.debugMessage}")
        purchaseUpdateListener(billingResult, purchases)
    }

    private val billingClient by lazy {
        BillingClient.newBuilder(context)
            .setListener(purchasesUpdatedListener)
            .enablePendingPurchases(PendingPurchasesParams.newBuilder().enableOneTimeProducts().build())
            .enableAutoServiceReconnection()
            .build()
    }

    /* ───────── Clean‑Arch stack ────────────────────────────────────────── */
    private val billingService by lazy { BillingService(billingClient) }
    private val billingRepository by lazy { BillingRepository(billingService) }
    private val useCaseConnection by lazy { UseCaseConnection(billingRepository) }
    private val useCaseQueryPurchases by lazy { UseCaseQueryPurchases(billingRepository) }
    private val useCaseQueryProducts by lazy { UseCaseQueryProducts(billingRepository) }
    private val useCasePurchase by lazy { UseCasePurchase(billingRepository) }

    /* ───────── Config & listener ───────────────────────────────────────── */
    private val _nonConsumables = mutableListOf<String>()
    private val _consumables = mutableListOf<String>()
    private val _subscriptions = mutableListOf<String>()
    private var connectionListener: BillingConnectionListener? = null

    val nonConsumableIds: List<String> get() = _nonConsumables
    val consumableIds: List<String> get() = _consumables
    val subscriptionIds: List<String> get() = _subscriptions

    fun setNonConsumables(ids: List<String>) = apply { _nonConsumables.setAll(ids) }
    fun setConsumables(ids: List<String>) = apply { _consumables.setAll(ids) }
    fun setSubscriptions(ids: List<String>) = apply { _subscriptions.setAll(ids) }
    fun setListener(listener: BillingConnectionListener?) = apply { connectionListener = listener }

    private var billingPurchaseListener: BillingPurchaseListener? = null

    /* ───────── Public API ──────────────────────────────────────────────── */
    fun startConnection() {
        useCaseConnection.startConnection { isSuccess, message ->
            connectionListener?.onBillingClientConnected(isSuccess, message ?: billingService.currentState.message)
        }
    }

    fun fetchPurchaseHistory(listener: BillingPurchaseHistoryListener) {
        scope.launch {
            when (val response = useCaseQueryPurchases.queryPurchases()) {
                is QueryResponse.Loading -> {}
                is QueryResponse.Success -> listener.onSuccess(response.data)
                is QueryResponse.Error -> listener.onError(response.errorMessage)
            }
        }
    }

    fun fetchProductDetails(listener: BillingProductDetailsListener) {
        scope.launch {
            when (val response = useCaseQueryProducts.queryProducts(nonConsumableIds, consumableIds, subscriptionIds)) {
                is QueryResponse.Loading -> {}
                is QueryResponse.Success -> listener.onSuccess(response.data)
                is QueryResponse.Error -> listener.onError(response.errorMessage)
            }
        }
    }

    /**
     * Returns a single product (optionally scoped by plan) from the in‑memory cache.
     *  @param productId: Pass the product id (inapp,subs)
     *  @param planId: pass the plan id (if subscription only else null)
     */
    fun getProductDetail(productId: String, planId: String?, listener: BillingProductDetailsListener) {
        scope.launch {
            when (val response = useCaseQueryProducts.queryProducts(productId, planId)) {
                is QueryResponse.Loading -> {}
                is QueryResponse.Success -> listener.onSuccess(response.data)
                is QueryResponse.Error -> listener.onError(response.errorMessage)
            }
        }
    }

    fun purchaseInApp(activity: Activity?, productId: String, listener: BillingPurchaseListener) {
        this.billingPurchaseListener = listener

        scope.launch {
            when (val response = useCasePurchase.purchaseInApp(activity, productId)) {
                is QueryResponse.Loading -> {}
                is QueryResponse.Success -> {}
                is QueryResponse.Error -> listener.onError(response.errorMessage)
            }
        }
    }

    fun purchaseSubs(activity: Activity?, productId: String, planId: String, listener: BillingPurchaseListener) {
        this.billingPurchaseListener = listener

        scope.launch {
            when (val response = useCasePurchase.purchaseSubs(activity, productId, planId)) {
                is QueryResponse.Loading -> {}
                is QueryResponse.Success -> {}
                is QueryResponse.Error -> listener.onError(response.errorMessage)
            }
        }
    }

    fun updateSubs(activity: Activity?, oldProductId: String, productId: String, planId: String, listener: BillingPurchaseListener) {
        this.billingPurchaseListener = listener

        scope.launch {
            when (val response = useCasePurchase.updateSubs(activity, oldProductId, productId, planId)) {
                is QueryResponse.Loading -> {}
                is QueryResponse.Success -> {}
                is QueryResponse.Error -> listener.onError(response.errorMessage)
            }
        }
    }

    private fun purchaseUpdateListener(billingResult: BillingResult, purchases: List<Purchase>?) {
        scope.launch {
            val response = BillingResponse(billingResult.responseCode)
            when {
                response.isOk -> {
                    billingService.currentState = BillingState.PURCHASE_SUCCESS
                    billingPurchaseListener?.onPurchaseResult(BillingState.PURCHASE_SUCCESS.message)
                    useCasePurchase.handlePurchase(purchases, consumableIds)
                    return@launch
                }

                response.isAlreadyOwned -> {
                    billingService.currentState = BillingState.PURCHASE_ALREADY_OWNED
                    billingPurchaseListener?.onPurchaseResult(BillingState.PURCHASE_ALREADY_OWNED.message)
                    useCasePurchase.handlePurchase(purchases, consumableIds)
                    return@launch
                }

                response.isUserCancelled -> billingService.currentState = BillingState.BILLING_FLOW_USER_CANCELLED
                response.isTerribleFailure -> billingService.currentState = BillingState.BILLING_FLOW_EXCEPTION
                response.isRecoverableError -> billingService.currentState = BillingState.BILLING_FLOW_EXCEPTION
                response.isNonrecoverableError -> billingService.currentState = BillingState.BILLING_FLOW_EXCEPTION
            }
            billingService.currentState = BillingState.PURCHASE_FAILED
            billingPurchaseListener?.onError(billingService.currentState.message)
        }
    }
}