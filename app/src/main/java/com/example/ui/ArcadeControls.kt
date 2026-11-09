package com.example.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.RotateLeft
import androidx.compose.material.icons.automirrored.filled.RotateRight
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.ChangeCircle
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

@Composable
fun ArcadeControls(
  onMoveLeft: () -> Unit,
  onMoveRight: () -> Unit,
  onSoftDrop: () -> Unit,
  onHardDrop: () -> Unit,
  onRotateClockwise: () -> Unit,
  onRotateCounterClockwise: () -> Unit,
  onHold: () -> Unit,
  canHold: Boolean,
  modifier: Modifier = Modifier
) {
  Column(
    modifier = modifier
      .fillMaxWidth()
      .padding(horizontal = 12.dp, vertical = 6.dp),
    verticalArrangement = Arrangement.spacedBy(8.dp)
  ) {
    // Top utility buttons: HOLD & HARD DROP
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      ArcadePillButton(
        label = "HOLD",
        icon = Icons.Default.ChangeCircle,
        enabled = canHold,
        color = Color(0xFF673AB7),
        testTag = "hold_button",
        onClick = onHold
      )

      ArcadePillButton(
        label = "HARD DROP",
        icon = Icons.Default.Bolt,
        enabled = true,
        color = Color(0xFFFF9800),
        testTag = "hard_drop_button",
        onClick = onHardDrop
      )
    }

    // Main arcade console: D-Pad on Left, Action buttons on Right
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      // Left D-Pad (Left, Down, Right)
      Row(
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        RepeatingArcadeButton(
          icon = Icons.AutoMirrored.Filled.ArrowBack,
          contentDescription = "Esquerda",
          testTag = "left_button",
          color = Color(0xFF1E293B),
          borderColor = Color(0xFF38BDF8),
          size = 56,
          onAction = onMoveLeft
        )

        RepeatingArcadeButton(
          icon = Icons.Default.KeyboardArrowDown,
          contentDescription = "Descer",
          testTag = "soft_drop_button",
          color = Color(0xFF1E293B),
          borderColor = Color(0xFF4ADE80),
          size = 56,
          onAction = onSoftDrop
        )

        RepeatingArcadeButton(
          icon = Icons.AutoMirrored.Filled.ArrowForward,
          contentDescription = "Direita",
          testTag = "right_button",
          color = Color(0xFF1E293B),
          borderColor = Color(0xFF38BDF8),
          size = 56,
          onAction = onMoveRight
        )
      }

      // Right Action buttons (Rotate CCW, Rotate CW - Primary large)
      Row(
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        SingleArcadeRoundButton(
          icon = Icons.AutoMirrored.Filled.RotateLeft,
          contentDescription = "Girar Anti-horário",
          label = "↺",
          testTag = "rotate_ccw_button",
          color = Color(0xFF3B1A5C),
          borderColor = Color(0xFFC084FC),
          size = 52,
          onClick = onRotateCounterClockwise
        )

        SingleArcadeRoundButton(
          icon = Icons.AutoMirrored.Filled.RotateRight,
          contentDescription = "Girar Horário",
          label = "↻",
          testTag = "rotate_cw_button",
          color = Color(0xFF8B5CF6),
          borderColor = Color(0xFFE9D5FF),
          size = 64,
          isPrimary = true,
          onClick = onRotateClockwise
        )
      }
    }
  }
}

/**
 * Arcade button supporting Hold-to-Repeat (Delayed Auto Shift).
 */
