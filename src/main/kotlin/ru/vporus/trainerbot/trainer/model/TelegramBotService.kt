package ru.vporus.trainerbot.trainer.model

import Question
import java.net.URI
import java.net.URLEncoder
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse

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