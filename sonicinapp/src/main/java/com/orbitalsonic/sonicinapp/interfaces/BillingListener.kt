package com.orbitalsonic.sonicinapp.interfaces

import com.orbitalsonic.sonicinapp.dataClasses.PurchaseDetail

/**
 * @Author: Muhammad Yaqoob
 * @Date: 01,October,2024.
 * @Accounts
 *      -> https://github.com/orbitalsonic
 *      -> https://www.linkedin.com/in/myaqoob7
 */

interface BillingListener {
    fun onConnectionResult(isSuccess: Boolean, message: String)
    fun purchasesResult(purchaseDetailList: List<PurchaseDetail>)
}