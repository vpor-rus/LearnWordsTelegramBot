import additional.LearnWordTrainer
import additional.Question
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

class TelegramBotService(private val botToken: String) {
    companion object {
        const val BASE_URL = "https://api.telegram.org/bot"
        const val CALLBACK_LEARN_WORDS = "learn_words_clicked"
        const val CALLBACK_STATISTIC = "statistic_clicked"
        const val CALLBACK_DATA_ANSWER_PREFIX = "answer_"
    }

    private val client = HttpClient.newBuilder().build()
    private var lastUpdateId = 0

    private val updateIdRegex = "\"update_id\":(\\d+)".toRegex()
    private val chatIdRegex = "\"chat\"\\s*:\\s*\\{[^}]*\"id\"\\s*:\\s*(\\d+)".toRegex()
    private val messageTextRegex = "\"text\":\"(.+?)\"".toRegex()
    private val dataRegex = "\"data\":\"(.+?)\"".toRegex()

    data class Update(
        val updateId: Int,
        val chatId: Long,
        val text: String?,
        val callbackData: String?
    )

    fun getUpdates(): List<Update> {
        val url = "$BASE_URL$botToken/getUpdates?offset=$lastUpdateId"
        val request = HttpRequest.newBuilder().uri(URI.create(url)).build()
        val response = client.send(request, HttpResponse.BodyHandlers.ofString())
        val updates = response.body()

        return parseUpdates(updates).also { updateList ->
            if (updateList.isNotEmpty()) {
                lastUpdateId = updateList.maxOf { it.updateId } + 1
            }
        }
    }

    private fun parseUpdates(updatesJson: String): List<Update> {
        val updateIds = updateIdRegex.findAll(updatesJson).map { it.groupValues[1].toInt() }.toList()
        val chatIds = chatIdRegex.findAll(updatesJson).map { it.groupValues[1].toLong() }.toList()
        val texts = messageTextRegex.findAll(updatesJson).map { it.groupValues[1] }.toList()
        val callbackDataList = dataRegex.findAll(updatesJson).map { it.groupValues[1] }.toList()

        return updateIds.mapIndexed { index, updateId ->
            val chatId = chatIds.getOrNull(index) ?: 0L
            Update(
                updateId = updateId,
                chatId = chatId,
                text = texts.getOrNull(index),
                callbackData = callbackDataList.getOrNull(index)
            )
        }
    }

    fun sendMessage(chatId: Long, text: String) {
        val url = "$BASE_URL$botToken/sendMessage?chat_id=$chatId&text=${text.encodeUrl()}"
        val request = HttpRequest.newBuilder().uri(URI.create(url)).build()
        client.send(request, HttpResponse.BodyHandlers.ofString())
    }

    fun sendMenu(chatId: Long) {
        val url = "$BASE_URL$botToken/sendMessage"
        val sendMenuBody = """
        {
            "chat_id": "$chatId",
            "text": "Основное меню",
            "reply_markup": {
                "inline_keyboard": [
                    [
                        {
                            "text": "Изучить слова",
                            "callback_data": "$CALLBACK_LEARN_WORDS"
                        },
                        {
                            "text": "Статистика",
                            "callback_data": "$CALLBACK_STATISTIC"
                        }
                    ]
                ]
            }
        }
        """.trimIndent()

        val request = HttpRequest.newBuilder()
            .uri(URI.create(url))
            .header("Content-type", "application/json")
            .POST(HttpRequest.BodyPublishers.ofString(sendMenuBody))
            .build()

        client.send(request, HttpResponse.BodyHandlers.ofString())
    }

    fun sendQuestion(chatId: Long, question: Question) {
        val optionsJson = question.variants.mapIndexed { index, word ->
            """
            {
                "text": "${word.translate}",
                "callback_data": "${CALLBACK_DATA_ANSWER_PREFIX}$index"
            }
            """.trimIndent()
        }.joinToString(separator = ",")

        val body = """
        {
            "chat_id": "$chatId",
            "text": "${question.correctAnswer.original}",
            "reply_markup": {
                "inline_keyboard": [
                    [$optionsJson]
                ]
            }
        }
        """.trimIndent()

        val url = "$BASE_URL$botToken/sendMessage"
        val request = HttpRequest.newBuilder()
            .uri(URI.create(url))
            .header("Content-Type", "application/json")
            .POST(HttpRequest.BodyPublishers.ofString(body))
            .build()

        client.send(request, HttpResponse.BodyHandlers.ofString())
    }

    private fun String.encodeUrl(): String = URLEncoder.encode(this, Charsets.UTF_8.toString())
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