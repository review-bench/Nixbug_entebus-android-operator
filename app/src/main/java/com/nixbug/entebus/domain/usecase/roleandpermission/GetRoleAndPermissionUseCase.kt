package com.nixbug.entebus.domain.usecase.roleandpermission

import com.nixbug.entebus.domain.model.OperatorPermission
import com.nixbug.entebus.domain.repository.RoleAndPermissionRepository
import com.nixbug.entebus.utils.network.Resource
import javax.inject.Inject

class GetRoleAndPermissionUseCase @Inject constructor(private val repository: RoleAndPermissionRepository) {
    suspend operator fun invoke(operatorId: Int): Resource<OperatorPermission> {
        return repository.fetchRoleAndPermission(operatorId)
    }
}
