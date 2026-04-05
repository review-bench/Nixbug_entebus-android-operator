package com.nixbug.entebus.domain.usecase.account

import com.nixbug.entebus.domain.model.OperatorAccountModel
import com.nixbug.entebus.domain.repository.OperatorAccountRepository
import com.nixbug.entebus.utils.network.Resource
import okhttp3.RequestBody
import javax.inject.Inject

class UpdateOperatorAccountUseCase @Inject constructor(
    private val repository: OperatorAccountRepository
) {

    suspend operator fun invoke(
        body: RequestBody
    ): Resource<OperatorAccountModel> {
        return repository.updateOperatorAccount(body)
    }
}