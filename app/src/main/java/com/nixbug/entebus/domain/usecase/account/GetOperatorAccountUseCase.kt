package com.nixbug.entebus.domain.usecase.account

import com.nixbug.entebus.domain.model.OperatorAccountModel
import com.nixbug.entebus.domain.repository.OperatorAccountRepository
import com.nixbug.entebus.utils.network.Resource
import javax.inject.Inject


class GetOperatorAccountUseCase @Inject constructor(private val repository: OperatorAccountRepository) {
    suspend operator fun invoke(): Resource<List<OperatorAccountModel>> =
        repository.fetchOperatorAccount()
}