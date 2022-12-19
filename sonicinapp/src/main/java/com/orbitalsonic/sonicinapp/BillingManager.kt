package com.orbitalsonic.sonicinapp

import android.app.Activity

import com.orbitalsonic.sonicinapp.helper.BillingHelper

/**
 * @param activity: Must be a reference of an Activity
 */
class BillingManager(private val activity: Activity) : BillingHelper(activity) {

    override fun startConnection(productIdsList: List<String>, callback: (isConnectionEstablished: Boolean, message: String) -> Unit) = startBillingConnection(productIdsList, callback)

    fun makePurchase(callback: (isPurchased: Boolean, message: String) -> Unit) = purchase(callback)

}
