package com.example.ui

import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.MusicOff
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.GameStatus
import com.example.viewmodel.TetrisViewModel
import java.text.NumberFormat
import java.util.Locale

@Composable
fun TetrisScreen(
  viewModel: TetrisViewModel,
  modifier: Modifier = Modifier
) {
  val gameState by viewModel.uiState.collectAsState()
  val topScores by viewModel.topScores.collectAsState()

  var showLeaderboard by remember { mutableStateOf(false) }

  BackHandler(enabled = gameState.status == GameStatus.PLAYING) {
    viewModel.pauseGame()
  }

  val numberFormat = NumberFormat.getNumberInstance(Locale.getDefault())

  val infiniteTransition = rememberInfiniteTransition(label = "pulse")
  val pulseAlpha by infiniteTransition.animateFloat(
    initialValue = 0.35f,
    targetValue = 1f,
    animationSpec = infiniteRepeatable(
      animation = tween(400, easing = LinearEasing),
      repeatMode = RepeatMode.Reverse
    ),
    label = "pulseAlpha"
  )

  Scaffold(
    contentWindowInsets = WindowInsets.safeDrawing,
    containerColor = Color(0xFF070412),
    modifier = modifier.fillMaxSize()
  ) { innerPadding ->
    Box(
      modifier = Modifier
        .fillMaxSize()
        .padding(innerPadding)
        .background(
          Brush.verticalGradient(
            listOf(Color(0xFF0F0B1E), Color(0xFF070412), Color(0xFF030208))
          )
        )
    ) {
      Column(
        modifier = Modifier
          .fillMaxSize()
          .padding(horizontal = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
      ) {
        // 1. Arcade Header Bar
        ArcadeHeader(
          musicEnabled = gameState.musicEnabled,
          onToggleMusic = { viewModel.toggleMusic() },
          onOpenLeaderboard = { showLeaderboard = true },
          onPause = { viewModel.pauseGame() }
        )

        // 2. Main Game Arena (Left Info - Center Board - Right Info)
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .weight(1f, fill = false)
            .padding(vertical = 4.dp),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          // Left Sidebar: HOLD, LEVEL, LINES
          Column(
            modifier = Modifier
              .width(76.dp)
              .fillMaxHeight(),
            verticalArrangement = Arrangement.SpaceEvenly,
            horizontalAlignment = Alignment.CenterHorizontally
          ) {
            ArcadeInfoBox(title = "GUARDAR") {
              PiecePreview(
                type = gameState.holdPiece,
                size = 54.dp
              )
            }

            ArcadeStatBox(
              title = "NÍVEL",
              value = "${gameState.level}",
              color = Color(0xFF38BDF8)
            )

            ArcadeStatBox(
              title = "LINHAS",
              value = "${gameState.lines}",
              color = Color(0xFF4ADE80)
            )
          }

          // Center: The 10x20 Tetris Matrix
          Box(
            modifier = Modifier
              .weight(1f)
              .padding(horizontal = 4.dp),
            contentAlignment = Alignment.Center
          ) {
            TetrisBoard(
              gameState = gameState,
              onMoveLeft = { viewModel.moveLeft() },
              onMoveRight = { viewModel.moveRight() },
              onSoftDrop = { viewModel.softDrop() },
              onRotate = { viewModel.rotateClockwise() },
              onHardDrop = { viewModel.hardDrop() },
              onHold = { viewModel.holdPiece() },
              modifier = Modifier.padding(horizontal = 2.dp)
            )

            // "PRESS TO PLAY" Overlay for initial state
            if (gameState.status == GameStatus.READY) {
              Box(
                modifier = Modifier
                  .matchParentSize()
                  .clip(RoundedCornerShape(8.dp))
                  .background(Color.Black.copy(alpha = 0.72f))
                  .clickable { viewModel.startGame() },
                contentAlignment = Alignment.Center
              ) {
                Column(
                  horizontalAlignment = Alignment.CenterHorizontally,
                  verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                  Button(
                    onClick = { viewModel.startGame() },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF7C3AED)),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.testTag("start_game_button")
                  ) {
                    Icon(Icons.Default.PlayArrow, contentDescription = null, tint = Color.White)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                      text = "JOGAR",
                      fontSize = 16.sp,
                      fontWeight = FontWeight.Black,
                      fontFamily = FontFamily.Monospace,
                      letterSpacing = 2.sp
                    )
                  }

                  Text(
                    text = "TOQUE PARA INICIAR",
                    color = Color(0xFFFFD700).copy(alpha = pulseAlpha),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace,
                    letterSpacing = 1.sp
                  )
                }
              }
            }
          }

          // Right Sidebar: NEXT, SCORE, HIGH SCORE
          Column(
            modifier = Modifier
              .width(76.dp)
              .fillMaxHeight(),
            verticalArrangement = Arrangement.SpaceEvenly,
            horizontalAlignment = Alignment.CenterHorizontally
          ) {
            ArcadeInfoBox(title = "PRÓXIMA") {
              PiecePreview(
                type = gameState.nextPieces.firstOrNull(),
                size = 54.dp
              )
            }

            ArcadeStatBox(
              title = "PONTOS",
              value = numberFormat.format(gameState.score),
              color = Color.White,
              isSmallValue = gameState.score >= 100000
            )

            ArcadeStatBox(
              title = "RECORDE",
              value = numberFormat.format(gameState.highScore),
              color = Color(0xFFFFD700),
              isSmallValue = gameState.highScore >= 100000
            )
          }
        }

        // 3. Gesture Controls Hint Bar (Replaces physical buttons)
        ArcadeGestureLegend()
      }

      // Modals & Overlays
      if (showLeaderboard) {
        LeaderboardDialog(
          scores = topScores,
          onDismiss = { showLeaderboard = false }
        )
      }

      if (gameState.status == GameStatus.PAUSED) {
        PauseDialog(
          musicEnabled = gameState.musicEnabled,
          soundEnabled = gameState.soundEnabled,
          hapticsEnabled = gameState.hapticsEnabled,
          ghostEnabled = gameState.ghostEnabled,
          onResume = { viewModel.resumeGame() },
          onRestart = { viewModel.restartGame() },
          onOpenLeaderboard = { showLeaderboard = true },
          onToggleMusic = { viewModel.toggleMusic() },
          onToggleSound = { viewModel.toggleSound() },
          onToggleHaptics = { viewModel.toggleHaptics() },
          onToggleGhost = { viewModel.toggleGhost() }
        )
      }

      if (gameState.status == GameStatus.ENTERING_INITIALS) {
        InitialsEntryDialog(
          score = gameState.score,
          level = gameState.level,
          lines = gameState.lines,
          onSubmit = { initials -> viewModel.submitInitials(initials) }
        )
      }

      if (gameState.status == GameStatus.GAME_OVER) {
        GameOverDialog(
          score = gameState.score,
          highScore = gameState.highScore,
          level = gameState.level,
          lines = gameState.lines,
          onPlayAgain = { viewModel.restartGame() },
          onOpenLeaderboard = { showLeaderboard = true }
        )
      }
    }
  }
}

