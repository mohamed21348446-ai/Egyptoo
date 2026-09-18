package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Badge
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.games.ActiveGameState
import com.example.games.GameType
import com.example.games.GamesManager
import com.example.ui.theme.WhatsAppAccentCyan
import com.example.ui.theme.WhatsAppAccentPurple
import com.example.ui.theme.WhatsAppDarkBackground
import com.example.ui.theme.WhatsAppEmerald
import com.example.ui.theme.WhatsAppGreen
import com.example.ui.theme.WhatsAppSecurityGold
import com.example.ui.theme.WhatsAppSurfaceVariant
import com.example.ui.theme.WhatsAppTextPrimary
import com.example.ui.theme.WhatsAppTextSecondary

@Composable
fun InChatGameWidget(
    gameState: ActiveGameState,
    gamesManager: GamesManager,
    onPostResultToChat: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    if (gameState.activeGame == GameType.NONE) return

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp)
            .testTag("in_chat_game_widget"),
        shape = RoundedCornerShape(16.dp),
        color = WhatsAppSurfaceVariant,
        tonalElevation = 8.dp,
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            Brush.horizontalGradient(
                listOf(WhatsAppAccentPurple.copy(alpha = 0.6f), WhatsAppAccentCyan.copy(alpha = 0.4f))
            )
        )
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            // Header with Game Title and Close button
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = gameState.activeGame.icon, fontSize = 20.sp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = gameState.activeGame.titleAr,
                        color = WhatsAppTextPrimary,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                IconButton(
                    onClick = { gamesManager.closeGame() },
                    modifier = Modifier.size(28.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "إغلاق التحدي",
                        tint = WhatsAppTextSecondary,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            when (gameState.activeGame) {
                GameType.TRIVIA -> TriviaGameContent(
                    gameState = gameState,
                    gamesManager = gamesManager,
                    onPostResultToChat = onPostResultToChat
                )
                GameType.SPIN_WHEEL -> SpinWheelContent(
                    gameState = gameState,
                    gamesManager = gamesManager,
                    onPostResultToChat = onPostResultToChat
                )
                GameType.TIC_TAC_TOE -> TicTacToeContent(
                    gameState = gameState,
                    gamesManager = gamesManager,
                    onPostResultToChat = onPostResultToChat
                )
                GameType.NONE -> {}
            }
        }
    }
}

