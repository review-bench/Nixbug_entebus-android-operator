package com.nixbug.entebus.domain.model.local


sealed class SyncEvent {

    data object Started : SyncEvent()

    data object Success : SyncEvent()

    data class Failed(val message: String) : SyncEvent()
}
