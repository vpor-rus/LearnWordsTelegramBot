package additional

import java.io.File
import java.lang.module.FindException

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

const val CRITERION_OF_STUDY = 3

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

            1 -> {
            val notLearnedList = dictionary.filter { it.correctAnswersCount < CRITERION_OF_STUDY }

            if (notLearnedList.isEmpty()) {
                println("Вы выучили все слова, поздравляем")
                continue
            }
                val questionWord = notLearnedList.shuffled().take(4)
                val  correctAnswer = questionWord.random()

                val options = questionWord.map { it.translate }.shuffled()
                println("Как переводится: ${correctAnswer.original} ?")
                options.forEachIndexed { index, option -> println("${index + 1}. $option") }

                println("Введите номер ответа: ")
                val answer = readLine()?.toInt()

                if (answer != null && answer in 1..options.size) {
                    if (options[answer - 1] == correctAnswer.translate) {
                        println("Правильно.\nОтвет: ${options[answer - 1]}")
                    } else {println("Неверно.\nПравильный ответ: ${correctAnswer.translate}")}
                } else {
                    println("Некорректный ввод")
                }
            }
            2 -> {
                val totalCount = dictionary.size
                val learnedCount = dictionary.filter { it.correctAnswersCount >= CRITERION_OF_STUDY }.size
                val percentLearned = if (totalCount > 0 ) {
                    (learnedCount * 100) /totalCount
                } else 0

                println(percentLearned)
            }

            else -> println("Введите 0 или 1 или 2")
        }
        println()
    }

}

