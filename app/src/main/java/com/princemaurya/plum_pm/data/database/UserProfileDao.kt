package com.princemaurya.plum_pm.data.database

import androidx.room.*
import com.princemaurya.plum_pm.data.model.UserProfile
import kotlinx.coroutines.flow.Flow

@Dao
interface UserProfileDao {
    @Query("SELECT * FROM user_profiles LIMIT 1")
    fun getUserProfile(): Flow<UserProfile?>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUserProfile(userProfile: UserProfile)
    
    @Update
    suspend fun updateUserProfile(userProfile: UserProfile)
    
    @Delete
    suspend fun deleteUserProfile(userProfile: UserProfile)
    
    @Query("DELETE FROM user_profiles")
    suspend fun deleteAllUserProfiles()
}
