package com.example.games

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.random.Random

enum class GameType(val titleAr: String, val icon: String) {
    NONE("لا يوجد", ""),
    TRIVIA("مسابقة الذكاء والسرعة", "🧠"),
    SPIN_WHEEL("عجلة التحديات والصراحة", "🎡"),
    TIC_TAC_TOE("تحدي X-O الحماسي", "⚔️")
}

data class TriviaQuestion(
    val question: String,
    val options: List<String>,
    val correctIndex: Int,
    val category: String
)

data class WheelChallenge(
    val title: String,
    val description: String,
    val points: Int,
    val emoji: String
)

data class TriviaGameState(
    val currentQuestionIndex: Int = 0,
    val selectedOptionIndex: Int? = null,
    val isAnswerRevealed: Boolean = false,
    val timeRemainingSec: Int = 10,
    val myScore: Int = 0,
    val friendScores: Map<String, Int> = mapOf("عمر الفهد" to 20, "سارة المنصور" to 30, "خالد العتيبي" to 10),
    val isFinished: Boolean = false
)

data class WheelGameState(
    val isSpinning: Boolean = false,
    val targetRotationDegrees: Float = 0f,
    val selectedChallenge: WheelChallenge? = null,
    val assignedFriend: String = "أنا",
    val completedCount: Int = 0
)

data class TicTacToeState(
    val board: List<String> = List(9) { "" },
    val currentTurn: String = "X", // "X" for Me, "O" for Friend
    val winner: String? = null, // "X", "O", "DRAW", or null
    val winningLine: List<Int> = emptyList(),
    val myWins: Int = 0,
    val friendWins: Int = 0
)

data class ActiveGameState(
    val activeGame: GameType = GameType.NONE,
    val triviaState: TriviaGameState = TriviaGameState(),
    val wheelState: WheelGameState = WheelGameState(),
    val ticTacToeState: TicTacToeState = TicTacToeState()
)

class GamesManager(private val scope: CoroutineScope) {

    private val _gameState = MutableStateFlow(ActiveGameState())
    val gameState: StateFlow<ActiveGameState> = _gameState.asStateFlow()

    private var timerJob: Job? = null

    val triviaQuestions = listOf(
        TriviaQuestion(
            question = "ما هي عاصمة الأندلس التاريخية وأشهر مدنها الثقافية؟",
            options = listOf("قرطبة", "إشبيلية", "غرناطة", "طليطلة"),
            correctIndex = 0,
            category = "تاريخ وثقافة"
        ),
        TriviaQuestion(
            question = "ما هو البروتوكول التشفيري الأكثر أماناً المستخدم في رسائل E2EE العالمية؟",
            options = listOf("Signal Protocol & AES-256", "MD5 Hash", "DES Simple", "Base64 Raw"),
            correctIndex = 0,
            category = "أمان وتقنية"
        ),
        TriviaQuestion(
            question = "كم عدد الأوتار في آلة العود الموسيقية التقليدية الشائعة؟",
            options = listOf("4 أو 5", "5 أو 6 أوتار مزدوجة", "8 أوتار فردية", "3 أوتار"),
            correctIndex = 1,
            category = "موسيقى وفنون"
        ),
        TriviaQuestion(
            question = "ما هو الكوكب الأقرب إلى الشمس في المجموعة الشمسية؟",
            options = listOf("الزهرة", "عطارد", "المريخ", "المشتري"),
            correctIndex = 1,
            category = "علوم وفضاء"
        ),
        TriviaQuestion(
            question = "في الألعاب الإلكترونية، ماذا يعني اختصار FPS؟",
            options = listOf("Frames Per Second / First-Person Shooter", "Fast Player Score", "Final Power Shield", "Future Planet Survival"),
            correctIndex = 0,
            category = "ألعاب وقيمنق"
        )
    )

