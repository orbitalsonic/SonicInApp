package com.orbitalsonic.sonicinapp.presentation.interfaces

/**
 * @Author: Muhammad Yaqoob
 * @Date: 18, February 2026
 * @Accounts
 *      -> https://github.com/orbitalsonic
 *      -> https://www.linkedin.com/in/myaqoob7
 */

interface BillingPurchaseListener {
    fun onPurchaseResult(message: String)
    fun onError(message: String)
}