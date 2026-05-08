package com.nixbug.entebus.utils

fun String.titleCaseFirstChar(): String {
    return replaceFirstChar { firstChar ->
        if (firstChar.isLowerCase()) {
            firstChar.titlecase()
        } else {
            firstChar.toString()
        }
    }
}
