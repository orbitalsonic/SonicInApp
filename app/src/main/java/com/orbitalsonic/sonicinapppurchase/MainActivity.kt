package com.orbitalsonic.sonicinapppurchase

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.orbitalsonic.sonicinapp.BillingManager
import com.orbitalsonic.sonicinapp.status.State

class MainActivity : AppCompatActivity() {
    private val billingManager by lazy { BillingManager(this) }
    private lateinit var tvTitle:TextView

    // mostly people use package name in their
    private val productId:String = "Paste your original Product ID"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initBilling()
        initObserver()

        tvTitle = findViewById(R.id.tv_title)
        findViewById<Button>(R.id.btn_purchase).setOnClickListener {
            onPurchaseClick()
        }
    }

    private fun initObserver() {
        State.billingState.observe(this) {
            Log.d("BillingManager", "initObserver: $it")
            tvTitle.text = it.toString()
        }
    }

    private fun initBilling() {
        if (BuildConfig.DEBUG) {
            billingManager.startConnection(billingManager.getDebugProductIDList()){ isConnectionEstablished, message ->
                showMessage(message)
            }
        } else {
            billingManager.startConnection(listOf(productId)) { isConnectionEstablished, message ->
                showMessage(message)
            }
        }
    }


    private fun onPurchaseClick() {
        billingManager.makePurchase { isSuccess, message ->
            showMessage(message)
        }
    }

    private fun showMessage(message:String){
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

}