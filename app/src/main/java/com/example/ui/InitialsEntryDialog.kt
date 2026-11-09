package com.example.ui

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
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import java.text.NumberFormat
import java.util.Locale

@Composable
fun InitialsEntryDialog(
  score: Int,
  level: Int,
  lines: Int,
  onSubmit: (String) -> Unit
) {
  var char1 by remember { mutableStateOf('A') }
  var char2 by remember { mutableStateOf('A') }
  var char3 by remember { mutableStateOf('A') }
  var activeSlot by remember { mutableIntStateOf(0) }

  val formattedScore = NumberFormat.getNumberInstance(Locale.getDefault()).format(score)

  val infiniteTransition = rememberInfiniteTransition(label = "arcadeBlink")
  val blinkAlpha by infiniteTransition.animateFloat(
    initialValue = 0.2f,
    targetValue = 1f,
    animationSpec = infiniteRepeatable(
      animation = tween(280, easing = LinearEasing),
      repeatMode = RepeatMode.Reverse
    ),
    label = "blinkAlpha"
  )

  Dialog(
    onDismissRequest = { onSubmit("$char1$char2$char3") },
    properties = DialogProperties(dismissOnBackPress = false, dismissOnClickOutside = false)
  ) {
    Box(
      modifier = Modifier
        .fillMaxWidth(0.92f)
        .clip(RoundedCornerShape(20.dp))
        .background(Brush.verticalGradient(listOf(Color(0xFF191238), Color(0xFF090614))))
        .border(
          width = 3.dp,
          brush = Brush.verticalGradient(listOf(Color(0xFFFFD700), Color(0xFFD500F9))),
          shape = RoundedCornerShape(20.dp)
        )
        .padding(20.dp)
        .testTag("initials_entry_dialog")
    ) {
      Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
      ) {
        // Arcade header
        Text(
          text = "★ NOVO RECORDE! ★",
          color = Color(0xFFFFD700),
          fontSize = 18.sp,
          fontWeight = FontWeight.Black,
          fontFamily = FontFamily.Monospace,
          letterSpacing = 2.sp,
          textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
          text = "FLIPERAMA HALL DA FAMA",
          color = Color(0xFFC084FC).copy(alpha = blinkAlpha),
          fontSize = 12.sp,
          fontWeight = FontWeight.Bold,
          fontFamily = FontFamily.Monospace,
          letterSpacing = 1.sp
        )

        Spacer(modifier = Modifier.height(14.dp))

        // Performance Summary Card
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(Color(0x33000000))
            .border(1.dp, Color(0x33FFFFFF), RoundedCornerShape(10.dp))
            .padding(12.dp)
        ) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
              Text("PONTOS", color = Color(0xFF94A3B8), fontSize = 11.sp, fontFamily = FontFamily.Monospace)
              Text(formattedScore, color = Color(0xFFFFD700), fontSize = 16.sp, fontWeight = FontWeight.Black, fontFamily = FontFamily.Monospace)
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
              Text("NÍVEL", color = Color(0xFF94A3B8), fontSize = 11.sp, fontFamily = FontFamily.Monospace)
              Text("$level", color = Color(0xFF38BDF8), fontSize = 16.sp, fontWeight = FontWeight.Black, fontFamily = FontFamily.Monospace)
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
              Text("LINHAS", color = Color(0xFF94A3B8), fontSize = 11.sp, fontFamily = FontFamily.Monospace)
              Text("$lines", color = Color(0xFF4ADE80), fontSize = 16.sp, fontWeight = FontWeight.Black, fontFamily = FontFamily.Monospace)
            }
          }
        }

        Spacer(modifier = Modifier.height(18.dp))

        Text(
          text = "DIGITE SUAS INICIAIS (3 LETRAS):",
          color = Color.White,
          fontSize = 12.sp,
          fontWeight = FontWeight.Bold,
          fontFamily = FontFamily.Monospace,
          letterSpacing = 1.sp
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Three Arcade Letter Slots
        Row(
          horizontalArrangement = Arrangement.spacedBy(16.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          LetterSlot(
            char = char1,
            isActive = activeSlot == 0,
            onSelect = { activeSlot = 0 },
            onUp = { char1 = nextChar(char1) },
            onDown = { char1 = prevChar(char1) },
            testTag = "initial_slot_1"
          )
          LetterSlot(
            char = char2,
            isActive = activeSlot == 1,
            onSelect = { activeSlot = 1 },
            onUp = { char2 = nextChar(char2) },
            onDown = { char2 = prevChar(char2) },
            testTag = "initial_slot_2"
          )
          LetterSlot(
            char = char3,
            isActive = activeSlot == 2,
            onSelect = { activeSlot = 2 },
            onUp = { char3 = nextChar(char3) },
            onDown = { char3 = prevChar(char3) },
            testTag = "initial_slot_3"
          )
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Fast Quick-Select letters row
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.Center
        ) {
          listOf('A', 'B', 'C', 'D', 'E', 'J', 'M', 'P', 'R', 'S', 'T', 'Z').forEach { quickChar ->
            Box(
              modifier = Modifier
                .padding(2.dp)
                .size(24.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(Color(0xFF261D4C))
                .clickable {
                  when (activeSlot) {
                    0 -> { char1 = quickChar; activeSlot = 1 }
                    1 -> { char2 = quickChar; activeSlot = 2 }
                    2 -> { char3 = quickChar }
                  }
                },
              contentAlignment = Alignment.Center
            ) {
              Text(
                text = "$quickChar",
                color = Color(0xFFC084FC),
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace
              )
            }
          }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Confirm Button
        Button(
          onClick = {
            onSubmit("$char1$char2$char3")
          },
          modifier = Modifier
            .fillMaxWidth()
            .height(50.dp)
            .testTag("submit_initials_button"),
          shape = RoundedCornerShape(12.dp),
          colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFF7C3AED)
          )
        ) {
          Icon(Icons.Default.Check, contentDescription = null, tint = Color.White)
          Spacer(modifier = Modifier.width(8.dp))
          Text(
            text = "SALVAR NO RANKING",
            color = Color.White,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Monospace,
            letterSpacing = 1.sp
          )
        }
      }
    }
  }
}

