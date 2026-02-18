package com.orbitalsonic.sonicinapp.presentation.states

/**
 * @Author: Muhammad Yaqoob
 * @Date: 18, February 2026
 * @Accounts
 *      -> https://github.com/orbitalsonic
 *      -> https://www.linkedin.com/in/myaqoob7
 */

sealed class QueryResponse<out T> {
    object Loading : QueryResponse<Nothing>()
    data class Success<out T>(val data: T) : QueryResponse<T>()
    data class Error(val errorMessage: String) : QueryResponse<Nothing>()
}