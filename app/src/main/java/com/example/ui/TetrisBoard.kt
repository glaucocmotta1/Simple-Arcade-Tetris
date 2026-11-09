package com.example.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.ActivePiece
import com.example.model.BOARD_HEIGHT
import com.example.model.BOARD_WIDTH
import com.example.model.GameStatus
import com.example.model.TetrisGameState
import kotlin.math.abs

@Composable
fun TetrisBoard(
  gameState: TetrisGameState,
  onMoveLeft: () -> Unit,
  onMoveRight: () -> Unit,
  onSoftDrop: () -> Unit,
  onRotate: () -> Unit,
  onHardDrop: () -> Unit,
  onHold: () -> Unit,
  modifier: Modifier = Modifier
) {
  val infiniteTransition = rememberInfiniteTransition(label = "flash")
  val flashAlpha by infiniteTransition.animateFloat(
    initialValue = 0.3f,
    targetValue = 1f,
    animationSpec = infiniteRepeatable(
      animation = tween(80, easing = LinearEasing),
      repeatMode = RepeatMode.Reverse
    ),
    label = "flashAlpha"
  )

  val isPlaying = gameState.status == GameStatus.PLAYING

  Box(
    modifier = modifier
      .fillMaxWidth()
      .aspectRatio(10f / 20f)
      .shadow(12.dp, RoundedCornerShape(8.dp), ambientColor = Color(0xFF673AB7), spotColor = Color(0xFF00E5FF))
      .clip(RoundedCornerShape(8.dp))
      .background(Color(0xFF090614))
      .border(2.dp, Brush.verticalGradient(listOf(Color(0xFF8A2BE2), Color(0xFF00E5FF))), RoundedCornerShape(8.dp))
      .testTag("tetris_board")
      .pointerInput(isPlaying) {
        if (!isPlaying) return@pointerInput

        awaitEachGesture {
          val down = awaitFirstDown(requireUnconsumed = false)
          val startTime = System.currentTimeMillis()
          val startPos = down.position
          var lastPos = startPos
          var accumulatedDx = 0f
          var accumulatedDy = 0f
          var hasTriggeredHold = false
          var hasTriggeredHardDrop = false
          var isLockedDirection = false
          var isVerticalDown = false
          var isHorizontal = false

          // Calibration thresholds
          val lockDistanceThreshold = 14f // Pixels before determining primary gesture axis
          val stepX = 28f // Pixels needed to shift 1 column left/right
          val stepSoftDrop = 26f // Pixels for continuous soft drop
          val holdSwipeThreshold = 40f // Pixels upward for Hold

          var pointerActive = true
          while (pointerActive) {
            val event = awaitPointerEvent()
            val change = event.changes.firstOrNull { it.id == down.id } ?: break

            if (!change.pressed) {
              // Finger released
              pointerActive = false
              val upTime = System.currentTimeMillis()
              val duration = upTime - startTime
              val totalDx = change.position.x - startPos.x
              val totalDy = change.position.y - startPos.y

              val velocityY = if (duration > 0) (totalDy / (duration / 1000f)) else 0f

              // Fast downward swipe / fling (Hard drop)
              // If gesture was vertical or has high downward velocity, trigger Hard Drop and ignore horizontal curves
              if ((isVerticalDown || (!isHorizontal && totalDy > 45f)) && !hasTriggeredHardDrop) {
                if (totalDy > 50f && (velocityY > 750f || duration < 320)) {
                  onHardDrop()
                  hasTriggeredHardDrop = true
                }
              } else if (!isLockedDirection && duration < 260 && abs(totalDx) < 18f && abs(totalDy) < 18f) {
                // Quick tap: Rotate clockwise
                onRotate()
              }
              change.consume()
            } else {
              // Finger is dragging
              val currentPos = change.position
              val dx = currentPos.x - lastPos.x
              val dy = currentPos.y - lastPos.y
              lastPos = currentPos

              accumulatedDx += dx
              accumulatedDy += dy

              val netDx = currentPos.x - startPos.x
              val netDy = currentPos.y - startPos.y
              val netDistanceSq = netDx * netDx + netDy * netDy

              // 1. Determine and lock axis once movement exceeds threshold
              if (!isLockedDirection && netDistanceSq >= lockDistanceThreshold * lockDistanceThreshold) {
                isLockedDirection = true
                if (netDy < -holdSwipeThreshold * 0.6f && abs(netDy) > abs(netDx)) {
                  // Upward swipe -> Hold
                  onHold()
                  hasTriggeredHold = true
                } else if (netDy > 14f && netDy > abs(netDx) * 0.8f) {
                  // Dominant downward motion -> Lock to vertical down!
                  // CRITICAL: All horizontal curves are completely ignored from this point forward!
                  isVerticalDown = true
                  accumulatedDx = 0f
                } else {
                  // Dominant horizontal motion -> Lock to horizontal column shifting
                  isHorizontal = true
                  accumulatedDy = 0f
                }
              }

              // 2. Process gesture based on locked axis
              if (isVerticalDown) {
                // Completely ignore any horizontal drift/curves of the finger
                accumulatedDx = 0f

                // Gentle downward drag -> soft drop
                if (accumulatedDy >= stepSoftDrop) {
                  onSoftDrop()
                  accumulatedDy = 0f
                }
                change.consume()
              } else if (isHorizontal) {
                // Only horizontal column shifting, ignore vertical wobble
                accumulatedDy = 0f

                if (abs(accumulatedDx) >= stepX) {
                  if (accumulatedDx > 0) onMoveRight() else onMoveLeft()
                  accumulatedDx = 0f
                  change.consume()
                }
              } else if (!isLockedDirection) {
                // Upward swipe check before full lock
                if (accumulatedDy < -holdSwipeThreshold && !hasTriggeredHold) {
                  onHold()
                  hasTriggeredHold = true
                  isLockedDirection = true
                  change.consume()
                }
              }
            }
          }
        }
      },
    contentAlignment = Alignment.Center
  ) {
    Canvas(modifier = Modifier.fillMaxSize()) {
      val cellWidth = size.width / BOARD_WIDTH
      val cellHeight = size.height / BOARD_HEIGHT

      // 1. Subtle grid lines
      drawArcadeGrid(cellWidth, cellHeight)

      // 2. Locked blocks on grid
      for (y in 0 until BOARD_HEIGHT) {
        val isClearing = gameState.clearingLines.contains(y)
        for (x in 0 until BOARD_WIDTH) {
          val blockColor = gameState.grid[y][x]
          if (blockColor != null) {
            val drawColor = if (isClearing) Color.White.copy(alpha = flashAlpha) else blockColor
            drawArcadeBlock(
              x = x,
              y = y,
              cellWidth = cellWidth,
              cellHeight = cellHeight,
              color = drawColor
            )
          }
        }
      }

      // 3. Ghost piece
      if (gameState.ghostEnabled && gameState.currentPiece != null && gameState.ghostPiecePosition != null) {
        val ghostPiece = gameState.currentPiece.copy(position = gameState.ghostPiecePosition)
        for (b in ghostPiece.blocks()) {
          if (b.y in 0 until BOARD_HEIGHT && b.x in 0 until BOARD_WIDTH) {
            drawGhostBlock(
              x = b.x,
              y = b.y,
              cellWidth = cellWidth,
              cellHeight = cellHeight,
              color = gameState.currentPiece.type.color
            )
          }
        }
      }

      // 4. Current active piece
      gameState.currentPiece?.let { piece ->
        for (b in piece.blocks()) {
          if (b.y in 0 until BOARD_HEIGHT && b.x in 0 until BOARD_WIDTH) {
            drawArcadeBlock(
              x = b.x,
              y = b.y,
              cellWidth = cellWidth,
              cellHeight = cellHeight,
              color = piece.type.color
            )
          }
        }
      }
    }

    // Tetris celebration notification
    AnimatedVisibility(
      visible = gameState.isTetrisNoticeVisible,
      enter = scaleIn(tween(200, easing = FastOutSlowInEasing)) + fadeIn(),
      exit = fadeOut(tween(200))
    ) {
      Box(
        modifier = Modifier
          .clip(RoundedCornerShape(8.dp))
          .background(Brush.horizontalGradient(listOf(Color(0xFFD500F9), Color(0xFFFF1744))))
          .border(2.dp, Color.Yellow, RoundedCornerShape(8.dp))
          .padding(horizontal = 24.dp, vertical = 10.dp)
      ) {
        Text(
          text = "★ TETRIS! ★",
          color = Color.Yellow,
          fontSize = 24.sp,
          fontWeight = FontWeight.Black,
          fontFamily = FontFamily.Monospace,
          letterSpacing = 2.sp
        )
      }
    }
  }
}

