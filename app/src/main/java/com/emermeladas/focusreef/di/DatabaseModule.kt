package com.emermeladas.focusreef.di

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
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
            .addMigrations(MIGRATION_1_2)
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

    /**
     * v1 → v2: the decorations table. The SQL must match what Room generates
     * for [com.emermeladas.focusreef.data.local.entities.DecorationEntity]
     * exactly (exportSchema is off, so there is no JSON to validate against).
     */
    private val MIGRATION_1_2 = object : Migration(1, 2) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL(
                """
                CREATE TABLE IF NOT EXISTS `decorations` (
                    `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                    `speciesName` TEXT NOT NULL,
                    `tankId` INTEGER NOT NULL,
                    `xBias` REAL NOT NULL,
                    `yBias` REAL NOT NULL
                )
                """.trimIndent(),
            )
            db.execSQL(
                "CREATE INDEX IF NOT EXISTS `index_decorations_tankId` ON `decorations` (`tankId`)",
            )
        }
    }
}
