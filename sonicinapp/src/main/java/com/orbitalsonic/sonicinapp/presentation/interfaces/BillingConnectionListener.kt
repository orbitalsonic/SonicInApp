package com.orbitalsonic.sonicinapp.presentation.interfaces

/**
 * @Author: Muhammad Yaqoob
 * @Date: 18, February 2026
 * @Accounts
 *      -> https://github.com/orbitalsonic
 *      -> https://www.linkedin.com/in/myaqoob7
 */

interface BillingConnectionListener {
    fun onBillingClientConnected(isSuccess: Boolean, message: String)
}