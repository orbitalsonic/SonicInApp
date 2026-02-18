package com.orbitalsonic.sonicinapp.utilities.extensions

/**
 * @Author: Muhammad Yaqoob
 * @Date: 18, February 2026
 * @Accounts
 *      -> https://github.com/orbitalsonic
 *      -> https://www.linkedin.com/in/myaqoob7
 */

/* ——— List extension to overwrite contents ——— */
internal fun <T> MutableList<T>.setAll(newItems: List<T>) {
    clear(); addAll(newItems)
}