package com.orbitalsonic.sonicinapppurchase

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.orbitalsonic.sonicinapp.BillingManager
import com.orbitalsonic.sonicinapp.dataClasses.PurchaseDetail
import com.orbitalsonic.sonicinapp.interfaces.BillingListener
import com.orbitalsonic.sonicinapp.interfaces.OnPurchaseListener

class MainActivity : AppCompatActivity() {

    val TAG = "TestingTag"

    private val billingManager by lazy { BillingManager(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initBilling()
        initObserver()

        findViewById<Button>(R.id.btn_purchase).setOnClickListener { onPurchaseClick() }
    }

    private fun initBilling() {
        val productInAppConsumable = when (BuildConfig.DEBUG) {
            true -> listOf("test")
            false -> listOf("abc", "def")
        }
        val productInAppNonConsumable = when (BuildConfig.DEBUG) {
            true -> listOf(billingManager.getDebugProductIDList())
            false -> listOf(packageName)
        }

        billingManager.initialize(
            productInAppConsumable = productInAppConsumable,
            productInAppNonConsumable = productInAppNonConsumable,
            productSubscriptions = listOf("Bronze", "Silver", "Gold"),
            billingListener = billingListener
        )
    }

    private val billingListener = object : BillingListener {
        override fun onConnectionResult(isSuccess: Boolean, message: String) {
            Log.d(TAG, "onConnectionResult: isSuccess: $isSuccess, message: $message")
            if (!isSuccess) {
                proceedApp()
            }
        }

        override fun purchasesResult(purchaseDetailList: List<PurchaseDetail>) {
            Log.d(TAG, "onConnectionResult: purchaseDetailList: $purchaseDetailList")
            //purchaseDetailList[0].productType
            proceedApp()
        }
    }

    private fun initObserver() {
        billingManager.productDetailsLiveData.observe(this) { productDetailList ->
            Log.d(TAG, "initNewObserver: --------------------------------------")
            productDetailList.forEach { productDetail ->
                Log.d(TAG, "---: $productDetail")
            }
        }
    }

    private fun proceedApp() {
        // your code here...
    }

    private fun onPurchaseClick() {
        // In-App
        billingManager.makeInAppPurchase(this, "test", onPurchaseListener)

        // Subscription
        //billingManager.makeSubPurchase(this, "product_abc", "plan_abc", onPurchaseListener)
    }

    private val onPurchaseListener = object : OnPurchaseListener {
        override fun onPurchaseResult(isPurchaseSuccess: Boolean, message: String) {
            showMessage(message)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        billingManager.destroyBilling()
    }

    private fun showMessage(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}