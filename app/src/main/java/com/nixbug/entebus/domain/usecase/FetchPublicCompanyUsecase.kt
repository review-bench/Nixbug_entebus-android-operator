package com.nixbug.entebus.domain.usecase

import com.nixbug.entebus.domain.model.PublicCompanyModel
import com.nixbug.entebus.domain.repository.PublicCompanyRepository
import com.nixbug.entebus.utils.network.Resource
import javax.inject.Inject

class FetchPublicCompanyUseCase @Inject constructor(private val repository: PublicCompanyRepository) {
    suspend operator fun invoke(
        location: String? = null,
        name: String? = null,
        offset: Int,
        limit: Int
    ): Resource<List<PublicCompanyModel>> =
        repository.fetchPublicCompany(location, name, offset, limit)
}
