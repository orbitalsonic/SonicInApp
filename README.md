[![](https://jitpack.io/v/orbitalsonic/SonicInApp.svg)](https://jitpack.io/#orbitalsonic/SonicInApp)
# SonicInApp

**SonicInApp** is a [Google Play Billing](https://developer.android.com/google/play/billing/integrate) library designed to simplify the integration of in-app purchases and subscriptions in Android applications.

## Gradle Setup

### Step 1: Add the Maven Repository

Add the JitPack repository to your project-level **build.gradle** or **settings.gradle** to include the library in your project file:

```
repositories {
    google()
    mavenCentral()
    maven { url "https://jitpack.io" }
}
```  

### Step 2: Add the Dependency

Next, include the library in your app-level **build.gradle** file. Replace x.x.x with the latest version shown below:
[![](https://jitpack.io/v/orbitalsonic/SonicInApp.svg)](https://jitpack.io/#orbitalsonic/SonicInApp)
```
implementation 'com.github.orbitalsonic:SonicInApp:x.x.x'
```

## Implementation Guide

### 1. Initialize Billing Manager

Initialize the **BillingManager** with the application `context`:

```
private val billingManager by lazy { BillingManager(context) }
```

### 2. Establish Billing Connection

Set up the connection and retrieve active purchase details:

```
val subsProductIdList = listOf("subs_product_id_1", "subs_product_id_2", "subs_product_id_3")

val productInAppConsumable = when (BuildConfig.DEBUG) {
    true -> listOf("product_id_1")
    false -> listOf("product_id_1", "product_id_2")
}
val productInAppNonConsumable = when (BuildConfig.DEBUG) {
    true -> listOf(billingManager.getDebugProductIDList())
    false -> listOf("product_id_1", "product_id_2")
}

billingManager.initialize(
    productInAppConsumable = productInAppConsumable,
    productInAppNonConsumable = productInAppNonConsumable,
    productSubscriptions = subsProductIdList,
    billingListener = object : BillingListener {
        override fun onConnectionResult(isSuccess: Boolean, message: String) {
            Log.d("BillingTAG", "Billing: initBilling: onConnectionResult: isSuccess = $isSuccess - message = $message")
        }
    
        override fun purchasesResult(purchaseDetailList: List<PurchaseDetail>) {
            if (purchaseDetailList.isEmpty()) {
                // No purchase found, reset all sharedPreferences (premium properties)
            }
            purchaseDetailList.forEachIndexed { index, purchaseDetail ->
                Log.d("BillingTAG", "Billing: initBilling: purchasesResult: $index) $purchaseDetail ")
            }
        }
    }
)

```
The `PurchaseDetail` class gives detailed information about the purchased items:

```
/**
 productId: Product Id for both inapp/subs (e.g. product_ads/product_weekly_ads)
 planId: Plan Id for subs (e.g. plan_weekly_ads)
 productTitle: Title of the Product
 planTitle: Title of the Plan
 productType: Product purchase type (e.g. InApp/Subs)
 purchaseToken: a unique token for this purchase
 purchaseTime: For subscriptions, this is the subscription signup time. It won't change after renewal.
 purchaseTimeMillis: UnixTimeStamp (starts from Jan 1, 1970)
 isAutoRenewing: Only in case of 'BillingClient.ProductType.SUBS'
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
```

### 3. Query Product Details

To observe and handle product details for both in-app purchases and subscriptions:

```
val subsProductIdList = listOf("subs_product_id_1", "subs_product_id_2", "subs_product_id_3")
val subsPlanIdList = listOf("subs-plan-id-1", "subs-plan-id-2", "subs-plan-id-3")

billingManager.productDetailsLiveData.observe(viewLifecycleOwner) { productDetailList ->
    Log.d("BillingTAG", "Billing: initObservers: $productDetailList")

    productDetailList.forEach { productDetail ->
        if (productDetail.productType == ProductType.inapp) {
            when (productDetail.productId) {
                "inapp_product_id_1" -> { /* Handle in-app product 1 */ }
                "inapp_product_id_2" -> { /* Handle in-app product 2 */ }
            }
        } else {
            when (productDetail.productId) {
                "subs_product_id_1" -> if (productDetail.planId == "subs-plan-id-1") { /* Handle plan1 subscription */ }
                "subs_product_id_2" -> if (productDetail.planId == "subs-plan-id-2") { /* Handle plan2 subscription */ }
                "subs_product_id_3" -> if (productDetail.planId == "subs-plan-id-3") { /* Handle plan3 subscription */ }
            }
        }
    }
}
```
The `ProductDetail` class contains detailed information about the products:

```
@param productId: Unique ID (Console's ID) for product
@param planId: Unique ID (Console's ID) for plan
@param productTitle: e.g. Gold Tier
@param planTitle: e.g. Weekly, Monthly, Yearly, etc
@param productType: e.g. InApp / Subs
@param currencyCode: e.g. USD, PKR, etc
@param price: e.g. Rs 750.00
@param priceAmountMicros: e.g. 750000000
@param freeTrialDays: e.g. 3, 5, 7, etc
@param billingPeriod
    - Weekly: P1W (One week)
    - Every 4 weeks: P4W (Four weeks)
    - Monthly: P1M (One month)
    - Every 2 months (Bimonthly): P2M (Two months)
    - Every 3 months (Quarterly): P3M (Three months)
    - Every 4 months: P4M (Four months)
    - Every 6 months (Semiannually): P6M (Six months)
    - Every 8 months: P8M (Eight months)
    - Yearly: P1Y (One year)

data class ProductDetail(
    var productId: String,
    var planId: String,
    var productTitle: String,
    var planTitle: String,
    var productType: ProductType,
    var currencyCode: String,
    var price: String,
    var priceAmountMicros: Long = 0,
    var freeTrialDays: Int = 0,
    var billingPeriod: String,
)
```

### 4. Handle Purchases

#### In-App Purchases

```
billingManager.makeInAppPurchase(activity, productId, object : OnPurchaseListener {
    override fun onPurchaseResult(isPurchaseSuccess: Boolean, message: String) {
        Log.d("BillingTAG", "makeInAppPurchase: $isPurchaseSuccess - $message")
    }
})

```

#### Subscription Purchases

```
  billingManager.makeSubPurchase(activity,
                    productId = "subs_product_id_1,
                    planId = "subs-plan-id-1,
                    object : OnPurchaseListener {
                        override fun onPurchaseResult(isPurchaseSuccess: Boolean, message: String) {
                            Log.d("BillingTAG", "makeSubPurchase: $isPurchaseSuccess - $message")
                        }
                    })

```

#### Updating Subscriptions

```
billingManager.updateSubPurchase(
    activity,
    oldProductId = "subs_product_id_1",
    oldPlanId = "subs_plan_id_1",
    productId = "subs_product_id_2",
    planId = "subs_plan_id_2",
    object : OnPurchaseListener {
        override fun onPurchaseResult(isPurchaseSuccess: Boolean, message: String) {
            Log.d("BillingTAG", "updateSubPurchase: $isPurchaseSuccess - $message")
        }
    }
)

billingManager.updateSubPurchase(
                    activity,
                    oldProductId = "mOldProductID",
                    productId = "New Product ID",
                    planId = "New Plan ID",
                    object : OnPurchaseListener {
                        override fun onPurchaseResult(isPurchaseSuccess: Boolean, message: String) {
                      Log.d("BillingTAG", "updateSubPurchase: $isPurchaseSuccess - $message")
                        }
                    }
                )

```
## Best Practices for Subscription IDs

### Option 1: One-to-One Mapping

For each subscription plan, use unique product and plan IDs:

- **Product ID**: `product_id_weekly`
  - **Plan ID**: `plan_id_weekly`
- **Product ID**: `product_id_monthly`
  - **Plan ID**: `plan_id_monthly`
- **Product ID**: `product_id_yearly`
  - **Plan ID**: `plan_id_yearly`

### Option 2: Multiple Plans per Product

If you're managing multiple plans under one product, store the plan ID on your server for future retrieval. This allows you to identify which plan was purchased.

For example:

- **Product ID**: `gold_product`
  - **Plan ID**: `gold-plan-weekly`
  - **Plan ID**: `gold-plan-monthly`
  - **Plan ID**: `gold-plan-yearly`

### Subscription Billing Periods

Fixed billing periods for subscriptions:

- Weekly
- Every 4 weeks
- Monthly
- Every 2 months (Bimonthly)
- Every 3 months (Quarterly)
- Every 4 months
- Every 6 months (Semiannually)
- Every 8 months
- Yearly


> [!TIP]
> Note: Use the **BillingManager** tag to observe the states

## Acknowledgements

This work has been made possible by the contribution of the [Sohaib Ahmed](https://github.com/epegasus).

## LICENSE

Copyright 2023 Engr. Muhammad Yaqoob

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
