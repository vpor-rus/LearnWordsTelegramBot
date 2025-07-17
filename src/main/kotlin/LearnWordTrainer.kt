package additional

import java.io.File

class Statistics (
    val learnedCount: Int,
    val totalCount: Int,
    val percentCount: Int,
)

class LearnWordTrainer {

    val dictionary = loadDictionary()


    fun loadDictionary(): MutableList<Word> {

        val fileWord = File("word.txt")
        val lines = fileWord.readLines()

        val dictionary = mutableListOf<Word>()

        for (line in lines) {
            val separateCell = line.split("|")
            val original = separateCell.getOrNull(0) ?: ""
            val translate = separateCell.getOrNull(1) ?: ""
            val correctAnswerCount = separateCell.getOrNull(2)?.toIntOrNull() ?: 0

            val word = Word(
                original = original, translate = translate, correctAnswerCount = correctAnswerCount
            )
            dictionary.add(word)
        }
        return dictionary
    }

    fun saveDictionary(dictionary: List<Word>) {

        val fileWord = File("word.txt")
        val lines = dictionary.map { "${it.original}|${it.translate}|${it.correctAnswerCount}" }
        fileWord.writeText(lines.joinToString("\n"))
    }

    fun getStatistics(): Statistics {
        val learnedCount = dictionary.count { it.correctAnswerCount >= CRITERION_OF_STUDY }
        val totalCount = dictionary.size
        val percentCount = if (totalCount != 0) {
            (learnedCount * 100) / totalCount
        } else 0

        return Statistics(
            learnedCount, totalCount, percentCount
        )
    }
}