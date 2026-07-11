package com.emermeladas.focusreef.di

import com.emermeladas.focusreef.data.remote.NasApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * Provides the Retrofit client for the NAS REST API.
 *
 * The API is not deployed yet — [RepositoryModule] binds the mock focus
 * history, so nothing requests these dependencies at runtime today.
 */
@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    /** Placeholder — replace with the NAS address once its API is deployed. */
    private const val NAS_BASE_URL = "http://192.168.1.100:8080/api/"

    @Provides
    @Singleton
    fun provideRetrofit(): Retrofit =
        Retrofit.Builder()
            .baseUrl(NAS_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    @Provides
    @Singleton
    fun provideNasApiService(retrofit: Retrofit): NasApiService =
        retrofit.create(NasApiService::class.java)
}
