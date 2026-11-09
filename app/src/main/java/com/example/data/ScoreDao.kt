package com.example.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ScoreDao {
  @Query("SELECT * FROM arcade_scores ORDER BY score DESC, lines DESC, timestamp ASC LIMIT :limit")
  fun getTopScores(limit: Int = 50): Flow<List<ScoreEntity>>

  @Query("SELECT MAX(score) FROM arcade_scores")
  fun getHighestScore(): Flow<Int?>

  @Query("SELECT COUNT(*) FROM arcade_scores")
  suspend fun countScores(): Int

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertScore(score: ScoreEntity): Long

  @Query("DELETE FROM arcade_scores")
  suspend fun clearAllScores()
}
