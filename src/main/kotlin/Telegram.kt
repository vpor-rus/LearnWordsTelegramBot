import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse


fun main(args: Array<String>) {
    if (args.isEmpty()) {
        println("Not found token")
        return
    }

    val botToken = args[0]
    val botService = TelegramBotService(botToken)

    while (true) {
        Thread.sleep(TIME_SLEEP)
        botService.processUpdates()
    }
}

const val TIME_SLEEP: Long = 2000

class TelegramBotService(private val botToken: String) {

    private val client = HttpClient.newBuilder().build()
    private var lastUpdateId = 0

    val messageTextRegex: Regex = "\"text\":\"(.+?)\"".toRegex()
    val updateIdRegex = "\"update_id\":(\\d+)".toRegex()
    val chatIdRegex = "\"chat\"\\s*:\\s*\\{[^}]*\"id\"\\s*:\\s*(\\d+)".toRegex()


    fun getUpdates(): String {
        val url = "https://api.telegram.org/bot$botToken/getUpdates?offset=$lastUpdateId"
        val request = HttpRequest.newBuilder().uri(URI.create(url)).build()
        val response = client.send(request, HttpResponse.BodyHandlers.ofString())
        return response.body()
    }

    fun processUpdates() {
        val updates = getUpdates()
        println("Updates: $updates")

        val updateIds = updateIdRegex.findAll(updates).map { it.groupValues[1].toInt() }.toList()
        if (updateIds.isNotEmpty()) {
            lastUpdateId = updateIds.maxOrNull()!! + 1
        }

        val texts = messageTextRegex.findAll(updates).map { it.groupValues[1] }.toList()
        val chatIds = chatIdRegex.findAll(updates).map { it.groupValues[1].toLong() }.toList()

        for ((index, updateId) in updateIds.withIndex()) {
            val chatId = chatIds.getOrNull(index)
            val text = texts.getOrNull(index)
            if (chatId != null && text != null) {
                handleUpdate(chatId, text, updateId)
            }
        }
    }

    fun handleUpdate(chatId: Long, text: String, updateId: Int) {
        println("Received message '$text' from chat $chatId with updateId $updateId")

        if (updateId >= this.lastUpdateId) {
            this.lastUpdateId = updateId + 1
        }

        if (text == "hello") {
            sendMessage(chatId, "hello")
        }

        if (text == "menu") {
            sendMenu(chatId)
        }
    }

    fun sendMessage(chatId: Long, text: String) {
        val url = "https://api.telegram.org/bot$botToken/sendMessage?chat_id=$chatId&text=${text.encodeUrl()}"
        val request = HttpRequest.newBuilder().uri(URI.create(url)).build()
        client.send(request, HttpResponse.BodyHandlers.ofString())
    }

    fun sendMenu(chatId: Long,) {
        val url = "https://api.telegram.org/bot$botToken/sendMessage"
        val sendMenuBody = """
            {
              "chat_id": "$chatId",
              "text": "Основное меню",
              "reply_markup": {
                "inline_keyboard": [
                  [
                    {
                      "text": "Изучить слова",
                      "callback_data": "data1",
                    }
                    {
                      "text": "Статистика",
                      "callback_data": "data2"
                    }
                  ]
                ]
              }
            }

        """.trimIndent()
        val request = HttpRequest.newBuilder().uri(URI.create(url)).
        header("Content-type", "application/json").
        POST(HttpRequest.BodyPublishers.ofString(sendMenuBody)).
        build()
        client.send(request, HttpResponse.BodyHandlers.ofString())
    }
}

fun String.encodeUrl(): String = java.net.URLEncoder.encode(this, Charsets.UTF_8.name())