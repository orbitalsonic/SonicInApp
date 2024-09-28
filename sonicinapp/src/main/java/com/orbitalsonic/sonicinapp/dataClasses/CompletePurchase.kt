package com.orbitalsonic.sonicinapp.dataClasses

import com.android.billingclient.api.ProductDetails
import com.android.billingclient.api.Purchase

/**
 * @Author: Muhammad Yaqoob
 * @Date: 01,October,2024.
 * @Accounts
 *      -> https://github.com/orbitalsonic
 *      -> https://www.linkedin.com/in/myaqoob7
 */

/**
 *      A single purchase can hold multiple products,
 *      this class create a new complete data class with
 *      product detail as per purchase.
 */

data class CompletePurchase(
    val purchase: Purchase,
    val productDetailList: List<ProductDetails>
)