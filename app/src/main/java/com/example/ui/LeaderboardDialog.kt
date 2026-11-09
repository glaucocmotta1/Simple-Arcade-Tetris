package com.example.ui

import android.content.Context
import android.content.Intent
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.R
import com.example.data.ScoreEntity
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun LeaderboardDialog(
  scores: List<ScoreEntity>,
  onDismiss: () -> Unit
) {
  val context = LocalContext.current
  val numberFormat = NumberFormat.getNumberInstance(Locale.getDefault())

  Dialog(
    onDismissRequest = onDismiss,
    properties = DialogProperties(usePlatformDefaultWidth = false)
  ) {
    Box(
      modifier = Modifier
        .fillMaxWidth(0.94f)
        .clip(RoundedCornerShape(20.dp))
        .background(
          Brush.verticalGradient(
            listOf(Color(0xFF130E29), Color(0xFF090614))
          )
        )
        .border(
          width = 2.dp,
          brush = Brush.verticalGradient(listOf(Color(0xFFFFD700), Color(0xFFD500F9))),
          shape = RoundedCornerShape(20.dp)
        )
        .padding(16.dp)
        .testTag("leaderboard_dialog")
    ) {
      Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
      ) {
        // Top header with arcade trophy & close button
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            Icon(
              imageVector = Icons.Default.EmojiEvents,
              contentDescription = null,
              tint = Color(0xFFFFD700),
              modifier = Modifier.size(28.dp)
            )
            Column {
              Text(
                text = stringResource(R.string.ranking_title),
                color = Color(0xFFFFD700),
                fontSize = 18.sp,
                fontWeight = FontWeight.Black,
                fontFamily = FontFamily.Monospace,
                letterSpacing = 2.sp
              )
              Text(
                text = stringResource(R.string.ranking_subtitle),
                color = Color(0xFF94A3B8),
                fontSize = 11.sp,
                fontFamily = FontFamily.Monospace
              )
            }
          }

          IconButton(
            onClick = onDismiss,
            modifier = Modifier.testTag("close_leaderboard_button")
          ) {
            Icon(
              imageVector = Icons.Default.Close,
              contentDescription = "Fechar",
              tint = Color.White
            )
          }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Table Column Headers
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(6.dp))
            .background(Color(0xFF1E1B4B))
            .padding(horizontal = 8.dp, vertical = 6.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = "#",
            color = Color(0xFF94A3B8),
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Monospace,
            modifier = Modifier.width(36.dp),
            textAlign = TextAlign.Center
          )
          Text(
            text = stringResource(R.string.arcade_name),
            color = Color(0xFF94A3B8),
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Monospace,
            modifier = Modifier.width(54.dp)
          )
          Text(
            text = stringResource(R.string.arcade_pts),
            color = Color(0xFF94A3B8),
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Monospace,
            modifier = Modifier.weight(1f),
            textAlign = TextAlign.End
          )
          Text(
            text = stringResource(R.string.arcade_lvl),
            color = Color(0xFF94A3B8),
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Monospace,
            modifier = Modifier.width(42.dp),
            textAlign = TextAlign.End
          )
          Text(
            text = "LIN",
            color = Color(0xFF94A3B8),
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Monospace,
            modifier = Modifier.width(42.dp),
            textAlign = TextAlign.End
          )
        }

        Spacer(modifier = Modifier.height(6.dp))

        // Scores List
        if (scores.isEmpty()) {
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .height(200.dp),
            contentAlignment = Alignment.Center
          ) {
            Text(
              text = "Nenhum recorde registrado ainda.\nSeja o primeiro!",
              color = Color(0xFF64748B),
              fontSize = 14.sp,
              textAlign = TextAlign.Center,
              fontFamily = FontFamily.Monospace
            )
          }
        } else {
          LazyColumn(
            modifier = Modifier
              .fillMaxWidth()
              .height(300.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
          ) {
            itemsIndexed(scores) { index, item ->
              ScoreRowItem(
                rank = index + 1,
                item = item,
                formattedScore = numberFormat.format(item.score)
              )
            }
          }
        }

        Spacer(modifier = Modifier.height(14.dp))
        HorizontalDivider(color = Color(0x33FFFFFF))
        Spacer(modifier = Modifier.height(12.dp))

        // Actions: Share & Close
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          if (scores.isNotEmpty()) {
            val top = scores.first()
            OutlinedButton(
              onClick = {
                shareTopScore(context, top.initials, top.score, top.level, top.lines)
              },
              modifier = Modifier
                .weight(1f)
                .height(48.dp)
                .testTag("share_score_button"),
              shape = RoundedCornerShape(10.dp),
              colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF38BDF8))
            ) {
              Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(16.dp))
              Spacer(modifier = Modifier.width(6.dp))
              Text("COMPARTILHAR", fontSize = 12.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
            }
          }

          Button(
            onClick = onDismiss,
            modifier = Modifier
              .weight(1f)
              .height(48.dp)
              .testTag("dismiss_leaderboard_button"),
            shape = RoundedCornerShape(10.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6D28D9))
          ) {
            Text("VOLTAR", fontSize = 13.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
          }
        }
      }
    }
  }
}

