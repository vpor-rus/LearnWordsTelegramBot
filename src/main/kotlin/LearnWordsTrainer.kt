import java.io.File

data class Statistics(
    val total: Int,
    val learned: Int,
    val percent: Int,
)

class LearnWordsTrainer {

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

    fun getStatistics(): Statistics {
        val total = dictionary.size
        val learned = dictionary.filter { it.correctAnswerCount >= CRITERION_OF_STUDY }.size
        val percent = if (total != 0) {
            (learned * 100) / total
        } else 0
        return Statistics(
           total, learned, percent,
        )
    }
}
