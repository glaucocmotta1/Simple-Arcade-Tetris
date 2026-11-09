package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import com.example.ui.TetrisScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.viewmodel.TetrisViewModel

class MainActivity : ComponentActivity() {
  private val viewModel: TetrisViewModel by viewModels()

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContent {
      MyApplicationTheme {
        TetrisScreen(viewModel = viewModel)
      }
    }
  }

  override fun onPause() {
    super.onPause()
    viewModel.pauseGame()
  }
}