private fun DrawScope.drawArcadeGrid(cellWidth: Float, cellHeight: Float) {
  val gridColor = Color(0x18FFFFFF)
  for (col in 1 until BOARD_WIDTH) {
    val x = col * cellWidth
    drawLine(
      color = gridColor,
      start = Offset(x, 0f),
      end = Offset(x, size.height),
      strokeWidth = 1f
    )
  }
  for (row in 1 until BOARD_HEIGHT) {
    val y = row * cellHeight
    drawLine(
      color = gridColor,
      start = Offset(0f, y),
      end = Offset(size.width, y),
      strokeWidth = 1f
    )
  }
}

private fun DrawScope.drawArcadeBlock(
  x: Int,
  y: Int,
  cellWidth: Float,
  cellHeight: Float,
  color: Color
) {
  val padding = 1f
  val left = x * cellWidth + padding
  val top = y * cellHeight + padding
  val w = cellWidth - padding * 2
  val h = cellHeight - padding * 2
  val bevel = minOf(w, h) * 0.18f

  drawRect(
    color = color,
    topLeft = Offset(left, top),
    size = Size(w, h)
  )

  val topBevel = Path().apply {
    moveTo(left, top)
    lineTo(left + w, top)
    lineTo(left + w - bevel, top + bevel)
    lineTo(left + bevel, top + bevel)
    close()
  }
  drawPath(topBevel, color = Color.White.copy(alpha = 0.45f))

  val leftBevel = Path().apply {
    moveTo(left, top)
    lineTo(left + bevel, top + bevel)
    lineTo(left + bevel, top + h - bevel)
    lineTo(left, top + h)
    close()
  }
  drawPath(leftBevel, color = Color.White.copy(alpha = 0.28f))

  val rightBevel = Path().apply {
    moveTo(left + w, top)
    lineTo(left + w, top + h)
    lineTo(left + w - bevel, top + h - bevel)
    lineTo(left + w - bevel, top + bevel)
    close()
  }
  drawPath(rightBevel, color = Color.Black.copy(alpha = 0.35f))

  val bottomBevel = Path().apply {
    moveTo(left, top + h)
    lineTo(left + bevel, top + h - bevel)
    lineTo(left + w - bevel, top + h - bevel)
    lineTo(left + w, top + h)
    close()
  }
  drawPath(bottomBevel, color = Color.Black.copy(alpha = 0.45f))

  drawRect(
    color = Color.Black.copy(alpha = 0.18f),
    topLeft = Offset(left + bevel, top + bevel),
    size = Size(w - bevel * 2, h - bevel * 2),
    style = Stroke(width = 1f)
  )
}

private fun DrawScope.drawGhostBlock(
  x: Int,
  y: Int,
  cellWidth: Float,
  cellHeight: Float,
  color: Color
) {
  val padding = 2f
  val left = x * cellWidth + padding
  val top = y * cellHeight + padding
  val w = cellWidth - padding * 2
  val h = cellHeight - padding * 2

  drawRect(
    color = color.copy(alpha = 0.12f),
    topLeft = Offset(left, top),
    size = Size(w, h)
  )
  drawRect(
    color = color.copy(alpha = 0.65f),
    topLeft = Offset(left, top),
    size = Size(w, h),
    style = Stroke(width = 1.5f)
  )
}
