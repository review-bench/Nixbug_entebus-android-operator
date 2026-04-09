package com.nixbug.entebus.domain.repository

import com.nixbug.entebus.utils.network.Resource

interface OperatorAccountImageRepository {
    suspend fun fetchOperatorAccountImage(): Resource<String>
}