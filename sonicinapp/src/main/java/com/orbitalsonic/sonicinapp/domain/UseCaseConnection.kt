package com.orbitalsonic.sonicinapp.domain

import com.orbitalsonic.sonicinapp.data.repository.BillingRepository
import com.orbitalsonic.sonicinapp.presentation.states.BillingState

/**
 * @Author: Muhammad Yaqoob
 * @Date: 18, February 2026
 * @Accounts
 *      -> https://github.com/orbitalsonic
 *      -> https://www.linkedin.com/in/myaqoob7
 */

internal class UseCaseConnection(private val repository: BillingRepository) {

    private var isConnecting = false

    fun startConnection(onResult: (Boolean, String?) -> Unit) {
        if (repository.isBillingClientReady) {
            repository.currentState = BillingState.ALREADY_CONNECTED
            onResult(true, null)
            return
        }

        if (isConnecting) {
            repository.currentState = BillingState.CONNECTING_IN_PROGRESS
            onResult(false, null)
            return
        }

        isConnecting = true
        repository.startConnection(onResult)
        isConnecting = false
    }
}