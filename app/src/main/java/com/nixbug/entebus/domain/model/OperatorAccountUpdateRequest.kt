package com.nixbug.entebus.domain.model

data class OperatorAccountUpdateRequest(
    val fullName: FieldUpdate<String> = unchanged(),
    val emailId: FieldUpdate<String> = unchanged(),
    val phoneNumber: FieldUpdate<String> = unchanged(),
    val gender: FieldUpdate<Int> = unchanged()
) {
    fun hasChanges(): Boolean {
        return listOf(fullName, emailId, phoneNumber, gender)
            .any { it is FieldUpdate.Value }
    }
}

sealed class FieldUpdate<out T> {
    data object Unchanged : FieldUpdate<Nothing>()
    data class Value<T>(val value: T?) : FieldUpdate<T>()
}

@Suppress("UNCHECKED_CAST")
fun <T> unchanged(): FieldUpdate<T> {
    return FieldUpdate.Unchanged as FieldUpdate<T>
}
