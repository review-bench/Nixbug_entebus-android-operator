package com.nixbug.entebus.data.repository

import com.nixbug.entebus.data.mapper.toLandmarkDomain
import com.nixbug.entebus.data.remote.operator.api.OperatorLandmarkApi
import com.nixbug.entebus.data.remote.utils.apiHelper
import com.nixbug.entebus.domain.repository.LandmarkRepository
import com.nixbug.entebus.utils.network.Resource
import com.nixbug.entebus.domain.model.LandmarkModel
import javax.inject.Inject

class LandmarkRepositoryImpl @Inject constructor(
    private val landmarkApi: OperatorLandmarkApi
) : LandmarkRepository {

    override suspend fun getLandmarksByIds(ids: List<Int>): Resource<List<LandmarkModel>> {
        return apiHelper(
            apiCall = {
                landmarkApi.getLandmark(idList = ids)
            },
            mapper = { response ->
                response.map { it.toLandmarkDomain() }
            }
        )
    }
}
