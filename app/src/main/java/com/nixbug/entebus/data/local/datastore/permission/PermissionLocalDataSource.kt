package com.nixbug.entebus.data.local.datastore.permission

import com.google.gson.Gson
import com.nixbug.entebus.domain.model.OperatorPermission
import kotlinx.coroutines.flow.firstOrNull
import javax.inject.Inject

class PermissionLocalDataSource @Inject constructor(
    private val permissionDataStore: PermissionLocalDataStore,
    private val gson: Gson
) {
    suspend fun savePermission(permission: OperatorPermission) {
        val json = gson.toJson(permission)
        permissionDataStore.save(json)
    }

    suspend fun getPermission(): OperatorPermission? {
        val json = permissionDataStore.get().firstOrNull()
        return json?.let { gson.fromJson(it, OperatorPermission::class.java) }
    }

    suspend fun clearPermission() {
        permissionDataStore.clear()
    }
}
