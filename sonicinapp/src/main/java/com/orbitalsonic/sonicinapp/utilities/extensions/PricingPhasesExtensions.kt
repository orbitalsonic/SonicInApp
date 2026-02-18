package com.orbitalsonic.sonicinapp.utilities.extensions

import com.android.billingclient.api.ProductDetails.PricingPhase
import com.android.billingclient.api.ProductDetails.PricingPhases

/**
 * @Author: Muhammad Yaqoob
 * @Date: 18, February 2026
 * @Accounts
 *      -> https://github.com/orbitalsonic
 *      -> https://www.linkedin.com/in/myaqoob7
 */

internal fun PricingPhases?.originalPhase(): PricingPhase? = this?.pricingPhaseList?.firstOrNull { phase ->
    phase.priceAmountMicros > 0 && phase.billingPeriod != "P0D"
}