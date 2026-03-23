package com.nixbug.entebus.domain.usecase.user

import com.nixbug.entebus.domain.repository.UserRepository
import javax.inject.Inject

class ClearUserUseCase @Inject constructor(private val repository: UserRepository) {
    suspend operator fun invoke() {
        repository.clearUser()
    }
}
