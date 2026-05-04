package com.nixbug.entebus.domain.model

data class ServiceUpdate(
    val status: ServicePatchStatus,
    val remark: String? = null
)

enum class ServicePatchStatus {
    RESTARTED,
    FINISHED
}
