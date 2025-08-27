package ru.vporus.trainerbot.trainer.model
import Word

data class Question(
    val variants: List<Word>,
    val correctAnswer: Word,
)