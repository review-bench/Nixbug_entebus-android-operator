package com.nixbug.entebus.data.repository

import com.nixbug.entebus.data.local.datastore.user.UserLocalDataSource
import com.nixbug.entebus.domain.model.local.UserModel
import com.nixbug.entebus.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val localDataSource: UserLocalDataSource
) : UserRepository {

    override suspend fun saveUser(
        companyId: Int,
        companyName: String,
        username: String
    ) {
        localDataSource.saveUser(companyId, companyName, username)
    }

    override fun getUser(): Flow<UserModel> {
        return localDataSource.getUser()
    }

    override suspend fun clearUser() {
        localDataSource.clearUser()
    }
}
