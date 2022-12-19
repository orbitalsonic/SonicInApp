package com.orbitalsonic.sonicinapppurchase

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.orbitalsonic.sonicinapp.BillingManager

class MainActivity : AppCompatActivity() {


    private val billingManager by lazy { BillingManager(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initBilling()
    }

    private fun initBilling() {
        /*if (BuildConfig.DEBUG) {
            billingManager.startConnection(billingManager.getDebugProductIDList()) { connectionResult, message ->
               // binding.mbMakePurchase.isEnabled = connectionResult
                Toast.makeText(this, message, Toast.LENGTH_LONG).show()
            }
        } else {
            billingManager.startConnection(listOf(packageName)) { connectionResult, message ->
               // binding.mbMakePurchase.isEnabled = connectionResult
                Toast.makeText(this, message, Toast.LENGTH_LONG).show()
            }
        }*/
    }


    private fun onPurchaseClick() {
        billingManager.makePurchase { isSuccess, message ->
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
           //binding.mbMakePurchase.isEnabled = !isSuccess
        }
    }

}