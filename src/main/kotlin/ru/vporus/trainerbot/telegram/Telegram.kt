package ru.vporus.trainerbot.telegram

import LearnWordTrainer

fun main(args: Array<String>) {
    if (args.isEmpty()) {
        println("Not found token")
        return
    }

    val botToken = args[0]
    val trainer = LearnWordTrainer()
    val bot = TelegramBot(botToken, trainer)
    bot.startPolling()
}