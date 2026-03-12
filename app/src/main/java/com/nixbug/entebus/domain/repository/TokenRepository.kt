package com.nixbug.entebus.domain.repository

import com.nixbug.entebus.domain.model.CreateTokenModel
import com.nixbug.entebus.utils.network.Resource

interface TokenRepository {
    suspend fun createToken(
        companyId: Int,
        username: String,
        password: String,
        clientDetails: String
    ): Resource<CreateTokenModel>
}
