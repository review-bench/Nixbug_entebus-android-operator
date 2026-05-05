package com.nixbug.entebus.di

import com.nixbug.entebus.data.rhino.RhinoFareCalculator
import com.nixbug.entebus.domain.repository.FareCalculatorRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class FareModule {

    @Binds
    @Singleton
    abstract fun bindFareCalculator(
        impl: RhinoFareCalculator
    ): FareCalculatorRepository
}
