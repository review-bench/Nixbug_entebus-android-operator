package com.nixbug.entebus.domain.repository

import com.nixbug.entebus.domain.model.OperatorPermission
import com.nixbug.entebus.utils.network.Resource

interface RoleAndPermissionRepository {

    suspend fun fetchRoleAndPermission(operatorId: Int): Resource<OperatorPermission>

    suspend fun getPermission(): OperatorPermission
}
