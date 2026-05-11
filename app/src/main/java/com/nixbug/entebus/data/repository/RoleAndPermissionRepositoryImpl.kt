package com.nixbug.entebus.data.repository

import com.nixbug.entebus.data.local.datastore.permission.PermissionLocalDataSource
import com.nixbug.entebus.data.mapper.toOperatorPermission
import com.nixbug.entebus.data.remote.operator.api.OperatorRoleApi
import com.nixbug.entebus.data.remote.operator.api.OperatorRoleMapApi
import com.nixbug.entebus.data.remote.utils.apiHelper
import com.nixbug.entebus.domain.model.OperatorPermission
import com.nixbug.entebus.domain.repository.RoleAndPermissionRepository
import com.nixbug.entebus.utils.network.Resource
import javax.inject.Inject

class RoleAndPermissionRepositoryImpl @Inject constructor(
    private val roleMapApi: OperatorRoleMapApi,
    private val roleApi: OperatorRoleApi,
    private val permissionLocalDataSource: PermissionLocalDataSource
) : RoleAndPermissionRepository {

    override suspend fun fetchRoleAndPermission(operatorId: Int): Resource<OperatorPermission> {

        val roleMapResult =
            apiHelper(apiCall = { roleMapApi.getRoleMap(operatorId = operatorId) }, mapper = { it })

        when (roleMapResult) {

            is Resource.Success -> {
                val roleMap = roleMapResult.data?.firstOrNull()

                if (roleMap == null) {
                    val defaultPermission = OperatorPermission()
                    permissionLocalDataSource.savePermission(defaultPermission)
                    return Resource.Success(defaultPermission)
                }

                val roleId = roleMap.roleId

                val roleResult = apiHelper(
                    apiCall = {
                        roleApi.fetchRoleOperatorCompanyRoleGet(id = roleId)
                    },
                    mapper = { roles ->
                        roles.firstOrNull()?.toOperatorPermission() ?: OperatorPermission()
                    }
                )
                if (roleResult is Resource.Success) {
                    roleResult.data?.let {
                        permissionLocalDataSource.savePermission(it)
                    }
                }
                return roleResult
            }

            is Resource.Empty -> {
                return Resource.Success(OperatorPermission())
            }

            is Resource.Error -> {
                return Resource.Error(message = roleMapResult.message)
            }

            else -> {
                return Resource.Error(message = "Unexpected error")
            }
        }
    }

    override suspend fun getPermission(): OperatorPermission {
        return permissionLocalDataSource.getPermission() ?: OperatorPermission()
    }

}
