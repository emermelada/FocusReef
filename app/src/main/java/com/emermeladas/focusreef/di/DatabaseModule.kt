package com.emermeladas.focusreef.di

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.emermeladas.focusreef.data.local.FocusReefDatabase
import com.emermeladas.focusreef.data.local.daos.AquariumDao
import com.emermeladas.focusreef.data.local.daos.PurchaseDao
import com.emermeladas.focusreef.utils.GameConfig
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Provides the Room database and its DAOs.
 */
@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): FocusReefDatabase =
        Room.databaseBuilder(context, FocusReefDatabase::class.java, FocusReefDatabase.NAME)
            .addCallback(SeedStarterTank)
            .build()

    @Provides
    fun provideAquariumDao(database: FocusReefDatabase): AquariumDao = database.aquariumDao()

    @Provides
    fun providePurchaseDao(database: FocusReefDatabase): PurchaseDao = database.purchaseDao()

    /**
     * Seeds the free starter tank the first time the database is created, so
     * the player always begins with one tank without paying for it.
     */
    private object SeedStarterTank : RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            db.execSQL(
                "INSERT INTO tanks (name) VALUES (?)",
                arrayOf(GameConfig.STARTER_TANK_NAME),
            )
        }
    }
}
