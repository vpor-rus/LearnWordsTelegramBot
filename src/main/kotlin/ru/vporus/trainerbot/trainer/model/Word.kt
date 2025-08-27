package ru.vporus.trainerbot.trainer.model

data class Word(
    val original: String,
    val translate: String,
    var correctAnswerCount: Int = 0,
)
