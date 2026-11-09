package com.example.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.audio.ArcadeMusicPlayer
import com.example.audio.ArcadeSoundManager
import com.example.audio.HapticFeedbackHelper
import com.example.data.AppDatabase
import com.example.data.ScoreEntity
import com.example.model.ActivePiece
import com.example.model.BOARD_HEIGHT
import com.example.model.BOARD_WIDTH
import com.example.model.GameStatus
import com.example.model.Point
import com.example.model.SevenBagRandomizer
import com.example.model.TetrisGameState
import com.example.model.TetrominoType
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class TetrisViewModel(application: Application) : AndroidViewModel(application) {
  private val db = AppDatabase.getInstance(application)
  private val scoreDao = db.scoreDao()

  val soundManager = ArcadeSoundManager()
  val musicPlayer = ArcadeMusicPlayer()
  val haptics = HapticFeedbackHelper(application)

  val topScores: StateFlow<List<ScoreEntity>> = scoreDao.getTopScores(50)
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  private val _uiState = MutableStateFlow(TetrisGameState())
  val uiState: StateFlow<TetrisGameState> = _uiState.asStateFlow()

  private val randomizer = SevenBagRandomizer()
  private var gameLoopJob: Job? = null
  private var lockDelayJob: Job? = null
  private var lineClearJob: Job? = null
  private var isLockDelayActive: Boolean = false
  private var isLineClearing: Boolean = false
  private var tetrisesInCurrentGame = 0

  init {
    // Observe database highest score
    viewModelScope.launch {
      scoreDao.getHighestScore().collect { maxScore ->
        _uiState.update { it.copy(highScore = maxScore ?: 0) }
      }
    }
    prepareNewGame()
  }

  private fun prepareNewGame() {
    randomizer.reset()
    tetrisesInCurrentGame = 0
    cancelLockDelay()
    lineClearJob?.cancel()
    isLineClearing = false
    val first = randomizer.next()
    val nexts = randomizer.peek(3)
    val startPiece = ActivePiece(type = first, position = first.initialPosition())
    val emptyGrid = List(BOARD_HEIGHT) { List(BOARD_WIDTH) { null } }
    val ghost = calculateGhost(emptyGrid, startPiece)

    _uiState.update {
      it.copy(
        grid = emptyGrid,
        currentPiece = startPiece,
        ghostPiecePosition = ghost,
        nextPieces = nexts,
        holdPiece = null,
        canHold = true,
        score = 0,
        level = 1,
        lines = 0,
        status = GameStatus.READY,
        clearingLines = emptyList(),
        isTetrisNoticeVisible = false
      )
    }
  }

  fun startGame() {
    if (_uiState.value.status == GameStatus.PLAYING) return
    _uiState.update { it.copy(status = GameStatus.PLAYING) }
    if (_uiState.value.musicEnabled) {
      musicPlayer.start()
    }
    startGameLoop()
  }

  fun pauseGame() {
    if (_uiState.value.status != GameStatus.PLAYING) return
    _uiState.update { it.copy(status = GameStatus.PAUSED) }
    musicPlayer.stop()
    cancelLockDelay()
    gameLoopJob?.cancel()
  }

  fun resumeGame() {
    if (_uiState.value.status != GameStatus.PAUSED) return
    _uiState.update { it.copy(status = GameStatus.PLAYING) }
    if (_uiState.value.musicEnabled) {
      musicPlayer.start()
    }
    startGameLoop()
  }

  fun restartGame() {
    musicPlayer.stop()
    cancelLockDelay()
    lineClearJob?.cancel()
    isLineClearing = false
    gameLoopJob?.cancel()
    prepareNewGame()
    startGame()
  }

  private fun startGameLoop() {
    gameLoopJob?.cancel()
    gameLoopJob = viewModelScope.launch {
      while (isActive && _uiState.value.status == GameStatus.PLAYING) {
        val dropInterval = getDropInterval(_uiState.value.level)
        delay(dropInterval)
        if (_uiState.value.status == GameStatus.PLAYING && !isLineClearing) {
          stepDown()
        }
      }
    }
  }

  private fun getDropInterval(level: Int): Long {
    return when (level) {
      1 -> 800L
      2 -> 720L
      3 -> 630L
      4 -> 550L
      5 -> 470L
      6 -> 380L
      7 -> 300L
      8 -> 220L
      9 -> 160L
      10 -> 120L
      else -> 90L
    }
  }

  private fun cancelLockDelay() {
    lockDelayJob?.cancel()
    lockDelayJob = null
    isLockDelayActive = false
  }

  private fun isLanded(grid: List<List<androidx.compose.ui.graphics.Color?>>, piece: ActivePiece): Boolean {
    val below = piece.copy(position = piece.position.copy(y = piece.position.y + 1))
    return !isValidPosition(grid, below)
  }

  private fun checkAndResetLockDelay(grid: List<List<androidx.compose.ui.graphics.Color?>>, piece: ActivePiece) {
    if (isLanded(grid, piece)) {
      // Piece is resting on the floor or another piece: restart 0.5s lock delay countdown
      startLockDelay(piece)
    } else {
      // Piece moved off an edge and is in free-fall again: cancel lock delay
      cancelLockDelay()
    }
  }

  private fun startLockDelay(piece: ActivePiece) {
    lockDelayJob?.cancel()
    isLockDelayActive = true
    lockDelayJob = viewModelScope.launch {
      delay(500L) // 0.5 seconds lock delay (allows moving/sliding and rotating)
      if (isActive && _uiState.value.status == GameStatus.PLAYING && !isLineClearing) {
        val current = _uiState.value.currentPiece
        if (current != null && isLanded(_uiState.value.grid, current)) {
          isLockDelayActive = false
          lockCurrentPiece(current)
        } else {
          isLockDelayActive = false
        }
      }
    }
  }

  fun moveLeft() {
    val state = _uiState.value
    if (state.status != GameStatus.PLAYING || isLineClearing) return
    val piece = state.currentPiece ?: return
    val newPos = piece.position.copy(x = piece.position.x - 1)
    val testPiece = piece.copy(position = newPos)

    if (isValidPosition(state.grid, testPiece)) {
      soundManager.playMove()
      haptics.lightClick()
      val ghost = calculateGhost(state.grid, testPiece)
      _uiState.update { it.copy(currentPiece = testPiece, ghostPiecePosition = ghost) }
      checkAndResetLockDelay(state.grid, testPiece)
    }
  }

  fun moveRight() {
    val state = _uiState.value
    if (state.status != GameStatus.PLAYING || isLineClearing) return
    val piece = state.currentPiece ?: return
    val newPos = piece.position.copy(x = piece.position.x + 1)
    val testPiece = piece.copy(position = newPos)

    if (isValidPosition(state.grid, testPiece)) {
      soundManager.playMove()
      haptics.lightClick()
      val ghost = calculateGhost(state.grid, testPiece)
      _uiState.update { it.copy(currentPiece = testPiece, ghostPiecePosition = ghost) }
      checkAndResetLockDelay(state.grid, testPiece)
    }
  }

  fun rotateClockwise() {
    rotate(clockwise = true)
  }

  fun rotateCounterClockwise() {
    rotate(clockwise = false)
  }

  private fun rotate(clockwise: Boolean) {
    val state = _uiState.value
    if (state.status != GameStatus.PLAYING || isLineClearing) return
    val piece = state.currentPiece ?: return
    val fromRot = piece.rotation
    val toRot = if (clockwise) (fromRot + 1) % 4 else (fromRot + 3) % 4

    val kicks = TetrominoType.getKicks(piece.type, fromRot, toRot)
    for (kick in kicks) {
      val testPiece = piece.copy(
        rotation = toRot,
        position = Point(piece.position.x + kick.x, piece.position.y - kick.y)
      )
      if (isValidPosition(state.grid, testPiece)) {
        soundManager.playRotate()
        haptics.lightClick()
        val ghost = calculateGhost(state.grid, testPiece)
        _uiState.update { it.copy(currentPiece = testPiece, ghostPiecePosition = ghost) }
        checkAndResetLockDelay(state.grid, testPiece)
        return
      }
    }
  }

  fun softDrop() {
    val state = _uiState.value
    if (state.status != GameStatus.PLAYING || isLineClearing) return
    soundManager.playSoftDrop()
    _uiState.update { it.copy(score = it.score + 1) }
    stepDown()
  }

  fun hardDrop() {
    val state = _uiState.value
    if (state.status != GameStatus.PLAYING || isLineClearing) return
    val piece = state.currentPiece ?: return
    val ghost = state.ghostPiecePosition ?: return

    cancelLockDelay()

    val dropDistance = ghost.y - piece.position.y
    val hardDropBonus = (dropDistance * 2).coerceAtLeast(0)

    soundManager.playHardDrop()
    haptics.heavyImpact()

    val lockedPiece = piece.copy(position = ghost)
    _uiState.update {
      it.copy(
        currentPiece = lockedPiece,
        score = it.score + hardDropBonus
      )
    }
    lockCurrentPiece(lockedPiece)
  }

  fun holdPiece() {
    val state = _uiState.value
    if (state.status != GameStatus.PLAYING || isLineClearing || !state.canHold) return
    val current = state.currentPiece ?: return

    cancelLockDelay()
    soundManager.playHold()
    haptics.lightClick()

    val previousHold = state.holdPiece
    val nextType = previousHold ?: randomizer.next()
    val newNextPieces = randomizer.peek(3)
    val newPiece = ActivePiece(type = nextType, position = nextType.initialPosition())
    val ghost = calculateGhost(state.grid, newPiece)

    _uiState.update {
      it.copy(
        currentPiece = newPiece,
        ghostPiecePosition = ghost,
        holdPiece = current.type,
        canHold = false,
        nextPieces = newNextPieces
      )
    }
  }

  private fun stepDown() {
    if (isLineClearing) return
    val state = _uiState.value
    val piece = state.currentPiece ?: return
    val nextPos = piece.position.copy(y = piece.position.y + 1)
    val testPiece = piece.copy(position = nextPos)

    if (isValidPosition(state.grid, testPiece)) {
      _uiState.update { it.copy(currentPiece = testPiece) }
      // After moving down, if it lands, start the lock delay
      if (isLanded(state.grid, testPiece)) {
        if (!isLockDelayActive) {
          startLockDelay(testPiece)
        }
      } else {
        cancelLockDelay()
      }
    } else {
      // Piece is touching the ground/blocks
      if (!isLockDelayActive) {
        startLockDelay(piece)
      }
    }
  }

  private fun lockCurrentPiece(piece: ActivePiece) {
    cancelLockDelay()
    val state = _uiState.value
    val newGrid = state.grid.map { it.toMutableList() }.toMutableList()

    var lockedAboveBoard = false
    for (b in piece.blocks()) {
      if (b.y < 0) {
        lockedAboveBoard = true
      } else if (b.y in 0 until BOARD_HEIGHT && b.x in 0 until BOARD_WIDTH) {
        newGrid[b.y][b.x] = piece.type.color
      }
    }

    if (lockedAboveBoard) {
      triggerGameOver()
      return
    }

    // Check completed lines
    val fullLines = mutableListOf<Int>()
    for (y in 0 until BOARD_HEIGHT) {
      if (newGrid[y].all { it != null }) {
        fullLines.add(y)
      }
    }

    if (fullLines.isNotEmpty()) {
      handleLineClears(newGrid, fullLines)
    } else {
      spawnNextPiece(newGrid.map { it.toList() })
    }
  }

  private fun handleLineClears(grid: List<List<androidx.compose.ui.graphics.Color?>>, fullLines: List<Int>) {
    val lineCount = fullLines.size
    soundManager.playLineClear(lineCount)
    if (lineCount >= 4) {
      tetrisesInCurrentGame++
      haptics.victoryPulse()
    } else {
      haptics.heavyImpact()
    }

    isLineClearing = true
    lineClearJob?.cancel()
    lineClearJob = viewModelScope.launch {
      // Clear current falling piece immediately so no phantom piece appears during line flash!
      _uiState.update {
        it.copy(
          grid = grid,
          currentPiece = null,
          ghostPiecePosition = null,
          clearingLines = fullLines,
          lastLineClearCount = lineCount,
          isTetrisNoticeVisible = lineCount >= 4
        )
      }
      delay(200)

      val remainingRows = grid.filterIndexed { index, _ -> !fullLines.contains(index) }
      val emptyRows = List(fullLines.size) { List(BOARD_WIDTH) { null } }
      val finalGrid = emptyRows + remainingRows

      val basePoints = when (lineCount) {
        1 -> 100
        2 -> 300
        3 -> 500
        else -> 800
      }
      val currentLevel = _uiState.value.level
      val addedScore = basePoints * currentLevel
      val newLines = _uiState.value.lines + lineCount
      val newLevel = (newLines / 10) + 1

      if (newLevel > currentLevel) {
        soundManager.playLevelUp()
        musicPlayer.setSpeedForLevel(newLevel)
      }

      val newScore = _uiState.value.score + addedScore
      val newHigh = maxOf(newScore, _uiState.value.highScore)

      _uiState.update {
        it.copy(
          grid = finalGrid,
          clearingLines = emptyList(),
          score = newScore,
          highScore = newHigh,
          lines = newLines,
          level = newLevel
        )
      }

      delay(150)
      _uiState.update { it.copy(isTetrisNoticeVisible = false) }

      isLineClearing = false
      spawnNextPiece(finalGrid)
    }
  }

  private fun spawnNextPiece(currentGrid: List<List<androidx.compose.ui.graphics.Color?>>) {
    val nextType = randomizer.next()
    val nextQueue = randomizer.peek(3)
    val newPiece = ActivePiece(type = nextType, position = nextType.initialPosition())

    if (!isValidPosition(currentGrid, newPiece)) {
      triggerGameOver()
      return
    }

    val ghost = calculateGhost(currentGrid, newPiece)
    _uiState.update {
      it.copy(
        grid = currentGrid,
        currentPiece = newPiece,
        ghostPiecePosition = ghost,
        nextPieces = nextQueue,
        canHold = true
      )
    }

    // Check if new piece immediately landed (e.g. board filled up near spawn)
    if (isLanded(currentGrid, newPiece)) {
      startLockDelay(newPiece)
    }
  }

  private fun triggerGameOver() {
    cancelLockDelay()
    lineClearJob?.cancel()
    isLineClearing = false
    gameLoopJob?.cancel()
    musicPlayer.stop()
    soundManager.playGameOver()
    haptics.victoryPulse()

    val currentScore = _uiState.value.score
    if (currentScore > 0) {
      // Transition to fliperama initials entry
      _uiState.update { it.copy(status = GameStatus.ENTERING_INITIALS) }
    } else {
      _uiState.update { it.copy(status = GameStatus.GAME_OVER) }
    }
  }

  fun submitInitials(initials: String) {
    val cleanInitials = initials.trim().take(3).ifEmpty { "VOC" }.uppercase()
    val score = _uiState.value.score
    val level = _uiState.value.level
    val lines = _uiState.value.lines

    viewModelScope.launch {
      scoreDao.insertScore(
        ScoreEntity(
          initials = cleanInitials,
          score = score,
          level = level,
          lines = lines,
          tetrises = tetrisesInCurrentGame
        )
      )
      _uiState.update { it.copy(status = GameStatus.GAME_OVER) }
    }
  }

  fun dismissInitials() {
    submitInitials("VOC")
  }

  fun toggleMusic() {
    val newVal = !_uiState.value.musicEnabled
    musicPlayer.isEnabled = newVal
    if (newVal && _uiState.value.status == GameStatus.PLAYING) {
      musicPlayer.start()
    } else {
      musicPlayer.stop()
    }
    _uiState.update { it.copy(musicEnabled = newVal) }
  }

  fun toggleSound() {
    val newVal = !_uiState.value.soundEnabled
    soundManager.isMuted = !newVal
    _uiState.update { it.copy(soundEnabled = newVal) }
  }

  fun toggleHaptics() {
    val newVal = !_uiState.value.hapticsEnabled
    haptics.isEnabled = newVal
    _uiState.update { it.copy(hapticsEnabled = newVal) }
  }

  fun toggleGhost() {
    _uiState.update { it.copy(ghostEnabled = !it.ghostEnabled) }
  }

  override fun onCleared() {
    super.onCleared()
    musicPlayer.stop()
    cancelLockDelay()
    lineClearJob?.cancel()
    isLineClearing = false
    gameLoopJob?.cancel()
  }

  private fun isValidPosition(
    grid: List<List<androidx.compose.ui.graphics.Color?>>,
    piece: ActivePiece
  ): Boolean {
    for (b in piece.blocks()) {
      if (b.x !in 0 until BOARD_WIDTH) return false
      if (b.y >= BOARD_HEIGHT) return false
      if (b.y >= 0 && grid[b.y][b.x] != null) return false
    }
    return true
  }

  private fun calculateGhost(
    grid: List<List<androidx.compose.ui.graphics.Color?>>,
    piece: ActivePiece
  ): Point {
    var curY = piece.position.y
    while (true) {
      val testPiece = piece.copy(position = piece.position.copy(y = curY + 1))
      if (isValidPosition(grid, testPiece)) {
        curY++
      } else {
        break
      }
    }
    return piece.position.copy(y = curY)
  }
}