    val wheelChallenges = listOf(
        WheelChallenge(
            title = "تحدي النبرة الصوتية 🎤",
            description = "سجل مقطع صوتي في الغرفة وغني 10 ثواني من أغنيتك المفضلة الآن!",
            points = 20,
            emoji = "🎤"
        ),
        WheelChallenge(
            title = "سؤال الصراحة المطلقة 🤫",
            description = "ما هو أكثر تصرف مضحك أو محرج قمت به هذا الأسبوع؟",
            points = 15,
            emoji = "🤫"
        ),
        WheelChallenge(
            title = "تحدي اللهجات 🌍",
            description = "تكلم لمدة دقيقة بلهجة عربية مختلفة تماماً عن لهجتك الأصلية!",
            points = 25,
            emoji = "🌍"
        ),
        WheelChallenge(
            title = "دي جي الغرفة 🎧",
            description = "اختر الأغنية التالية في مشغل الموسيقى المشترك وشاركها مع أصدقائك!",
            points = 10,
            emoji = "🎧"
        ),
        WheelChallenge(
            title = "تحدي اللغز السريع ⚡",
            description = "شيء كلما أخذت منه كَبُر.. ما هو؟ (أجب خلال 15 ثانية)",
            points = 20,
            emoji = "⚡"
        ),
        WheelChallenge(
            title = "مدح الصديق 🌟",
            description = "اذكر أفضل ميزة تعجبك في صديقك عمر أو سارة في هذه الغرفة!",
            points = 15,
            emoji = "🌟"
        )
    )

    fun launchGame(gameType: GameType) {
        _gameState.value = _gameState.value.copy(
            activeGame = gameType,
            triviaState = TriviaGameState(),
            wheelState = WheelGameState(),
            ticTacToeState = TicTacToeState()
        )

        if (gameType == GameType.TRIVIA) {
            startTriviaTimer()
        }
    }

    fun closeGame() {
        timerJob?.cancel()
        timerJob = null
        _gameState.value = _gameState.value.copy(activeGame = GameType.NONE)
    }

    // --- Trivia Logic ---
    private fun startTriviaTimer() {
        timerJob?.cancel()
        timerJob = scope.launch(Dispatchers.Default) {
            var timeLeft = 10
            while (isActive && timeLeft > 0) {
                _gameState.value = _gameState.value.copy(
                    triviaState = _gameState.value.triviaState.copy(timeRemainingSec = timeLeft)
                )
                delay(1000)
                timeLeft--
            }
            // Auto reveal if not answered
            if (!_gameState.value.triviaState.isAnswerRevealed) {
                revealTriviaAnswer(null)
            }
        }
    }

    fun answerTrivia(optionIndex: Int) {
        if (_gameState.value.triviaState.isAnswerRevealed) return
        revealTriviaAnswer(optionIndex)
    }

    private fun revealTriviaAnswer(selectedOption: Int?) {
        timerJob?.cancel()
        val currentIdx = _gameState.value.triviaState.currentQuestionIndex
        val q = triviaQuestions[currentIdx]
        val isCorrect = selectedOption == q.correctIndex
        val addedPoints = if (isCorrect) 10 else 0

        val newScores = _gameState.value.triviaState.friendScores.toMutableMap()
        // Simulate friends answering too
        if (Random.nextBoolean()) {
            newScores["عمر الفهد"] = (newScores["عمر الفهد"] ?: 0) + 10
        }
        if (Random.nextBoolean()) {
            newScores["سارة المنصور"] = (newScores["سارة المنصور"] ?: 0) + 10
        }

        _gameState.value = _gameState.value.copy(
            triviaState = _gameState.value.triviaState.copy(
                selectedOptionIndex = selectedOption,
                isAnswerRevealed = true,
                myScore = _gameState.value.triviaState.myScore + addedPoints,
                friendScores = newScores
            )
        )
    }

    fun nextTriviaQuestion(): Boolean {
        val nextIdx = _gameState.value.triviaState.currentQuestionIndex + 1
        return if (nextIdx < triviaQuestions.size) {
            _gameState.value = _gameState.value.copy(
                triviaState = _gameState.value.triviaState.copy(
                    currentQuestionIndex = nextIdx,
                    selectedOptionIndex = null,
                    isAnswerRevealed = false,
                    timeRemainingSec = 10
                )
            )
            startTriviaTimer()
            true
        } else {
            _gameState.value = _gameState.value.copy(
                triviaState = _gameState.value.triviaState.copy(isFinished = true)
            )
            false
        }
    }