@Composable
fun RepeatingArcadeButton(
  icon: ImageVector,
  contentDescription: String,
  testTag: String,
  color: Color,
  borderColor: Color,
  size: Int,
  onAction: () -> Unit,
  modifier: Modifier = Modifier
) {
  val scope = rememberCoroutineScope()
  var repeatJob by remember { mutableStateOf<Job?>(null) }
  var isPressed by remember { mutableStateOf(false) }

  Box(
    modifier = modifier
      .size(size.dp)
      .shadow(4.dp, RoundedCornerShape(14.dp))
      .clip(RoundedCornerShape(14.dp))
      .background(
        Brush.verticalGradient(
          if (isPressed) listOf(color.copy(alpha = 0.6f), color)
          else listOf(color, color.copy(alpha = 0.8f))
        )
      )
      .border(
        width = 2.dp,
        color = if (isPressed) Color.White else borderColor,
        shape = RoundedCornerShape(14.dp)
      )
      .testTag(testTag)
      .pointerInput(Unit) {
        detectTapGestures(
          onPress = {
            isPressed = true
            onAction()
            repeatJob?.cancel()
            repeatJob = scope.launch {
              delay(190) // Initial DAS delay
              while (isActive) {
                onAction()
                delay(60) // Auto-repeat rate
              }
            }
            tryAwaitRelease()
            repeatJob?.cancel()
            repeatJob = null
            isPressed = false
          }
        )
      },
    contentAlignment = Alignment.Center
  ) {
    Icon(
      imageVector = icon,
      contentDescription = contentDescription,
      tint = if (isPressed) Color.White else borderColor,
      modifier = Modifier.size((size * 0.52f).dp)
    )
  }
}

@Composable
fun SingleArcadeRoundButton(
  icon: ImageVector,
  contentDescription: String,
  label: String,
  testTag: String,
  color: Color,
  borderColor: Color,
  size: Int,
  isPrimary: Boolean = false,
  onClick: () -> Unit
) {
  Box(
    modifier = Modifier
      .size(size.dp)
      .shadow(if (isPrimary) 8.dp else 4.dp, CircleShape, ambientColor = borderColor, spotColor = borderColor)
      .clip(CircleShape)
      .background(
        Brush.verticalGradient(
          if (isPrimary) listOf(Color(0xFFA855F7), Color(0xFF6B21A8))
          else listOf(color, color.copy(alpha = 0.8f))
        )
      )
      .border(
        width = if (isPrimary) 3.dp else 2.dp,
        color = borderColor,
        shape = CircleShape
      )
      .clickable(
        interactionSource = remember { MutableInteractionSource() },
        indication = ripple(bounded = true, color = Color.White),
        onClick = onClick
      )
      .testTag(testTag),
    contentAlignment = Alignment.Center
  ) {
    Column(
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.Center
    ) {
      Icon(
        imageVector = icon,
        contentDescription = contentDescription,
        tint = Color.White,
        modifier = Modifier.size((size * 0.46f).dp)
      )
    }
  }
}

@Composable
fun ArcadePillButton(
  label: String,
  icon: ImageVector,
  enabled: Boolean,
  color: Color,
  testTag: String,
  onClick: () -> Unit
) {
  val activeColor = if (enabled) color else Color(0xFF334155)
  val textColor = if (enabled) Color.White else Color(0xFF64748B)

  Row(
    modifier = Modifier
      .height(44.dp)
      .shadow(3.dp, RoundedCornerShape(22.dp))
      .clip(RoundedCornerShape(22.dp))
      .background(Brush.horizontalGradient(listOf(activeColor, activeColor.copy(alpha = 0.8f))))
      .border(1.5.dp, if (enabled) activeColor.copy(alpha = 0.9f) else Color(0xFF475569), RoundedCornerShape(22.dp))
      .clickable(
        enabled = enabled,
        interactionSource = remember { MutableInteractionSource() },
        indication = ripple(bounded = true, color = Color.White),
        onClick = onClick
      )
      .padding(horizontal = 14.dp)
      .testTag(testTag),
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.spacedBy(6.dp)
  ) {
    Icon(
      imageVector = icon,
      contentDescription = label,
      tint = textColor,
      modifier = Modifier.size(18.dp)
    )
    Text(
      text = label,
      color = textColor,
      fontSize = 12.sp,
      fontWeight = FontWeight.Bold,
      fontFamily = FontFamily.Monospace,
      letterSpacing = 1.sp
    )
  }
}
