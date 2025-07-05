package additional
import java.io.File


data class Word(
    val original: String,
    val translate: String,
    val correctAnswersCount: Int = 0,
)

fun loadDictionary(): List<Word> {
    val wordFile = File("word.txt")
    val lines = if (wordFile.exists()) {
        wordFile.readLines()
    } else {
        emptyList()
    }

    val dictionary = mutableListOf<Word>()

    for (line in lines) {
        val line =  line.split("|")
        val original = line.getOrNull(0) ?: ""
        val translate = line.getOrNull(1) ?: ""
        val correctCount = line.getOrNull(2)?.toIntOrNull() ?: 0

        dictionary.add(Word(original, translate, correctCount))
    }
    return dictionary
}

fun main() {
    val dictionary = loadDictionary()

    while (true) {
        println("Программа предназначена для изучения иностранных слов,\n" +
                "Выберите ваше действие (введите 0 или 1 или 2):")
        println("1 - Учить слова\n2 - Статистика\n0 - Выход")

        val choice = readLine()?.toInt()
        when (choice) {
            0 -> {
                println("Выбран пункт\"выход\"")
                break
            }

            1 -> println("Выбран пункт \"учить слова\"")
            2 -> {
            val totalCount = dictionary.size
            val learnedCount = dictionary.filter { it.correctAnswersCount >= 3 }.size
                val percentTeache = if (totalCount > 0) { (learnedCount * 100) / totalCount }
                else 0
                println("Выучено $learnedCount из $totalCount | $percentTeache%")
            }
            else -> println("Введите 0 или 1 или 2")
        }
        println()
    }

}