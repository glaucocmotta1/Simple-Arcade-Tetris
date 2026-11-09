package com.example.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import java.text.NumberFormat
import java.util.Locale

@Composable
fun GameOverDialog(
  score: Int,
  highScore: Int,
  level: Int,
  lines: Int,
  onPlayAgain: () -> Unit,
  onOpenLeaderboard: () -> Unit
) {
  val formattedScore = NumberFormat.getNumberInstance(Locale.getDefault()).format(score)
  val formattedHigh = NumberFormat.getNumberInstance(Locale.getDefault()).format(highScore)
  val isNewHigh = score > 0 && score >= highScore

  Dialog(
    onDismissRequest = onPlayAgain,
    properties = DialogProperties(dismissOnBackPress = false, dismissOnClickOutside = false)
  ) {
    Box(
      modifier = Modifier
        .fillMaxWidth(0.92f)
        .clip(RoundedCornerShape(20.dp))
        .background(Brush.verticalGradient(listOf(Color(0xFF230E20), Color(0xFF090614))))
        .border(
          width = 2.dp,
          brush = Brush.verticalGradient(listOf(Color(0xFFFF1744), Color(0xFFD500F9))),
          shape = RoundedCornerShape(20.dp)
        )
        .padding(20.dp)
        .testTag("game_over_dialog")
    ) {
      Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
      ) {
        Text(
          text = "FIM DE JOGO",
          color = Color(0xFFFF1744),
          fontSize = 22.sp,
          fontWeight = FontWeight.Black,
          fontFamily = FontFamily.Monospace,
          letterSpacing = 3.sp
        )

        if (isNewHigh) {
          Spacer(modifier = Modifier.height(4.dp))
          Text(
            text = "★ NOVO RECORDE PESSOAL! ★",
            color = Color(0xFFFFD700),
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Monospace,
            letterSpacing = 1.sp
          )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Box(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0x33000000))
            .border(1.dp, Color(0x33FFFFFF), RoundedCornerShape(12.dp))
            .padding(16.dp)
        ) {
          Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween
            ) {
              Text("PONTUAÇÃO:", color = Color(0xFF94A3B8), fontSize = 12.sp, fontFamily = FontFamily.Monospace)
              Text(formattedScore, color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
            }
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween
            ) {
              Text("RECORDE ATUAL:", color = Color(0xFF94A3B8), fontSize = 12.sp, fontFamily = FontFamily.Monospace)
              Text(formattedHigh, color = Color(0xFFFFD700), fontSize = 14.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
            }
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween
            ) {
              Text("NÍVEL ALCANÇADO:", color = Color(0xFF94A3B8), fontSize = 12.sp, fontFamily = FontFamily.Monospace)
              Text("$level", color = Color(0xFF38BDF8), fontSize = 14.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
            }
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween
            ) {
              Text("LINHAS ELIMINADAS:", color = Color(0xFF94A3B8), fontSize = 12.sp, fontFamily = FontFamily.Monospace)
              Text("$lines", color = Color(0xFF4ADE80), fontSize = 14.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
            }
          }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Button(
          onClick = onPlayAgain,
          modifier = Modifier
            .fillMaxWidth()
            .height(50.dp)
            .testTag("play_again_button"),
          shape = RoundedCornerShape(12.dp),
          colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981))
        ) {
          Icon(Icons.Default.PlayArrow, contentDescription = null, tint = Color.White)
          Spacer(modifier = Modifier.width(8.dp))
          Text("JOGAR NOVAMENTE", fontSize = 14.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
        }

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedButton(
          onClick = onOpenLeaderboard,
          modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
            .testTag("game_over_leaderboard_button"),
          shape = RoundedCornerShape(12.dp),
          colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFFFD700))
        ) {
          Icon(Icons.Default.EmojiEvents, contentDescription = null, tint = Color(0xFFFFD700))
          Spacer(modifier = Modifier.width(8.dp))
          Text("VER HALL DA FAMA", fontSize = 13.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
        }
      }
    }
  }
}
