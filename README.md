[![](https://jitpack.io/v/orbitalsonic/SonicInApp.svg)](https://jitpack.io/#orbitalsonic/SonicInApp)
# SonicInApp

SonicInApp is a [Google Play Billing](https://developer.android.com/google/play/billing/integrate) library that demonstrates InApp purchase in your Android application

## Getting Started

### Step 1

Add maven repository in project level build.gradle or in latest project setting.gradle file
```
    repositories {
        google()
        mavenCentral()
        maven { url "https://jitpack.io" }
    }
```  

### Step 2

Add SonicInApp dependencies in App level build.gradle. Check Latest Version
```
    dependencies {
             implementation 'com.github.orbitalsonic:SonicInApp:1.2.4'
    }
```  

### Step 3

Declare BillingManger Variable, "this" can be of Application Context

also declare your original productId

```
    private val billingManager by lazy { BillingManager(this) }
    private val productId:String = "Paste your original Product ID"
```  

#### Billing Initializaiton

Get debugging ids for testing using "getDebugProductIDList()" method

```
    if (BuildConfig.DEBUG) {
            billingManager.startConnection(billingManager.getDebugProductIDList()) { isConnectionEstablished, alreadyPurchased, message ->
                showMessage(message)
                if (alreadyPurchased) {
                    // Save settings for purchased product
                }
            }
        } else {
            billingManager.startConnection(listOf(productId)) { isConnectionEstablished, alreadyPurchased, message ->
                showMessage(message)
                if (alreadyPurchased) {
                    // Save settings for purchased product
                }
            }
        }

```
#### Billing State Observer

observe the states of establishing connections

```
State.billingState.observe(this) {
            Log.d("BillingManager", "initObserver: $it")
        }
```
#### Purchasing InApp

"this" parameter Must be a reference of an Activity

```
   billingManager.makePurchase(this) { isSuccess, message ->
            showMessage(message)
        }
```

#### Old Purchase

```
    if (BuildConfig.DEBUG) {
            billingManager.startOldPurchaseConnection(billingManager.getDebugProductIDList()) { isConnectionEstablished, alreadyPurchased, message ->
                if (alreadyPurchased) {
                    // Save settings for purchased product
                }
            }
        } else {
            billingManager.startOldPurchaseConnection(listOf(productId)) { isConnectionEstablished, alreadyPurchased, message ->
                if (alreadyPurchased) {
                    // Save settings for purchased product
                }
            }
        }

```

#### Note
Here is the list of debuging product ids, can be test every state of billing

```
"android.test.purchased"
"android.test.item_unavailable"
"android.test.refunded"
"android.test.canceled"
```

can be found here

```
billingManager.getDebugProductIDList()
billingManager.getDebugProductIDsList()
```

# Acknowledgements

This work has been made possible by contribution from the [Sohaib Ahmed](https://github.com/epegasus).

# LICENSE

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