    // --- Spin Wheel Logic ---
    fun spinWheel() {
        if (_gameState.value.wheelState.isSpinning) return

        val friends = listOf("أنا", "عمر الفهد ⚡", "سارة المنصور 🎨", "خالد العتيبي 🚀")
        val chosenFriend = friends.random()
        val chosenChallenge = wheelChallenges.random()
        val extraSpins = 4 + Random.nextInt(4)
        val targetDeg = _gameState.value.wheelState.targetRotationDegrees + 360f * extraSpins + Random.nextInt(360)

        _gameState.value = _gameState.value.copy(
            wheelState = _gameState.value.wheelState.copy(
                isSpinning = true,
                targetRotationDegrees = targetDeg,
                selectedChallenge = null,
                assignedFriend = chosenFriend
            )
        )

        scope.launch {
            delay(2500)
            _gameState.value = _gameState.value.copy(
                wheelState = _gameState.value.wheelState.copy(
                    isSpinning = false,
                    selectedChallenge = chosenChallenge
                )
            )
        }
    }

    fun completeWheelChallenge() {
        _gameState.value = _gameState.value.copy(
            wheelState = _gameState.value.wheelState.copy(
                completedCount = _gameState.value.wheelState.completedCount + 1,
                selectedChallenge = null
            )
        )
    }

    // --- Tic Tac Toe Logic ---
    fun onTicTacToeCellClick(index: Int) {
        val st = _gameState.value.ticTacToeState
        if (st.winner != null || st.board[index].isNotEmpty()) return

        val newBoard = st.board.toMutableList()
        newBoard[index] = st.currentTurn

        val win = checkTicTacToeWinner(newBoard)
        if (win != null) {
            val (winnerSymbol, line) = win
            _gameState.value = _gameState.value.copy(
                ticTacToeState = st.copy(
                    board = newBoard,
                    winner = winnerSymbol,
                    winningLine = line,
                    myWins = if (winnerSymbol == "X") st.myWins + 1 else st.myWins,
                    friendWins = if (winnerSymbol == "O") st.friendWins + 1 else st.friendWins
                )
            )
            return
        }

        if (newBoard.none { it.isEmpty() }) {
            _gameState.value = _gameState.value.copy(
                ticTacToeState = st.copy(board = newBoard, winner = "DRAW")
            )
            return
        }

        // Switch turn to friend (O) and let friend move automatically after short delay
        _gameState.value = _gameState.value.copy(
            ticTacToeState = st.copy(board = newBoard, currentTurn = "O")
        )

        scope.launch {
            delay(600)
            makeFriendMove()
        }
    }

    private fun makeFriendMove() {
        val st = _gameState.value.ticTacToeState
        if (st.winner != null || st.currentTurn != "O") return

        val emptyIndices = st.board.indices.filter { st.board[it].isEmpty() }
        if (emptyIndices.isEmpty()) return

        val move = emptyIndices.random()
        val newBoard = st.board.toMutableList()
        newBoard[move] = "O"

        val win = checkTicTacToeWinner(newBoard)
        if (win != null) {
            val (winnerSymbol, line) = win
            _gameState.value = _gameState.value.copy(
                ticTacToeState = st.copy(
                    board = newBoard,
                    winner = winnerSymbol,
                    winningLine = line,
                    friendWins = st.friendWins + 1,
                    currentTurn = "X"
                )
            )
        } else if (newBoard.none { it.isEmpty() }) {
            _gameState.value = _gameState.value.copy(
                ticTacToeState = st.copy(board = newBoard, winner = "DRAW", currentTurn = "X")
            )
        } else {
            _gameState.value = _gameState.value.copy(
                ticTacToeState = st.copy(board = newBoard, currentTurn = "X")
            )
        }
    }

    fun resetTicTacToe() {
        _gameState.value = _gameState.value.copy(
            ticTacToeState = _gameState.value.ticTacToeState.copy(
                board = List(9) { "" },
                currentTurn = "X",
                winner = null,
                winningLine = emptyList()
            )
        )
    }

    private fun checkTicTacToeWinner(board: List<String>): Pair<String, List<Int>>? {
        val lines = listOf(
            listOf(0, 1, 2), listOf(3, 4, 5), listOf(6, 7, 8), // rows
            listOf(0, 3, 6), listOf(1, 4, 7), listOf(2, 5, 8), // cols
            listOf(0, 4, 8), listOf(2, 4, 6)             // diagonals
        )
        for (line in lines) {
            val a = board[line[0]]
            val b = board[line[1]]
            val c = board[line[2]]
            if (a.isNotEmpty() && a == b && b == c) {
                return Pair(a, line)
            }
        }
        return null
    }
}