@Composable
private fun TriviaGameContent(
    gameState: ActiveGameState,
    gamesManager: GamesManager,
    onPostResultToChat: (String) -> Unit
) {
    val tState = gameState.triviaState
    val questions = gamesManager.triviaQuestions

    if (tState.isFinished) {
        // Game Finished Card
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Default.EmojiEvents,
                contentDescription = null,
                tint = WhatsAppSecurityGold,
                modifier = Modifier.size(48.dp)
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "انتهت المسابقة! نتيجتك: ${tState.myScore} نقطة 🏆",
                color = WhatsAppTextPrimary,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "عمر: ${tState.friendScores["عمر الفهد"]} | سارة: ${tState.friendScores["سارة المنصور"]}",
                color = WhatsAppTextSecondary,
                fontSize = 12.sp
            )
            Spacer(modifier = Modifier.height(10.dp))
            Button(
                onClick = {
                    onPostResultToChat("🏆 فزت في مسابقة الذكاء والسرعة بـ ${tState.myScore} نقطة! مين يتحداني الجولة الجاية؟")
                    gamesManager.closeGame()
                },
                colors = ButtonDefaults.buttonColors(containerColor = WhatsAppEmerald)
            ) {
                Text("مشاركة النتيجة في المحادثة", color = Color.Black, fontWeight = FontWeight.Bold)
            }
        }
        return
    }

    val currentQ = questions[tState.currentQuestionIndex]

    // Category and Progress Row
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Badge(containerColor = WhatsAppAccentCyan.copy(alpha = 0.2f), contentColor = WhatsAppAccentCyan) {
            Text(currentQ.category, fontSize = 10.sp, modifier = Modifier.padding(2.dp))
        }
        Text(
            text = "السؤال ${tState.currentQuestionIndex + 1} من ${questions.size} (نقاطك: ${tState.myScore})",
            color = WhatsAppTextSecondary,
            fontSize = 11.sp
        )
    }

    Spacer(modifier = Modifier.height(6.dp))

    // Question Text
    Text(
        text = currentQ.question,
        color = WhatsAppTextPrimary,
        fontSize = 14.sp,
        fontWeight = FontWeight.SemiBold,
        modifier = Modifier.fillMaxWidth()
    )

    Spacer(modifier = Modifier.height(6.dp))

    // Timer bar
    val timerProgress = (tState.timeRemainingSec / 10f).coerceIn(0f, 1f)
    LinearProgressIndicator(
        progress = { timerProgress },
        modifier = Modifier
            .fillMaxWidth()
            .height(4.dp)
            .clip(RoundedCornerShape(2.dp)),
        color = if (tState.timeRemainingSec <= 3) Color.Red else WhatsAppGreen,
        trackColor = WhatsAppDarkBackground
    )

    Spacer(modifier = Modifier.height(8.dp))

    // 4 Options Grid (2x2)
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        currentQ.options.chunked(2).forEachIndexed { rowIdx, pair ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                pair.forEachIndexed { colIdx, optText ->
                    val optIdx = rowIdx * 2 + colIdx
                    val isSelected = tState.selectedOptionIndex == optIdx
                    val isCorrect = optIdx == currentQ.correctIndex

                    val bgColor = when {
                        !tState.isAnswerRevealed -> if (isSelected) WhatsAppEmerald.copy(alpha = 0.3f) else WhatsAppDarkBackground
                        isCorrect -> WhatsAppGreen.copy(alpha = 0.35f)
                        isSelected && !isCorrect -> Color.Red.copy(alpha = 0.35f)
                        else -> WhatsAppDarkBackground
                    }

                    val borderColor = when {
                        !tState.isAnswerRevealed -> if (isSelected) WhatsAppEmerald else Color.Transparent
                        isCorrect -> WhatsAppGreen
                        isSelected && !isCorrect -> Color.Red
                        else -> Color.Transparent
                    }

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(bgColor)
                            .border(1.dp, borderColor, RoundedCornerShape(8.dp))
                            .clickable(enabled = !tState.isAnswerRevealed) {
                                gamesManager.answerTrivia(optIdx)
                            }
                            .padding(vertical = 10.dp, horizontal = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = optText,
                            color = WhatsAppTextPrimary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }

    // Next Question Button if answered
    if (tState.isAnswerRevealed) {
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            FilledTonalButton(
                onClick = { gamesManager.nextTriviaQuestion() },
                colors = ButtonDefaults.filledTonalButtonColors(
                    containerColor = WhatsAppEmerald,
                    contentColor = Color.Black
                )
            ) {
                Text("السؤال التالي ➡️", fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun SpinWheelContent(
    gameState: ActiveGameState,
    gamesManager: GamesManager,
    onPostResultToChat: (String) -> Unit
) {
    val wState = gameState.wheelState

    val animatedRotation by animateFloatAsState(
        targetValue = wState.targetRotationDegrees,
        animationSpec = tween(durationMillis = 2400, easing = FastOutSlowInEasing),
        label = "wheelRotation"
    )

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Animated Wheel representation
        Box(
            modifier = Modifier
                .size(120.dp)
                .rotate(animatedRotation),
            contentAlignment = Alignment.Center
        ) {
            Canvas(modifier = Modifier.size(110.dp)) {
                val colors = listOf(
                    Color(0xFF00A884), Color(0xFF00D2FF), Color(0xFFA855F7),
                    Color(0xFFFFD279), Color(0xFFFF5252), Color(0xFF25D366)
                )
                val sweep = 360f / colors.size
                colors.forEachIndexed { i, c ->
                    drawArc(
                        color = c,
                        startAngle = i * sweep,
                        sweepAngle = sweep,
                        useCenter = true
                    )
                }
            }
            // Center pin
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .clip(CircleShape)
                    .background(Color.White)
                    .border(2.dp, Color.Black, CircleShape)
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        if (wState.selectedChallenge != null) {
            val chal = wState.selectedChallenge
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                color = WhatsAppDarkBackground,
                border = androidx.compose.foundation.BorderStroke(1.dp, WhatsAppSecurityGold.copy(alpha = 0.5f))
            ) {
                Column(modifier = Modifier.padding(10.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "المختار للتحدي: ${wState.assignedFriend} 🎯",
                        color = WhatsAppSecurityGold,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = chal.title,
                        color = WhatsAppTextPrimary,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = chal.description,
                        color = WhatsAppTextSecondary,
                        fontSize = 12.sp,
                        textAlign = TextAlign.Center
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(
                    onClick = {
                        onPostResultToChat("🎡 تحدي العجلة مع ${wState.assignedFriend}: ${chal.title}!\n${chal.description}")
                        gamesManager.completeWheelChallenge()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = WhatsAppEmerald)
                ) {
                    Icon(Icons.Default.Check, contentDescription = null, tint = Color.Black)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("إنجاز التحدي (+${chal.points} نقطة)", color = Color.Black)
                }

                OutlinedButton(onClick = { gamesManager.spinWheel() }) {
                    Text("تدوير مجدداً 🎡", color = WhatsAppTextPrimary)
                }
            }
        } else {
            Button(
                onClick = { gamesManager.spinWheel() },
                enabled = !wState.isSpinning,
                colors = ButtonDefaults.buttonColors(containerColor = WhatsAppEmerald)
            ) {
                Text(
                    text = if (wState.isSpinning) "العجلة تدور الآن... 🎡" else "تدوير عجلة التحدي 🎡",
                    color = Color.Black,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun TicTacToeContent(
    gameState: ActiveGameState,
    gamesManager: GamesManager,
    onPostResultToChat: (String) -> Unit
) {
    val tState = gameState.ticTacToeState

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Status & Score
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = when {
                    tState.winner == "X" -> "🎉 فزت بالجولة!"
                    tState.winner == "O" -> "فاز الصديق (عمر)!"
                    tState.winner == "DRAW" -> "تعادل حماسي!"
                    tState.currentTurn == "X" -> "دورك باللعب (X)"
                    else -> "عمر يفكر بحركته (O)..."
                },
                color = if (tState.winner == "X") WhatsAppGreen else WhatsAppTextPrimary,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = "أنا: ${tState.myWins} | عمر: ${tState.friendWins}",
                color = WhatsAppTextSecondary,
                fontSize = 12.sp
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        // 3x3 Grid
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            for (row in 0..2) {
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    for (col in 0..2) {
                        val index = row * 3 + col
                        val cellValue = tState.board[index]
                        val isWinCell = tState.winningLine.contains(index)

                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(
                                    if (isWinCell) WhatsAppGreen.copy(alpha = 0.35f) else WhatsAppDarkBackground
                                )
                                .border(
                                    1.dp,
                                    if (isWinCell) WhatsAppGreen else WhatsAppSurfaceVariant,
                                    RoundedCornerShape(8.dp)
                                )
                                .clickable(enabled = cellValue.isEmpty() && tState.winner == null && tState.currentTurn == "X") {
                                    gamesManager.onTicTacToeCellClick(index)
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = cellValue,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = if (cellValue == "X") WhatsAppEmerald else WhatsAppAccentCyan
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedButton(onClick = { gamesManager.resetTicTacToe() }) {
                Icon(Icons.Default.Refresh, contentDescription = null, tint = WhatsAppTextPrimary, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("جولة جديدة", color = WhatsAppTextPrimary, fontSize = 11.sp)
            }

            if (tState.winner != null) {
                Button(
                    onClick = {
                        val winMsg = if (tState.winner == "X") "🏆 فزت على عمر بجولة X-O حماسية!" else "⚔️ لعبنا جولة X-O قوية في الغرفة!"
                        onPostResultToChat(winMsg)
                        gamesManager.closeGame()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = WhatsAppEmerald)
                ) {
                    Text("مشاركة النتيجة", color = Color.Black, fontSize = 11.sp)
                }
            }
        }
    }
}
