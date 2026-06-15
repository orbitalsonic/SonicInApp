package com.orbitalsonic.sonicinapppurchase

import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.orbitalsonic.sonicinapp.BillingManager
import com.orbitalsonic.sonicinapp.data.entities.product.ProductDetail
import com.orbitalsonic.sonicinapp.data.entities.purchase.PurchaseDetail
import com.orbitalsonic.sonicinapp.presentation.interfaces.BillingConnectionListener
import com.orbitalsonic.sonicinapp.presentation.interfaces.BillingProductDetailsListener
import com.orbitalsonic.sonicinapp.presentation.interfaces.BillingPurchaseHistoryListener
import com.orbitalsonic.sonicinapp.presentation.interfaces.BillingPurchaseListener

class MainActivity : AppCompatActivity() {

    val TAG = "TestBillingTag"

    private val billingManager by lazy { BillingManager(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initBilling()

        findViewById<Button>(R.id.btn_purchase).setOnClickListener { onPurchaseClick() }
    }

    private fun initBilling() {
        billingManager
            .setNonConsumables(emptyList())
            .setConsumables(emptyList())
            .setSubscriptions(emptyList())
            .setListener(object : BillingConnectionListener {
                override fun onBillingClientConnected(isSuccess: Boolean, message: String) {
                    Log.d(TAG, "onBillingClientConnected: isSuccess: $isSuccess, message: $message")
                    fetchData(billingManager)
                }
            })
            .startConnection()
    }

    private fun fetchData(billingManager: BillingManager) {
        billingManager.fetchPurchaseHistory(object : BillingPurchaseHistoryListener {
            override fun onError(message: String) {
                Log.e(TAG, "fetchPurchaseHistory: Error: $message")
            }

            override fun onSuccess(purchaseDetails: List<PurchaseDetail>) {
                Log.d(TAG, "fetchPurchaseHistory: onSuccess: $purchaseDetails")
            }
        })

        billingManager.fetchProductDetails(object : BillingProductDetailsListener {
            override fun onError(message: String) {
                Log.e(TAG, "fetchProductDetails: Error: $message")
            }

            override fun onSuccess(productDetails: List<ProductDetail>) {
                Log.d(TAG, "fetchProductDetails: onSuccess: $productDetails")
                productDetails.forEach { _ ->
                    //Log.d(TAG, "fetchProductDetails: onSuccess: productDetail: $it")
                }
            }
        })

        billingManager.getProductDetail("", "", object : BillingProductDetailsListener {
            override fun onError(message: String) {
                Log.e(TAG, "getProductDetail: Error: $message")
            }

            override fun onSuccess(productDetails: List<ProductDetail>) {
                Log.d(TAG, "fetchProductDetail: onSuccess: $productDetails")
                productDetails.forEach {
                    Log.d(TAG, "fetchProductDetail: onSuccess: productDetail: $it")
                }
            }
        })
    }

    private fun onPurchaseClick() {
        // In-App
        billingManager.purchaseInApp(this, "android.test.purchased", purchaseListener)

        // Subscription
        //billingManager.purchaseSubs(this, APP_SUB_WEEKLY, SUB_PLAN_WEEKLY, purchaseListener)
    }

    private val purchaseListener = object : BillingPurchaseListener {
        override fun onPurchaseResult(message: String) {
            Log.d(TAG, "purchaseListener: onPurchaseResult: message: $message")
        }

        override fun onError(message: String) {
            Log.e(TAG, "purchaseListener: onError: message: $message")
        }
    }
}