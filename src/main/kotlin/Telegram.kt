import additional.LearnWordTrainer
import ru.vporus.trainerbot.telegram.TelegramBot
import ru.vporus.trainerbot.telegram.api.TelegramBotService

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