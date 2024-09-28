package com.orbitalsonic.sonicinapp.dataClasses

import com.android.billingclient.api.ProductDetails

/**
 * @Author: Muhammad Yaqoob
 * @Date: 01,October,2024.
 * @Accounts
 *      -> https://github.com/orbitalsonic
 *      -> https://www.linkedin.com/in/myaqoob7
 */

internal data class QueryProductDetail(
    val productDetail: ProductDetail,
    val productDetails: ProductDetails,
    val offerDetails: ProductDetails.SubscriptionOfferDetails?
)