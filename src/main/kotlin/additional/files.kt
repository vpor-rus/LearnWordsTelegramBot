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
        val original = line.getOrNull(0) ?: ""
        val translate = line.getOrNull(1) ?: ""
        val correctAnswersCount = line.getOrNull(2)?.toIntOrNull() ?: 0
        val word = Word(original = original, translate = translate, correctAnswersCount = correctAnswersCount )
        dictionary.add(word)
    }
    for (word in dictionary) {
        println(word)
    }
}