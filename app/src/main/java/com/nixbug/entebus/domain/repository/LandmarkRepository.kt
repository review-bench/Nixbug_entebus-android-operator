package com.nixbug.entebus.domain.repository

import com.nixbug.entebus.domain.model.LandmarkModel
import com.nixbug.entebus.utils.network.Resource

interface LandmarkRepository {
    suspend fun getLandmarksByIds(ids: List<Int>): Resource<List<LandmarkModel>>
}
