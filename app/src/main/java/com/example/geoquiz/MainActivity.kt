package com.example.geoquiz

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {

    private val questions = listOf(
        Question(R.string.question_nepal, true),
        Question(R.string.question_australia, false),
        Question(R.string.question_pacific, true),
        Question(R.string.question_brazil, false),
        Question(R.string.question_sahara, true),
        Question(R.string.question_japan, false)
    )

    private var currentIndex = 0
    private var score = 0

    // Each question can contribute to the score only once.
    private var answered = BooleanArray(questions.size)

    private lateinit var questionText: TextView
    private lateinit var progressText: TextView
    private lateinit var scoreText: TextView
    private lateinit var statusText: TextView
    private lateinit var trueButton: Button
    private lateinit var falseButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        // Keep the content clear of system bars and display cutouts.
        ViewCompat.setOnApplyWindowInsetsListener(
            findViewById<View>(R.id.main)
        ) { view, insets ->
            val bars = insets.getInsets(
                WindowInsetsCompat.Type.systemBars() or
                        WindowInsetsCompat.Type.displayCutout()
            )
            view.setPadding(
                bars.left,
                bars.top,
                bars.right,
                bars.bottom
            )
            insets
        }

        questionText = findViewById(R.id.questionText)
        progressText = findViewById(R.id.progressText)
        scoreText = findViewById(R.id.scoreText)
        statusText = findViewById(R.id.statusText)
        trueButton = findViewById(R.id.trueButton)
        falseButton = findViewById(R.id.falseButton)

        // Restore progress after an activity recreation.
        savedInstanceState?.let { state ->
            currentIndex = state.getInt(KEY_INDEX)
            score = state.getInt(KEY_SCORE)
            answered = state.getBooleanArray(KEY_ANSWERED)
                ?: BooleanArray(questions.size)
        }

        trueButton.setOnClickListener {
            checkAnswer(true)
        }

        falseButton.setOnClickListener {
            checkAnswer(false)
        }

        findViewById<Button>(R.id.nextButton).setOnClickListener {
            // After the last question, return to the first.
            currentIndex = (currentIndex + 1) % questions.size
            updateScreen()
        }

        findViewById<Button>(R.id.cheatButton).setOnClickListener {
            val question = questions[currentIndex]

            // Explicit Intent: name the Activity to open.
            val cheatIntent = Intent(this, CheatActivity::class.java)

            cheatIntent.putExtra(
                CheatActivity.EXTRA_QUESTION,
                getString(question.textResId)
            )

            cheatIntent.putExtra(
                CheatActivity.EXTRA_ANSWER,
                question.answer
            )

            startActivity(cheatIntent)
        }

        findViewById<Button>(R.id.restartButton).setOnClickListener {
            currentIndex = 0
            score = 0
            answered.fill(false)
            updateScreen()
        }

        updateScreen()
    }

    private fun checkAnswer(userAnswer: Boolean) {
        // Prevent repeated taps from adding extra points.
        if (answered[currentIndex]) return

        val isCorrect = userAnswer == questions[currentIndex].answer

        answered[currentIndex] = true

        if (isCorrect) {
            score++
        }

        val message = if (isCorrect) {
            R.string.correct
        } else {
            R.string.incorrect
        }

        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()

        updateScreen()
    }

    private fun updateScreen() {
        questionText.setText(questions[currentIndex].textResId)

        progressText.text = getString(
            R.string.question_number,
            currentIndex + 1,
            questions.size
        )

        val alreadyAnswered = answered[currentIndex]

        trueButton.isEnabled = !alreadyAnswered
        falseButton.isEnabled = !alreadyAnswered

        statusText.setText(
            if (alreadyAnswered) {
                R.string.already_answered
            } else {
                R.string.choose_answer
            }
        )

        val answeredCount = answered.count { it }

        scoreText.text = if (answeredCount == questions.size) {
            getString(
                R.string.final_score,
                score,
                questions.size
            )
        } else {
            getString(
                R.string.score_progress,
                score,
                questions.size,
                answeredCount
            )
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putInt(KEY_INDEX, currentIndex)
        outState.putInt(KEY_SCORE, score)
        outState.putBooleanArray(KEY_ANSWERED, answered)
        super.onSaveInstanceState(outState)
    }

    companion object {
        private const val KEY_INDEX = "current_index"
        private const val KEY_SCORE = "score"
        private const val KEY_ANSWERED = "answered_questions"
    }
}