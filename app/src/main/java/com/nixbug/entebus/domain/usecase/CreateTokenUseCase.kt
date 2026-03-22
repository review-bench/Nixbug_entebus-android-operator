package com.nixbug.entebus.domain.usecase

import com.nixbug.entebus.domain.model.CreateTokenModel
import com.nixbug.entebus.domain.repository.TokenRepository
import com.nixbug.entebus.utils.network.Resource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class CreateTokenUseCase @Inject constructor(private val repository: TokenRepository) {

    suspend operator fun invoke(
        companyId: Int,
        username: String,
        password: String,
        clientDetails: String
    ): Resource<CreateTokenModel> =
        repository.createToken(companyId, username, password, clientDetails)
}

class SaveTokenUseCase @Inject constructor(
    private val repository: TokenRepository
) {
    suspend operator fun invoke(token: CreateTokenModel) {
        repository.saveToken(token)
    }
}

class GetTokenUseCase @Inject constructor(
    private val repository: TokenRepository
) {
    operator fun invoke(): Flow<CreateTokenModel?> {
        return repository.getToken()
    }
}

class ClearTokenUseCase @Inject constructor(
    private val repository: TokenRepository
) {
    suspend operator fun invoke() {
        repository.clearToken()
    }
}