package com.nixbug.entebus.domain.usecase.roleandpermission

import com.nixbug.entebus.domain.repository.RoleAndPermissionRepository
import javax.inject.Inject

class CanUpdateServiceUseCase @Inject constructor(private val repository: RoleAndPermissionRepository) {
    suspend operator fun invoke(): Boolean {
        return repository.getPermission().canUpdateService
    }
}
