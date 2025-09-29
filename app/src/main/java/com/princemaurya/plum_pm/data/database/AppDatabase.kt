package com.princemaurya.plum_pm.data.database

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import android.content.Context
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.princemaurya.plum_pm.data.model.UserProfile
import com.princemaurya.plum_pm.data.model.WellnessTip

@Database(
    entities = [UserProfile::class, WellnessTip::class],
    version = 3,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userProfileDao(): UserProfileDao
    abstract fun wellnessTipDao(): WellnessTipDao
    
    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null
        
        // Migration from version 2 to 3: add `isCurrentGeneration` column to `wellness_tips`
        private val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // Add the new column with NOT NULL and default value = 1 (true)
                database.execSQL(
                    "ALTER TABLE wellness_tips ADD COLUMN isCurrentGeneration INTEGER NOT NULL DEFAULT 1"
                )
            }
        }
        
        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "wellness_database"
                )
                    .addMigrations(MIGRATION_2_3)
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
