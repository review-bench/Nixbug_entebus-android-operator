package com.nixbug.entebus.domain.usecase.uimode

import com.nixbug.entebus.domain.model.local.UiMode
import com.nixbug.entebus.domain.repository.UiPreferenceRepository
import javax.inject.Inject

class GetUiModeUseCase @Inject constructor(private val repository: UiPreferenceRepository) {
    suspend operator fun invoke(): UiMode {
        return repository.getUiMode()
    }
}
