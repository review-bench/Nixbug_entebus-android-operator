package com.nixbug.entebus.di

import com.google.gson.Gson
import com.nixbug.entebus.BuildConfig
import com.nixbug.entebus.data.interceptor.AuthInterceptor
import com.nixbug.entebus.data.remote.operator.api.TokenApi
import com.nixbug.entebus.data.remote.publicapi.api.CompanyApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import javax.inject.Singleton
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @OperatorApi
    @Provides
    @Singleton
    fun provideOperatorRetrofit(gson: Gson, authInterceptor: AuthInterceptor): Retrofit {
        val client = OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .build()
        return Retrofit.Builder()
            .baseUrl(BuildConfig.OPERATOR_BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }

    @PublicApi
    @Provides
    @Singleton
    fun providePublicRetrofit(gson: Gson): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BuildConfig.PUBLIC_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }

    @Provides
    fun provideTokenApi(@OperatorApi retrofit: Retrofit): TokenApi {
        return retrofit.create(TokenApi::class.java)
    }

    @Provides
    fun provideCompanyApi(@PublicApi retrofit: Retrofit): CompanyApi {
        return retrofit.create(CompanyApi::class.java)
    }

}
