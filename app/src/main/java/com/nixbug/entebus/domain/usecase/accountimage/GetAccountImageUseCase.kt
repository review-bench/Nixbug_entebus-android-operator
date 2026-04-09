package com.nixbug.entebus.domain.usecase.accountimage

import com.nixbug.entebus.domain.repository.OperatorAccountImageRepository
import com.nixbug.entebus.utils.network.Resource
import javax.inject.Inject

class GetAccountImageUseCase @Inject constructor(private val repository: OperatorAccountImageRepository) {
    suspend operator fun invoke(): Resource<String> =
        repository.fetchOperatorAccountImage()
}