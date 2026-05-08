package com.nixbug.entebus.utils

import java.util.Locale

fun Double.toCurrency(): String {
    return String.format(
        Locale.US,
        "%.2f",
        this
    )
}
