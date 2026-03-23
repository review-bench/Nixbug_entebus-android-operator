package com.nixbug.entebus.domain.usecase.user

import com.nixbug.entebus.domain.model.UserModel
import com.nixbug.entebus.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetUserUseCase @Inject constructor(private val repository: UserRepository) {
    operator fun invoke(): Flow<UserModel?> {
        return repository.getUser()
    }
}