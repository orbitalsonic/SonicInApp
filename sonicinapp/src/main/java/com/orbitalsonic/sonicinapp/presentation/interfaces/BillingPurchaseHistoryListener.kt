package com.orbitalsonic.sonicinapp.presentation.interfaces

import com.orbitalsonic.sonicinapp.data.entities.purchase.PurchaseDetail

/**
 * @Author: Muhammad Yaqoob
 * @Date: 18, February 2026
 * @Accounts
 *      -> https://github.com/orbitalsonic
 *      -> https://www.linkedin.com/in/myaqoob7
 */

interface BillingPurchaseHistoryListener {
    fun onSuccess(purchaseDetails: List<PurchaseDetail>)
    fun onError(message: String) {}
}