import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse


fun main(args: Array<String>) {
    if (args.isEmpty()) {
        println("Not found connection")
        return
    }
    val botToken = args[0]
    val botService = TelegramBotService(botToken)

    while (true) {

        Thread.sleep(2000)
        botService.processUpdates()
    }
}

class TelegramBotService(private val botToken: String) {

    private val client = HttpClient.newBuilder().build()
    private var updateId = 0

    val messageTextRegex: Regex = "\"text\":\"(.+?)\"".toRegex()
    val updateIdRegex = "\"update_id\":(\\d+)".toRegex()
    val chatIdRegex = "\"chat\"\\s*:\\s*\\{[^}]*\"id\"\\s*:\\s*(\\d+)".toRegex()

    fun getUpdates(): String {
        val url = "https://api.telegram.org/bot$botToken/getUpdates?offset=$updateId"
        val request = HttpRequest.newBuilder().uri(URI.create(url)).build()
        val response = client.send(request, HttpResponse.BodyHandlers.ofString())
        return response.body()
    }

    fun sendMessage(chatId: Long, text: String) {
        val url = "https://api.telegram.org/bot$botToken/sendMessage?chat_id=$chatId&text=${text.encodeUrl()}"
        val request = HttpRequest.newBuilder().uri(URI.create(url)).build()
        client.send(request, HttpResponse.BodyHandlers.ofString())
    }

    fun processUpdates() {
        val updates = getUpdates()
        println("Updates: $updates")

        val updateIds = updateIdRegex.findAll(updates).map { it.groupValues[1].toInt() }.toList()

        if (updateIds.isNotEmpty()) {
            updateId = updateIds.maxOrNull()!! + 1
        }

        val texts = messageTextRegex.findAll(updates).map { it.groupValues[1] }.toList()
        val chatIds = chatIdRegex.findAll(updates).map { it.groupValues[1].toLong() }.toList()

        for ((chatId, text) in chatIds.zip(texts)) {
            println("Received message '$text' from chat $chatId")

            if (text == "Hello") {
                sendMessage(chatId, "Hello")
            }
        }
    }
}

fun String.encodeUrl(): String = java.net.URLEncoder.encode(this, Charsets.UTF_8.name())