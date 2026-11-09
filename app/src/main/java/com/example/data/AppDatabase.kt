package com.example.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(entities = [ScoreEntity::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
  abstract fun scoreDao(): ScoreDao

  companion object {
    @Volatile
    private var INSTANCE: AppDatabase? = null

    fun getInstance(context: Context): AppDatabase {
      return INSTANCE ?: synchronized(this) {
        val instance = Room.databaseBuilder(
          context.applicationContext,
          AppDatabase::class.java,
          "tetris_arcade.db"
        )
        .addCallback(DatabaseCallback())
        .build()
        INSTANCE = instance
        instance
      }
    }

    private class DatabaseCallback : RoomDatabase.Callback() {
      override fun onCreate(db: SupportSQLiteDatabase) {
        super.onCreate(db)
        // Pre-populate classic fliperama hall of fame scores
        val defaultScores = listOf(
          Triple("TET", 50000, 10 to 45),
          Triple("ARC", 38500, 8 to 36),
          Triple("RET", 26000, 6 to 28),
          Triple("PRO", 18200, 5 to 22),
          Triple("ACE", 12500, 4 to 16),
          Triple("MAX", 8400, 3 to 12),
          Triple("NEO", 5100, 2 to 8),
          Triple("VAP", 3000, 1 to 5)
        )
        val now = System.currentTimeMillis()
        var offset = 86400000L * 10
        for ((name, pts, lvlLines) in defaultScores) {
          val (lvl, lines) = lvlLines
          val time = now - offset
          offset -= 86400000L
          db.execSQL(
            "INSERT INTO arcade_scores (initials, score, level, lines, timestamp, tetrises) " +
            "VALUES ('$name', $pts, $lvl, $lines, $time, ${lines / 4})"
          )
        }
      }
    }
  }
}
