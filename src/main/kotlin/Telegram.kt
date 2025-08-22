import ru.vporus.trainerbot.telegram.api.TelegramBotService
import ru.vporus.trainerbot.trainer.model.Question
import java.net.URI
import java.net.URLEncoder
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse

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

class TelegramBot(
    private val botToken: String,
    private val trainer: LearnWordTrainer
) {
    companion object {
        const val TIME_SLEEP: Long = 2000
        const val CMD_HELLO = "hello"
        const val CMD_MENU = "menu"
        const val CALLBACK_LEARN_WORDS = "learn_words_clicked"
        const val CALLBACK_STATISTIC = "statistic_clicked"
        const val CALLBACK_DATA_ANSWER_PREFIX = "answer_"
    }

    private val botService = TelegramBotService(botToken)

    fun startPolling() {
        while (true) {
            Thread.sleep(TIME_SLEEP)
            val updates = botService.getUpdates()
            for (update in updates) {
                handleUpdate(update)
            }
        }
    }

    private fun handleUpdate(update: TelegramBotService.Update) {
        val chatId = update.chatId
        val text = update.text
        val callbackData = update.callbackData

        when {
            text == CMD_HELLO -> botService.sendMessage(chatId, "hello")
            text == CMD_MENU -> botService.sendMenu(chatId)
            callbackData != null -> handleCallback(chatId, callbackData)
        }
    }

    private fun handleCallback(chatId: Long, callbackData: String) {
        when {
            callbackData == CALLBACK_LEARN_WORDS -> checkNextQuestionAndSend(chatId)
            callbackData == CALLBACK_STATISTIC -> sendStatistics(chatId)
            callbackData.startsWith(CALLBACK_DATA_ANSWER_PREFIX) -> handleAnswer(chatId, callbackData)
            else -> botService.sendMessage(chatId, "Неизвестная команда: $callbackData")
        }
    }

    private fun checkNextQuestionAndSend(chatId: Long) {
        val question = trainer.getNextQuestion()
        if (question == null) {
            botService.sendMessage(chatId, "Все слова в словаре выучены")
        } else {
            botService.sendQuestion(chatId, question)
        }
    }

    private fun sendStatistics(chatId: Long) {
        val stats = trainer.getStatistics()
        val message = "Результат изучения: ${stats.learnedCount}/${stats.totalCount} (${stats.percentCount}%)"
        botService.sendMessage(chatId, message)
    }

    private fun handleAnswer(chatId: Long, callbackData: String) {
        val indexStr = callbackData.removePrefix(CALLBACK_DATA_ANSWER_PREFIX)
        val userAnswerIndex = indexStr.toIntOrNull()
        val isCorrect = trainer.checkAnswer(userAnswerIndex)
        val response = if (isCorrect) "Правильно!" else "Неправильно"
        botService.sendMessage(chatId, response)
        checkNextQuestionAndSend(chatId)
    }
}