@Composable
private fun LetterSlot(
  char: Char,
  isActive: Boolean,
  onSelect: () -> Unit,
  onUp: () -> Unit,
  onDown: () -> Unit,
  testTag: String
) {
  Column(
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.Center
  ) {
    IconButton(
      onClick = onUp,
      modifier = Modifier.size(36.dp)
    ) {
      Icon(
        imageVector = Icons.Default.KeyboardArrowUp,
        contentDescription = "Letra anterior",
        tint = if (isActive) Color(0xFFFFD700) else Color(0xFF64748B)
      )
    }

    Box(
      modifier = Modifier
        .size(width = 54.dp, height = 64.dp)
        .clip(RoundedCornerShape(8.dp))
        .background(if (isActive) Color(0xFF3B1A5C) else Color(0xFF1E1B4B))
        .border(
          width = if (isActive) 2.5.dp else 1.dp,
          color = if (isActive) Color(0xFFFFD700) else Color(0xFF475569),
          shape = RoundedCornerShape(8.dp)
        )
        .clickable(onClick = onSelect)
        .testTag(testTag),
      contentAlignment = Alignment.Center
    ) {
      Text(
        text = "$char",
        color = if (isActive) Color(0xFFFFD700) else Color.White,
        fontSize = 32.sp,
        fontWeight = FontWeight.Black,
        fontFamily = FontFamily.Monospace
      )
    }

    IconButton(
      onClick = onDown,
      modifier = Modifier.size(36.dp)
    ) {
      Icon(
        imageVector = Icons.Default.KeyboardArrowDown,
        contentDescription = "Próxima letra",
        tint = if (isActive) Color(0xFFFFD700) else Color(0xFF64748B)
      )
    }
  }
}

private fun nextChar(c: Char): Char {
  return if (c >= 'Z') 'A' else c + 1
}

private fun prevChar(c: Char): Char {
  return if (c <= 'A') 'Z' else c - 1
}
