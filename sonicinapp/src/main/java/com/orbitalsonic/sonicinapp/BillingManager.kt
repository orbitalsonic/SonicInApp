package com.orbitalsonic.sonicinapp

import android.app.Activity
import android.content.Context
import com.orbitalsonic.sonicinapp.controller.BillingController
import com.orbitalsonic.sonicinapp.interfaces.BillingListener
import com.orbitalsonic.sonicinapp.interfaces.OnPurchaseListener

/**
 * @Author: Muhammad Yaqoob
 * @Date: 01,October,2024.
 * @Accounts
 *      -> https://github.com/orbitalsonic
 *      -> https://www.linkedin.com/in/myaqoob7
 */

/**
 * @param context: Context can be of Application class
 */

class BillingManager(private val context: Context) : BillingController(context) {

    /**
     *  @param productInAppConsumable: Pass list of in-app product's ID (consumable) or pass emptyList
     *  @param productInAppNonConsumable: Pass list of in-app product's ID
     *  @param productSubscriptions: Pass list of subscription product's ID
     */

    fun initialize(
        productInAppConsumable: List<String>,
        productInAppNonConsumable: List<String>,
        productSubscriptions: List<String>,
        billingListener: BillingListener? = null
    ) {
        startBillingConnection(
            userInAppConsumable = productInAppConsumable,
            userInAppNonConsumable = productInAppNonConsumable,
            userSubsPurchases = productSubscriptions,
            billingListener = billingListener
        )
    }

    fun makeInAppPurchase(activity: Activity?, productId: String, onPurchaseListener: OnPurchaseListener) {
        makePurchaseInApp(activity = activity, productId = productId, onPurchaseListener = onPurchaseListener)
    }

    fun makeSubPurchase(activity: Activity?, productId: String, planId: String, onPurchaseListener: OnPurchaseListener) {
        makePurchaseSub(activity = activity, productId = productId, planId = planId, onPurchaseListener = onPurchaseListener)
    }

    fun updateSubPurchase(activity: Activity?, oldProductId: String, productId: String, planId: String, onPurchaseListener: OnPurchaseListener) {
        updatePurchaseSub(activity = activity, oldProductId = oldProductId, productId = productId, planId = planId, onPurchaseListener = onPurchaseListener)
    }

    fun destroyBilling() = cleanBilling()

    companion object {
        const val TAG = "BillingManager"
    }
}