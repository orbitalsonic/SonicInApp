package com.orbitalsonic.sonicinapp.utilities.extensions

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * @Author: Muhammad Yaqoob
 * @Date: 18, February 2026
 * @Accounts
 *      -> https://github.com/orbitalsonic
 *      -> https://www.linkedin.com/in/myaqoob7
 */

internal fun Long.toFormattedDate(pattern: String = "MMM dd, yyyy", locale: Locale = Locale.getDefault()): String {
    val formatter = SimpleDateFormat(pattern, locale)
    return formatter.format(Date(this))
}