package com.example.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.model.TetrominoType

@Composable
fun PiecePreview(
  type: TetrominoType?,
  size: Dp = 56.dp,
  modifier: Modifier = Modifier
) {
  Box(
    modifier = modifier
      .size(size)
      .clip(RoundedCornerShape(6.dp))
      .background(Color(0xFF0F0B1E))
      .border(1.dp, Color(0x33FFFFFF), RoundedCornerShape(6.dp))
  ) {
    if (type != null) {
      Canvas(modifier = Modifier.size(size)) {
        val blocks = type.getBlocks(0)
        val minX = blocks.minOf { it.x }
        val maxX = blocks.maxOf { it.x }
        val minY = blocks.minOf { it.y }
        val maxY = blocks.maxOf { it.y }

        val pieceWidth = maxX - minX + 1
        val pieceHeight = maxY - minY + 1

        val cellSize = (this.size.width / 4.4f)
        val offsetX = (this.size.width - pieceWidth * cellSize) / 2f
        val offsetY = (this.size.height - pieceHeight * cellSize) / 2f

        for (b in blocks) {
          val drawX = offsetX + (b.x - minX) * cellSize
          val drawY = offsetY + (b.y - minY) * cellSize

          // Base block
          drawRect(
            color = type.color,
            topLeft = Offset(drawX + 1f, drawY + 1f),
            size = Size(cellSize - 2f, cellSize - 2f)
          )
          // Highlight
          drawRect(
            color = Color.White.copy(alpha = 0.45f),
            topLeft = Offset(drawX + 1f, drawY + 1f),
            size = Size(cellSize - 2f, (cellSize - 2f) * 0.35f)
          )
        }
      }
    }
  }
}
