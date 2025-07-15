import java.io.File
import kotlin.compareTo
import kotlin.plus

data class Statistics(
    val total: Int,
    val learned: Int,
    val percent: Int,
)

data class Question(
    val variants: List<Word>,
    val correctionAnswer: Word,
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

    fun getNextQuestion(): Question?{
        val notLearnedList = dictionary.filter { it.correctAnswerCount < CRITERION_OF_STUDY }
        if (notLearnedList.isEmpty()) return null

        val needToAddInVariantsAnswer = NUMBER_VARIANTS_IN_ANSWERS - notLearnedList.size
        val learnedList = dictionary.filter { it.correctAnswerCount >= CRITERION_OF_STUDY }
        val questionWord = if (needToAddInVariantsAnswer > 0) {
            (notLearnedList + learnedList.shuffled().take(needToAddInVariantsAnswer).shuffled())
        } else {
            notLearnedList.shuffled().take(NUMBER_VARIANTS_IN_ANSWERS)
        }

        val correctAnswer = notLearnedList.random()
        val options = questionWord.map { it.translate }.shuffled()
        return Question(
            variants = options,
            correctionAnswer = correctAnswer,
        )
    }
}
