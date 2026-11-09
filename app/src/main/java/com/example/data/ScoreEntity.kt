package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "arcade_scores")
data class ScoreEntity(
  @PrimaryKey(autoGenerate = true)
  val id: Long = 0,
  val initials: String,
  val score: Int,
  val level: Int,
  val lines: Int,
  val timestamp: Long = System.currentTimeMillis(),
  val tetrises: Int = 0
)