@Composable
private fun ScoreRowItem(
  rank: Int,
  item: ScoreEntity,
  formattedScore: String
) {
  val (badgeBg, rankColor, textColor) = when (rank) {
    1 -> Triple(Color(0xFF854D0E), Color(0xFFFFD700), Color(0xFFFEF08A))
    2 -> Triple(Color(0xFF334155), Color(0xFFE2E8F0), Color(0xFFF1F5F9))
    3 -> Triple(Color(0xFF7C2D12), Color(0xFFFDBA74), Color(0xFFFFEDD5))
    else -> Triple(Color.Transparent, Color(0xFF64748B), Color(0xFFE2E8F0))
  }

  Row(
    modifier = Modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(6.dp))
      .background(if (rank <= 3) badgeBg.copy(alpha = 0.25f) else Color(0x0CFFFFFF))
      .border(
        width = 1.dp,
        color = if (rank <= 3) rankColor.copy(alpha = 0.4f) else Color(0x10FFFFFF),
        shape = RoundedCornerShape(6.dp)
      )
      .padding(horizontal = 8.dp, vertical = 7.dp),
    verticalAlignment = Alignment.CenterVertically
  ) {
    // Rank number with badge
    Box(
      modifier = Modifier
        .width(36.dp)
        .height(24.dp)
        .clip(RoundedCornerShape(4.dp))
        .background(if (rank <= 3) badgeBg else Color.Transparent),
      contentAlignment = Alignment.Center
    ) {
      Text(
        text = if (rank == 1) "👑1" else "$rank",
        color = rankColor,
        fontSize = 12.sp,
        fontWeight = FontWeight.Black,
        fontFamily = FontFamily.Monospace
      )
    }

    // Player Initials
    Text(
      text = item.initials,
      color = if (rank <= 3) rankColor else Color.White,
      fontSize = 14.sp,
      fontWeight = FontWeight.Black,
      fontFamily = FontFamily.Monospace,
      letterSpacing = 1.5.sp,
      modifier = Modifier.width(54.dp)
    )

    // Score
    Text(
      text = formattedScore,
      color = textColor,
      fontSize = 13.sp,
      fontWeight = FontWeight.Bold,
      fontFamily = FontFamily.Monospace,
      modifier = Modifier.weight(1f),
      textAlign = TextAlign.End
    )

    // Level
    Text(
      text = "${item.level}",
      color = Color(0xFF38BDF8),
      fontSize = 12.sp,
      fontWeight = FontWeight.SemiBold,
      fontFamily = FontFamily.Monospace,
      modifier = Modifier.width(42.dp),
      textAlign = TextAlign.End
    )

    // Lines
    Text(
      text = "${item.lines}",
      color = Color(0xFF4ADE80),
      fontSize = 12.sp,
      fontWeight = FontWeight.SemiBold,
      fontFamily = FontFamily.Monospace,
      modifier = Modifier.width(42.dp),
      textAlign = TextAlign.End
    )
  }
}

private fun shareTopScore(
  context: Context,
  initials: String,
  score: Int,
  level: Int,
  lines: Int
) {
  val text = "🎮 Recorde no Tetris Arcade!\nJogador: $initials\nPontuação: $score pts\nNível: $level | Linhas: $lines\nConsegue bater meu recorde de fliperama?"
  val intent = Intent(Intent.ACTION_SEND).apply {
    type = "text/plain"
    putExtra(Intent.EXTRA_SUBJECT, "Tetris Arcade Recorde")
    putExtra(Intent.EXTRA_TEXT, text)
  }
  context.startActivity(Intent.createChooser(intent, "Compartilhar recorde"))
}
