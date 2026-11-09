package com.example.model

import androidx.compose.ui.graphics.Color

const val BOARD_WIDTH = 10
const val BOARD_HEIGHT = 20

enum class GameStatus {
  READY,
  PLAYING,
  PAUSED,
  GAME_OVER,
  ENTERING_INITIALS
}

data class ActivePiece(
  val type: TetrominoType,
  val position: Point,
  val rotation: Int = 0
) {
  fun blocks(): List<Point> {
    return type.getBlocks(rotation).map { it + position }
  }
}

data class TetrisGameState(
  val grid: List<List<Color?>> = List(BOARD_HEIGHT) { List(BOARD_WIDTH) { null } },
  val currentPiece: ActivePiece? = null,
  val ghostPiecePosition: Point? = null,
  val nextPieces: List<TetrominoType> = emptyList(),
  val holdPiece: TetrominoType? = null,
  val canHold: Boolean = true,
  val score: Int = 0,
  val highScore: Int = 0,
  val level: Int = 1,
  val lines: Int = 0,
  val status: GameStatus = GameStatus.READY,
  val clearingLines: List<Int> = emptyList(), // lines currently flashing during clear animation
  val lastLineClearCount: Int = 0,
  val isTetrisNoticeVisible: Boolean = false,
  val musicEnabled: Boolean = true,
  val soundEnabled: Boolean = true,
  val hapticsEnabled: Boolean = true,
  val ghostEnabled: Boolean = true
)
