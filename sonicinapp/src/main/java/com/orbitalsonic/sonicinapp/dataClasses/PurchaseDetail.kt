package com.orbitalsonic.sonicinapp.dataClasses

/**
 * @Author: Muhammad Yaqoob
 * @Date: 01,October,2024.
 * @Accounts
 *      -> https://github.com/orbitalsonic
 *      -> https://www.linkedin.com/in/myaqoob7
 */

/**
 * @param productId: Product Id for both inapp/subs (e.g. product_ads/product_weekly_ads)
 * @param planId: Plan Id for subs (e.g. plan_weekly_ads)
 * @param productTitle: Title of the Product
 * @param planTitle: Title of the Plan
 * @param productType: Product purchase type (e.g. InApp/Subs)
 * @param purchaseToken: a unique token for this purchase
 * @param purchaseTime: For subscriptions, this is the subscription signup time. It won't change after renewal.
 * @param purchaseTimeMillis: UnixTimeStamp (starts from Jan 1, 1970)
 * @param isAutoRenewing: Only in case of 'BillingClient.ProductType.SUBS'
 */

data class PurchaseDetail(
    val productId: String,
    var planId: String,
    var productTitle: String,
    var planTitle: String,
    val purchaseToken: String,
    val productType: ProductType,
    val purchaseTime: String,
    val purchaseTimeMillis: Long,
    val isAutoRenewing: Boolean,
)