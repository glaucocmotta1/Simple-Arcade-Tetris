package com.example.model

import androidx.compose.ui.graphics.Color

enum class TetrominoType(
  val color: Color,
  val lightColor: Color,
  val darkColor: Color
) {
  I(
    color = Color(0xFF00E5FF),      // Cyan
    lightColor = Color(0xFF84FFFF),
    darkColor = Color(0xFF00B0FF)
  ),
  J(
    color = Color(0xFF2979FF),      // Blue
    lightColor = Color(0xFF82B1FF),
    darkColor = Color(0xFF1565C0)
  ),
  L(
    color = Color(0xFFFF9100),      // Orange
    lightColor = Color(0xFFFFD180),
    darkColor = Color(0xFFE65100)
  ),
  O(
    color = Color(0xFFFFEA00),      // Yellow
    lightColor = Color(0xFFFFFF8D),
    darkColor = Color(0xFFFBC02D)
  ),
  S(
    color = Color(0xFF00E676),      // Green
    lightColor = Color(0xFFB9F6CA),
    darkColor = Color(0xFF00C853)
  ),
  T(
    color = Color(0xFFD500F9),      // Purple
    lightColor = Color(0xFFEA80FC),
    darkColor = Color(0xFFAA00FF)
  ),
  Z(
    color = Color(0xFFFF1744),      // Red
    lightColor = Color(0xFFFF8A80),
    darkColor = Color(0xFFD50000)
  );

  /**
   * Returns relative coordinates of the 4 blocks for a given rotation (0, 1, 2, 3).
   * Centers the rotation pivot according to the Super Rotation System (SRS).
   */
  fun getBlocks(rotation: Int): List<Point> {
    val normRot = (rotation % 4 + 4) % 4
    return when (this) {
      I -> when (normRot) {
        0 -> listOf(Point(0, 1), Point(1, 1), Point(2, 1), Point(3, 1))
        1 -> listOf(Point(2, 0), Point(2, 1), Point(2, 2), Point(2, 3))
        2 -> listOf(Point(0, 2), Point(1, 2), Point(2, 2), Point(3, 2))
        else -> listOf(Point(1, 0), Point(1, 1), Point(1, 2), Point(1, 3))
      }
      J -> when (normRot) {
        0 -> listOf(Point(0, 0), Point(0, 1), Point(1, 1), Point(2, 1))
        1 -> listOf(Point(1, 0), Point(2, 0), Point(1, 1), Point(1, 2))
        2 -> listOf(Point(0, 1), Point(1, 1), Point(2, 1), Point(2, 2))
        else -> listOf(Point(1, 0), Point(1, 1), Point(0, 2), Point(1, 2))
      }
      L -> when (normRot) {
        0 -> listOf(Point(2, 0), Point(0, 1), Point(1, 1), Point(2, 1))
        1 -> listOf(Point(1, 0), Point(1, 1), Point(1, 2), Point(2, 2))
        2 -> listOf(Point(0, 1), Point(1, 1), Point(2, 1), Point(0, 2))
        else -> listOf(Point(0, 0), Point(1, 0), Point(1, 1), Point(1, 2))
      }
      O -> listOf(Point(1, 0), Point(2, 0), Point(1, 1), Point(2, 1))
      S -> when (normRot) {
        0 -> listOf(Point(1, 0), Point(2, 0), Point(0, 1), Point(1, 1))
        1 -> listOf(Point(1, 0), Point(1, 1), Point(2, 1), Point(2, 2))
        2 -> listOf(Point(1, 1), Point(2, 1), Point(0, 2), Point(1, 2))
        else -> listOf(Point(0, 0), Point(0, 1), Point(1, 1), Point(1, 2))
      }
      T -> when (normRot) {
        0 -> listOf(Point(1, 0), Point(0, 1), Point(1, 1), Point(2, 1))
        1 -> listOf(Point(1, 0), Point(1, 1), Point(2, 1), Point(1, 2))
        2 -> listOf(Point(0, 1), Point(1, 1), Point(2, 1), Point(1, 2))
        else -> listOf(Point(1, 0), Point(0, 1), Point(1, 1), Point(1, 2))
      }
      Z -> when (normRot) {
        0 -> listOf(Point(0, 0), Point(1, 0), Point(1, 1), Point(2, 1))
        1 -> listOf(Point(2, 0), Point(1, 1), Point(2, 1), Point(1, 2))
        2 -> listOf(Point(0, 1), Point(1, 1), Point(2, 1), Point(2, 2))
        else -> listOf(Point(1, 0), Point(0, 1), Point(1, 1), Point(0, 2))
      }
    }
  }

  /**
   * Initial spawn position on the 10-wide board.
   */
  fun initialPosition(): Point {
    return when (this) {
      O -> Point(3, 0)
      I -> Point(3, -1)
      else -> Point(3, 0)
    }
  }

  companion object {
    /**
     * SRS Wall Kick offsets for JLSTZ pieces.
     * Returns list of kick tests (dx, dy) for moving from [fromRot] to [toRot].
     */
    fun getKicks(type: TetrominoType, fromRot: Int, toRot: Int): List<Point> {
      val f = (fromRot % 4 + 4) % 4
      val t = (toRot % 4 + 4) % 4

      if (type == O) return listOf(Point(0, 0))

      if (type == I) {
        return when (f to t) {
          0 to 1 -> listOf(Point(0, 0), Point(-2, 0), Point(1, 0), Point(-2, -1), Point(1, 2))
          1 to 0 -> listOf(Point(0, 0), Point(2, 0), Point(-1, 0), Point(2, 1), Point(-1, -2))
          1 to 2 -> listOf(Point(0, 0), Point(-1, 0), Point(2, 0), Point(-1, 2), Point(2, -1))
          2 to 1 -> listOf(Point(0, 0), Point(1, 0), Point(-2, 0), Point(1, -2), Point(-2, 1))
          2 to 3 -> listOf(Point(0, 0), Point(2, 0), Point(-1, 0), Point(2, 1), Point(-1, -2))
          3 to 2 -> listOf(Point(0, 0), Point(-2, 0), Point(1, 0), Point(-2, -1), Point(1, 2))
          3 to 0 -> listOf(Point(0, 0), Point(1, 0), Point(-2, 0), Point(1, -2), Point(-2, 1))
          0 to 3 -> listOf(Point(0, 0), Point(-1, 0), Point(2, 0), Point(-1, 2), Point(2, -1))
          else -> listOf(Point(0, 0))
        }
      }

      // JLSTZ kicks
      return when (f to t) {
        0 to 1 -> listOf(Point(0, 0), Point(-1, 0), Point(-1, 1), Point(0, -2), Point(-1, -2))
        1 to 0 -> listOf(Point(0, 0), Point(1, 0), Point(1, -1), Point(0, 2), Point(1, 2))
        1 to 2 -> listOf(Point(0, 0), Point(1, 0), Point(1, -1), Point(0, 2), Point(1, 2))
        2 to 1 -> listOf(Point(0, 0), Point(-1, 0), Point(-1, 1), Point(0, -2), Point(-1, -2))
        2 to 3 -> listOf(Point(0, 0), Point(1, 0), Point(1, 1), Point(0, -2), Point(1, -2))
        3 to 2 -> listOf(Point(0, 0), Point(-1, 0), Point(-1, -1), Point(0, 2), Point(-1, 2))
        3 to 0 -> listOf(Point(0, 0), Point(-1, 0), Point(-1, -1), Point(0, 2), Point(-1, 2))
        0 to 3 -> listOf(Point(0, 0), Point(1, 0), Point(1, 1), Point(0, -2), Point(1, -2))
        else -> listOf(Point(0, 0))
      }
    }
  }
}

/**
 * 7-Bag Randomizer: ensures all 7 tetrominoes appear before repeating, exactly like official Tetris.
 * Always maintains a buffer of upcoming pieces so that next() exactly returns the piece that was peeked.
 */
class SevenBagRandomizer {
  private val queue = mutableListOf<TetrominoType>()

  private fun refill() {
    queue.addAll(TetrominoType.values().toList().shuffled())
  }

  fun next(): TetrominoType {
    if (queue.isEmpty()) {
      refill()
    }
    val item = queue.removeAt(0)
    if (queue.size < 7) {
      refill()
    }
    return item
  }

  fun peek(count: Int = 3): List<TetrominoType> {
    while (queue.size < count) {
      refill()
    }
    return queue.take(count)
  }

  fun reset() {
    queue.clear()
    refill()
    refill()
  }
}
