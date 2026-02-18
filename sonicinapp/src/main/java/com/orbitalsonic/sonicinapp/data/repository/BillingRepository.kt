package com.orbitalsonic.sonicinapp.data.repository

import android.app.Activity
import com.android.billingclient.api.BillingFlowParams
import com.android.billingclient.api.Purchase
import com.orbitalsonic.sonicinapp.data.dataSource.BillingService
import com.orbitalsonic.sonicinapp.presentation.states.BillingState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * @Author: Muhammad Yaqoob
 * @Date: 18, February 2026
 * @Accounts
 *      -> https://github.com/orbitalsonic
 *      -> https://www.linkedin.com/in/myaqoob7
 */

internal class BillingRepository(private val billingService: BillingService) {

    val isBillingClientReady get() =  billingService.isBillingClientReady
    var currentState: BillingState get() = billingService.currentState
        internal set(value) {
            billingService.currentState = value
        }

    fun startConnection(onResult: (Boolean, String?) -> Unit) = billingService.startConnection(onResult)

    /* ------------------------------- Purchase History ------------------------------- */

    suspend fun queryInAppPurchases() = withContext(Dispatchers.IO) {
        billingService.queryInAppPurchases()
    }

    suspend fun querySubsPurchases() = withContext(Dispatchers.IO) {
        billingService.querySubsPurchases()
    }

    /* ----------------------------------- Products ----------------------------------- */

    suspend fun queryInAppProductDetails(productIds: List<String>) = withContext(Dispatchers.IO) {
        billingService.queryInAppProductDetails(productIds)
    }

    suspend fun querySubsProductDetails(productIds: List<String>) = withContext(Dispatchers.IO) {
        billingService.querySubsProductDetails(productIds)
    }

    /* ----------------------------------- Purchases ----------------------------------- */

    suspend fun purchaseFlow(activity: Activity, params: BillingFlowParams) = withContext(Dispatchers.Main) {
        billingService.purchaseFlow(activity, params)
    }

    /* ------------------------------- Acknowledgements ------------------------------- */

    suspend fun consumePurchases(list: List<Purchase>) = withContext(Dispatchers.IO) {
        billingService.consumePurchases(list)
    }

    /* ------------------------------- Acknowledgements ------------------------------- */
    suspend fun acknowledgePurchases(list: List<Purchase>) = withContext(Dispatchers.IO) {
        billingService.acknowledgePurchases(list)
    }
}