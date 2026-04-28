package com.nixbug.entebus.data.local.datastore.user

import com.nixbug.entebus.domain.model.local.UserModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class UserLocalDataSource @Inject constructor(
    private val userDataStore: UserLocalDataStore
) {

    suspend fun saveUser(
        companyId: Int,
        companyName: String,
        username: String
    ) {
        userDataStore.save(companyId, companyName, username)
    }

    fun getUser(): Flow<UserModel> {
        return userDataStore.get().map { prefs ->
            UserModel(
                companyId = prefs.companyId,
                companyName = prefs.companyName,
                username = prefs.username
            )
        }
    }

    suspend fun clearUser() {
        userDataStore.clear()
    }
}
