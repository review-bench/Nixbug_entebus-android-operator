package com.nixbug.entebus.domain.model

data class LandmarkModel(
    val id: Int,
    val name: String,
    val aliasNames: List<String>?
)
