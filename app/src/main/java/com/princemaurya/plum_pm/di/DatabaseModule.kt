package com.princemaurya.plum_pm.di

import android.content.Context
import androidx.room.Room
import com.princemaurya.plum_pm.data.database.AppDatabase
import com.princemaurya.plum_pm.data.database.UserProfileDao
import com.princemaurya.plum_pm.data.database.WellnessTipDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    
    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context.applicationContext,
            AppDatabase::class.java,
            "wellness_database"
        )
        .fallbackToDestructiveMigration() // For development - in production use proper migrations
        .build()
    }
    
    @Provides
    fun provideUserProfileDao(database: AppDatabase): UserProfileDao {
        return database.userProfileDao()
    }
    
    @Provides
    fun provideWellnessTipDao(database: AppDatabase): WellnessTipDao {
        return database.wellnessTipDao()
    }
}