@Composable
private fun ArcadeHeader(
  musicEnabled: Boolean,
  onToggleMusic: () -> Unit,
  onOpenLeaderboard: () -> Unit,
  onPause: () -> Unit
) {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .padding(horizontal = 4.dp, vertical = 2.dp),
    horizontalArrangement = Arrangement.SpaceBetween,
    verticalAlignment = Alignment.CenterVertically
  ) {
    Row(
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
      Box(
        modifier = Modifier
          .size(10.dp)
          .clip(CircleShape)
          .background(Color(0xFF00E5FF))
      )
      Text(
        text = "TETRIS ARCADE",
        color = Color(0xFF00E5FF),
        fontSize = 17.sp,
        fontWeight = FontWeight.Black,
        fontFamily = FontFamily.Monospace,
        letterSpacing = 2.sp
      )
    }

    Row(
      horizontalArrangement = Arrangement.spacedBy(2.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      IconButton(
        onClick = onToggleMusic,
        modifier = Modifier.testTag("music_button")
      ) {
        Icon(
          imageVector = if (musicEnabled) Icons.Default.MusicNote else Icons.Default.MusicOff,
          contentDescription = "Música Korobeiniki",
          tint = if (musicEnabled) Color(0xFFFFD700) else Color(0xFF64748B)
        )
      }

      IconButton(
        onClick = onOpenLeaderboard,
        modifier = Modifier.testTag("leaderboard_header_button")
      ) {
        Icon(
          imageVector = Icons.Default.EmojiEvents,
          contentDescription = "Hall da Fama",
          tint = Color(0xFFFFD700)
        )
      }

      IconButton(
        onClick = onPause,
        modifier = Modifier.testTag("pause_header_button")
      ) {
        Icon(
          imageVector = Icons.Default.Pause,
          contentDescription = "Pausar",
          tint = Color.White
        )
      }
    }
  }
}

