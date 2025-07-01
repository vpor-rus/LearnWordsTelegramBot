package additional

import java.io.File

data class Word(
    val original: String,
    val translate: String,
    val correctAnswersCount: Int = 0,
)

fun main() {
    val wordFile: File = File("word.txt")
    val lines = wordFile.readLines()

    val dictionary = mutableListOf<Word>()

    for (line in lines) {
        val line = line.split("|")
        val correctAnswersCount = line.getOrNull(2)?.toIntOrNull() ?: 0
        val word = Word(line[0], line[1])
        dictionary.add(word)
    }
    dictionary.forEach { everyWord: Word ->
        println(everyWord)
    }
}