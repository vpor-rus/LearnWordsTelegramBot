import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse

fun main(args: Array<String>) {

    val botToken: String = args[0]
    var updateId = 0

    val messageTextRegex: Regex = "\"text\":\"(.+?)\"".toRegex()
    val updateIdRegex = "\"update_id\":(\\d+)".toRegex()

    while (true) {

        Thread.sleep(2000)
        val updates: String = getUpdates(botToken, updateId)
        println(updates)

        val updateIds = updateIdRegex.findAll(updates)
            .map { it.groupValues[1].toInt() }
            .toList()

        if (updateIds.isNotEmpty()) {
            updateId = updateIds.maxOrNull()!! + 1
        }

        val matchResult: MatchResult? = messageTextRegex.find(updates)
        val text = matchResult?.groups?.get(1)?.value
        println(text)
    }
}

fun getUpdates(botToken: String, updateId: Int): String {

    val urlGetUpdates = "https://api.telegram.org/bot$botToken/getUpdates?offset=$updateId"
    val client: HttpClient = HttpClient.newBuilder().build()
    val request: HttpRequest = HttpRequest.newBuilder().uri(URI.create(urlGetUpdates)).build()
    val response: HttpResponse<String> = client.send(request, HttpResponse.BodyHandlers.ofString())

    return response.body()
}
