package com.orbitalsonic.sonicinapp.presentation.interfaces

import com.orbitalsonic.sonicinapp.data.entities.product.ProductDetail

/**
 * @Author: Muhammad Yaqoob
 * @Date: 18, February 2026
 * @Accounts
 *      -> https://github.com/orbitalsonic
 *      -> https://www.linkedin.com/in/myaqoob7
 */

interface BillingProductDetailsListener {
    fun onSuccess(productDetails: List<ProductDetail>)
    fun onError(message: String) {}
}