package com.example.geoquiz

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class CheatActivity : AppCompatActivity() {

    private var answerIsTrue = false
    private var answerRevealed = false

    private lateinit var answerText: TextView
    private lateinit var showAnswerButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_cheat)

        ViewCompat.setOnApplyWindowInsetsListener(
            findViewById<View>(R.id.cheatRoot)
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

        answerText = findViewById(R.id.answerText)
        showAnswerButton = findViewById(R.id.showAnswerButton)

        // Read the data sent from MainActivity.
        findViewById<TextView>(R.id.cheatQuestionText).text =
            intent.getStringExtra(EXTRA_QUESTION)

        answerIsTrue = intent.getBooleanExtra(EXTRA_ANSWER, false)

        answerRevealed =
            savedInstanceState?.getBoolean(KEY_REVEALED) ?: false

        showAnswerButton.setOnClickListener {
            answerRevealed = true
            updateAnswer()
        }

        findViewById<Button>(R.id.backButton).setOnClickListener {
            // Close this Activity and return to the existing quiz.
            finish()
        }

        updateAnswer()
    }

    private fun updateAnswer() {
        val message = when {
            !answerRevealed -> R.string.answer_hidden
            answerIsTrue -> R.string.answer_true
            else -> R.string.answer_false
        }

        answerText.setText(message)
        showAnswerButton.isEnabled = !answerRevealed
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putBoolean(KEY_REVEALED, answerRevealed)
        super.onSaveInstanceState(outState)
    }

    companion object {
        const val EXTRA_QUESTION = "com.example.geoquiz.question"
        const val EXTRA_ANSWER = "com.example.geoquiz.answer"

        private const val KEY_REVEALED = "answer_revealed"
    }
}
