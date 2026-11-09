package com.example

import com.example.model.Point
import com.example.model.SevenBagRandomizer
import com.example.model.TetrominoType
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {
  @Test
  fun testSevenBagRandomizerGeneratesAllSevenPieces() {
    val randomizer = SevenBagRandomizer()
    val firstBag = (1..7).map { randomizer.next() }
    val uniquePieces = firstBag.toSet()
    assertEquals(7, uniquePieces.size)
    assertTrue(uniquePieces.containsAll(TetrominoType.values().toList()))
  }

  @Test
  fun testPeekMatchesNextExactly() {
    val randomizer = SevenBagRandomizer()
    for (i in 1..20) {
      val peeked = randomizer.peek(3)
      val next = randomizer.next()
      assertEquals("A peça peeked[0] deve ser exatamente a peça que cai em next()", peeked[0], next)
    }
  }

  @Test
  fun testTetrominoBlocksCount() {
    for (type in TetrominoType.values()) {
      for (rot in 0..3) {
        val blocks = type.getBlocks(rot)
        assertEquals("Tetromino $type rot $rot must have 4 blocks", 4, blocks.size)
      }
    }
  }

  @Test
  fun testPointAddition() {
    val p1 = Point(2, 3)
    val p2 = Point(-1, 4)
    val res = p1 + p2
    assertEquals(Point(1, 7), res)
  }
}