@Composable
private fun ArcadeInfoBox(
  title: String,
  content: @Composable () -> Unit
) {
  Column(
    horizontalAlignment = Alignment.CenterHorizontally,
    modifier = Modifier
      .clip(RoundedCornerShape(8.dp))
      .background(Color(0xFF140F2D))
      .border(1.dp, Color(0xFF261D4C), RoundedCornerShape(8.dp))
      .padding(horizontal = 4.dp, vertical = 6.dp)
  ) {
    Text(
      text = title,
      color = Color(0xFF94A3B8),
      fontSize = 10.sp,
      fontWeight = FontWeight.Bold,
      fontFamily = FontFamily.Monospace,
      letterSpacing = 1.sp
    )
    Spacer(modifier = Modifier.height(4.dp))
    content()
  }
}

@Composable
private fun ArcadeStatBox(
  title: String,
  value: String,
  color: Color,
  isSmallValue: Boolean = false
) {
  Column(
    horizontalAlignment = Alignment.CenterHorizontally,
    modifier = Modifier
      .width(74.dp)
      .clip(RoundedCornerShape(8.dp))
      .background(Color(0xFF140F2D))
      .border(1.dp, Color(0xFF261D4C), RoundedCornerShape(8.dp))
      .padding(horizontal = 4.dp, vertical = 6.dp)
  ) {
    Text(
      text = title,
      color = Color(0xFF94A3B8),
      fontSize = 9.sp,
      fontWeight = FontWeight.Bold,
      fontFamily = FontFamily.Monospace,
      letterSpacing = 0.5.sp
    )
    Spacer(modifier = Modifier.height(2.dp))
    Text(
      text = value,
      color = color,
      fontSize = if (isSmallValue) 11.sp else 13.sp,
      fontWeight = FontWeight.Black,
      fontFamily = FontFamily.Monospace,
      maxLines = 1,
      textAlign = TextAlign.Center
    )
  }
}

@Composable
private fun ArcadeGestureLegend() {
  Box(
    modifier = Modifier
      .fillMaxWidth()
      .padding(horizontal = 4.dp, vertical = 6.dp)
      .clip(RoundedCornerShape(12.dp))
      .background(Color(0xFF100C24))
      .border(1.dp, Color(0xFF261D4C), RoundedCornerShape(12.dp))
      .padding(horizontal = 8.dp, vertical = 6.dp)
  ) {
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceAround,
      verticalAlignment = Alignment.CenterVertically
    ) {
      GestureHintItem(symbol = "👆", action = "Girar", sub = "Toque")
      GestureHintItem(symbol = "↔️", action = "Mover", sub = "Deslizar")
      GestureHintItem(symbol = "⬆️", action = "Guardar", sub = "Cima")
      GestureHintItem(symbol = "⬇️", action = "Queda", sub = "Baixo")
      GestureHintItem(symbol = "⚡", action = "Cai Rápido", sub = "Arraste", highlight = true)
    }
  }
}

@Composable
private fun GestureHintItem(
  symbol: String,
  action: String,
  sub: String,
  highlight: Boolean = false
) {
  Column(
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.Center
  ) {
    Text(
      text = symbol,
      fontSize = 15.sp
    )
    Text(
      text = action,
      color = if (highlight) Color(0xFFFFD700) else Color.White,
      fontSize = 10.sp,
      fontWeight = FontWeight.Bold,
      fontFamily = FontFamily.Monospace
    )
    Text(
      text = sub,
      color = Color(0xFF64748B),
      fontSize = 8.sp,
      fontFamily = FontFamily.Monospace
    )
  }
}
