package com.nixbug.entebus.domain.usecase.user

import com.nixbug.entebus.domain.repository.UserRepository
import javax.inject.Inject

class SaveUserUseCase @Inject constructor(
    private val repository: UserRepository
) {
    suspend operator fun invoke(
        companyId: Int,
        companyName: String,
        username: String
    ) {
        repository.saveUser(companyId, companyName, username)
    }
}