package com.emermeladas.focusreef.di

import com.emermeladas.focusreef.data.repositories.AquariumRepository
import com.emermeladas.focusreef.data.repositories.AquariumRepositoryImpl
import com.emermeladas.focusreef.data.repositories.FocusHistoryRepository
import com.emermeladas.focusreef.data.repositories.MockFocusHistoryRepository
import com.emermeladas.focusreef.data.repositories.ProgressionRepository
import com.emermeladas.focusreef.data.repositories.ProgressionRepositoryImpl
import com.emermeladas.focusreef.data.repositories.WalletRepository
import com.emermeladas.focusreef.data.repositories.WalletRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Binds repository interfaces to their active implementations.
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    /**
     * Focus history source. Currently the MOCK — swap to
     * [com.emermeladas.focusreef.data.repositories.RemoteFocusHistoryRepository]
     * when the NAS REST API is deployed.
     */
    @Binds
    @Singleton
    abstract fun bindFocusHistoryRepository(
        impl: MockFocusHistoryRepository,
    ): FocusHistoryRepository

    @Binds
    @Singleton
    abstract fun bindAquariumRepository(
        impl: AquariumRepositoryImpl,
    ): AquariumRepository

    @Binds
    @Singleton
    abstract fun bindWalletRepository(
        impl: WalletRepositoryImpl,
    ): WalletRepository

    @Binds
    @Singleton
    abstract fun bindProgressionRepository(
        impl: ProgressionRepositoryImpl,
    ): ProgressionRepository
}
