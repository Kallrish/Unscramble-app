package com.example.android.unscramble.ui.game

import android.text.Spannable
import android.text.SpannableString
import android.text.style.TtsSpan
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel

/**
 * ViewModel containing the app data and methods to process the data
 */
class GameViewModel : ViewModel() {

    private val _score = MutableLiveData(0)
    val score: LiveData<Int>
        get() = _score

    private var _currentWordCount = MutableLiveData(0)
    val currentWordCount: LiveData<Int>
        get() = _currentWordCount

    private val _currentScrambledWord = MutableLiveData<String>()
    val currentScrambledWord: LiveData<Spannable> = Transformations.map(_currentScrambledWord) {
        if (it == null) {
            SpannableString("")
        } else {
            val scrambleWord = it.toString()
            val spannable: Spannable = SpannableString(scrambleWord)
            spannable.setSpan(
                TtsSpan.VerbatimBuilder(scrambleWord).build(),
                0,
                scrambleWord.length,
                Spannable.SPAN_INCLUSIVE_INCLUSIVE
            )
            spannable
        }
    }

    // List of words used in the game
    private var wordList: MutableList<String> = mutableListOf()
    private lateinit var currentWord: String

    init {
        getNextWord()
    }

    /*
    * Updates currentWord and currentScrambledWord with the next word.
    */
    fun getNextWord() {
        currentWord = allWordsList.random()
        val tempWord = currentWord.toCharArray()
        tempWord.shuffle()

        while (String(tempWord).equals(currentWord, false)) {
            tempWord.shuffle()
        }
        if (wordList.contains(currentWord)) {
            getNextWord()
        } else {
            _currentScrambledWord.value = String(tempWord)
            _currentWordCount.value = (_currentWordCount.value)?.inc()
            wordList.add(currentWord)
        }
    }

    /*
    * Re-initializes the game data to restart the game.
    */
    fun reinitializeData() {
        _score.value = 0
        _currentWordCount.value = 0
        wordList.clear()
        getNextWord()
    }

    /*
    * Increases the game score if the player's word is correct.
    */
    private fun increaseScore() {
        _score.value = (_score.value)?.plus(SCORE_INCREASE)
    }

    /*
    * Returns true if the player word is correct.
    * Increases the score accordingly.
    */
    fun isUserCorrect(playerWord: String): Boolean {
        if (playerWord.equals(currentWord, true)) {
            increaseScore()
            return true
        }
        return false
    }

    /*
    * Returns true if the current word count is less than MAX_NO_OF_WORDS.
    * Updates the next word.
    */
    fun nextWord(): Boolean {
        return if (currentWordCount.value!! < MAX_NO_OF_WORDS) {
            getNextWord()
            true
        } else false
    }
}