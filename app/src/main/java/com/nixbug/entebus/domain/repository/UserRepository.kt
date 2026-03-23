package com.nixbug.entebus.domain.repository

import com.nixbug.entebus.domain.model.UserModel
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    suspend fun saveUser(companyId: Int, companyName: String, username: String)
    fun getUser(): Flow<UserModel>
    suspend fun clearUser()
}