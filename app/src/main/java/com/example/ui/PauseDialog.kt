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
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Vibration
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties

@Composable
fun PauseDialog(
  musicEnabled: Boolean,
  soundEnabled: Boolean,
  hapticsEnabled: Boolean,
  ghostEnabled: Boolean,
  onResume: () -> Unit,
  onRestart: () -> Unit,
  onOpenLeaderboard: () -> Unit,
  onToggleMusic: () -> Unit,
  onToggleSound: () -> Unit,
  onToggleHaptics: () -> Unit,
  onToggleGhost: () -> Unit
) {
  Dialog(
    onDismissRequest = onResume,
    properties = DialogProperties(dismissOnBackPress = true, dismissOnClickOutside = true)
  ) {
    Box(
      modifier = Modifier
        .fillMaxWidth(0.92f)
        .clip(RoundedCornerShape(20.dp))
        .background(Brush.verticalGradient(listOf(Color(0xFF191238), Color(0xFF090614))))
        .border(
          width = 2.dp,
          brush = Brush.verticalGradient(listOf(Color(0xFF8B5CF6), Color(0xFF00E5FF))),
          shape = RoundedCornerShape(20.dp)
        )
        .padding(20.dp)
        .testTag("pause_dialog")
    ) {
      Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
      ) {
        Text(
          text = "⏸ PAUSA",
          color = Color(0xFF00E5FF),
          fontSize = 20.sp,
          fontWeight = FontWeight.Black,
          fontFamily = FontFamily.Monospace,
          letterSpacing = 2.sp
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Primary Resume Button
        Button(
          onClick = onResume,
          modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
            .testTag("resume_button"),
          shape = RoundedCornerShape(10.dp),
          colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF7C3AED))
        ) {
          Icon(Icons.Default.PlayArrow, contentDescription = null, tint = Color.White)
          Spacer(modifier = Modifier.width(8.dp))
          Text("CONTINUAR", fontSize = 14.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Restart Button
        OutlinedButton(
          onClick = onRestart,
          modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
            .testTag("restart_button"),
          shape = RoundedCornerShape(10.dp),
          colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFF43F5E))
        ) {
          Icon(Icons.Default.Refresh, contentDescription = null, tint = Color(0xFFF43F5E))
          Spacer(modifier = Modifier.width(8.dp))
          Text("REINICIAR PARTIDA", fontSize = 13.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Hall of fame button
        OutlinedButton(
          onClick = onOpenLeaderboard,
          modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
            .testTag("pause_leaderboard_button"),
          shape = RoundedCornerShape(10.dp),
          colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFFFD700))
        ) {
          Icon(Icons.Default.EmojiEvents, contentDescription = null, tint = Color(0xFFFFD700))
          Spacer(modifier = Modifier.width(8.dp))
          Text("HALL DA FAMA (RANKING)", fontSize = 12.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
        }

        Spacer(modifier = Modifier.height(16.dp))
        HorizontalDivider(color = Color(0x22FFFFFF))
        Spacer(modifier = Modifier.height(12.dp))

        Text(
          text = "CONFIGURAÇÕES RETRO",
          color = Color(0xFF94A3B8),
          fontSize = 11.sp,
          fontWeight = FontWeight.Bold,
          fontFamily = FontFamily.Monospace,
          letterSpacing = 1.sp
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Settings toggles
        ToggleRow(
          icon = Icons.Default.MusicNote,
          label = "Música (Korobeiniki)",
          checked = musicEnabled,
          onCheckedChange = { onToggleMusic() },
          testTag = "music_toggle"
        )

        ToggleRow(
          icon = Icons.Default.GraphicEq,
          label = "Efeitos Sonoros",
          checked = soundEnabled,
          onCheckedChange = { onToggleSound() },
          testTag = "sound_toggle"
        )

        ToggleRow(
          icon = Icons.Default.Vibration,
          label = "Vibração Tátil",
          checked = hapticsEnabled,
          onCheckedChange = { onToggleHaptics() },
          testTag = "haptics_toggle"
        )

        ToggleRow(
          icon = Icons.Default.Visibility,
          label = "Peça Fantasma",
          checked = ghostEnabled,
          onCheckedChange = { onToggleGhost() },
          testTag = "ghost_toggle"
        )
      }
    }
  }
}

@Composable
private fun ToggleRow(
  icon: ImageVector,
  label: String,
  checked: Boolean,
  onCheckedChange: (Boolean) -> Unit,
  testTag: String
) {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .padding(vertical = 2.dp),
    horizontalArrangement = Arrangement.SpaceBetween,
    verticalAlignment = Alignment.CenterVertically
  ) {
    Row(
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
      Icon(
        imageVector = icon,
        contentDescription = null,
        tint = if (checked) Color(0xFF00E5FF) else Color(0xFF64748B),
        modifier = Modifier.size(18.dp)
      )
      Text(
        text = label,
        color = if (checked) Color.White else Color(0xFF94A3B8),
        fontSize = 12.sp,
        fontFamily = FontFamily.Monospace
      )
    }

    Switch(
      checked = checked,
      onCheckedChange = onCheckedChange,
      modifier = Modifier.testTag(testTag),
      colors = SwitchDefaults.colors(
        checkedThumbColor = Color(0xFF00E5FF),
        checkedTrackColor = Color(0xFF1E3A8A),
        uncheckedThumbColor = Color(0xFF94A3B8),
        uncheckedTrackColor = Color(0xFF1E293B)
      )
    )
  }
}
