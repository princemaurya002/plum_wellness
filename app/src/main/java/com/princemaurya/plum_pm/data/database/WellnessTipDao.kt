package com.princemaurya.plum_pm.data.database

import androidx.room.*
import com.princemaurya.plum_pm.data.model.WellnessTip
import kotlinx.coroutines.flow.Flow

@Dao
interface WellnessTipDao {
    @Query("SELECT * FROM wellness_tips WHERE isCurrentGeneration = 1 ORDER BY createdAt DESC")
    fun getAllTips(): Flow<List<WellnessTip>>
    
    @Query("SELECT * FROM wellness_tips WHERE isFavorite = 1 ORDER BY createdAt DESC")
    fun getFavoriteTips(): Flow<List<WellnessTip>>
    
    @Query("SELECT * FROM wellness_tips ORDER BY createdAt DESC")
    fun getAllTipsIncludingFavorites(): Flow<List<WellnessTip>>
    
    @Query("SELECT * FROM wellness_tips WHERE id = :tipId")
    suspend fun getTipById(tipId: String): WellnessTip?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTip(tip: WellnessTip)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTips(tips: List<WellnessTip>)
    
    @Update
    suspend fun updateTip(tip: WellnessTip)
    
    @Delete
    suspend fun deleteTip(tip: WellnessTip)
    
    @Query("DELETE FROM wellness_tips")
    suspend fun deleteAllTips()
    
    @Query("DELETE FROM wellness_tips WHERE isFavorite = 0")
    suspend fun deleteNonFavoriteTips()
    
    @Query("UPDATE wellness_tips SET isFavorite = :isFavorite WHERE id = :tipId")
    suspend fun updateFavoriteStatus(tipId: String, isFavorite: Boolean)
    
    @Query("UPDATE wellness_tips SET isCurrentGeneration = 0 WHERE isCurrentGeneration = 1")
    suspend fun markAllAsOldGeneration()
}
