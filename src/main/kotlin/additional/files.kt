package additional

import java.io.File

data class Word(
    val original: String,
    val translate: String,
    val correctAnswerCount: Int = 0,
)

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

const val CRITERION_OF_STUDY = 3

const val NUMBER_VARIANTS_IN_ANSWERS = 4

fun main() {

    val dictionary = loadDictionary()

    println("Программа предназначена для изучения иностранных слов\n")

    while (true) {
        println(
            "выберите ваше действие\n" + "1 - Учить слова\n2 - статистика\n0 - выход"
        )
        val choice = readLine()?.toInt()


        when (choice) {
            1 -> {
                val notLearnedList = dictionary.filter { it.correctAnswerCount < CRITERION_OF_STUDY }

                if (notLearnedList.isEmpty()) {
                    println("Вы выучили все слова, поздравляем")
                    continue
                }

                val needToAddInVariantsAnswer = NUMBER_VARIANTS_IN_ANSWERS - notLearnedList.size
                val learnedList = dictionary.filter { it.correctAnswerCount >= CRITERION_OF_STUDY }
                val questionWord = if (needToAddInVariantsAnswer > 0) {
                    (notLearnedList + learnedList.shuffled().take(needToAddInVariantsAnswer).shuffled())
                } else {
                    notLearnedList.shuffled().take(NUMBER_VARIANTS_IN_ANSWERS)
                }

                val correctAnswer = notLearnedList.random()

                val options = questionWord.map { it.translate }.shuffled()
                println("Как переводится: ${correctAnswer.original} ?")
                options.forEachIndexed { index, option -> println("${index + 1}. $option") }

                println("Введите номер ответа: ")
                val answer = readLine()?.toInt()

                if (answer != null && answer in 1..options.size) {
                    if (options[answer - 1] == correctAnswer.translate) {
                        println("Правильно.\nОтвет: ${options[answer - 1]}")
                    } else {
                        println("Неверно.\nПравильный ответ: ${correctAnswer.translate}")
                    }
                } else {
                    println("Некорректный ввод")
                }
            }

            2 -> {
                val totalCount = dictionary.size
                val learnedCount = dictionary.filter { it.correctAnswerCount >= CRITERION_OF_STUDY }.size
                val percentCount = if (totalCount != 0) {
                    (learnedCount * 100) / totalCount
                } else 0

                println("результат изучения $percentCount")
            }

            0 -> {
                println("выбрал выход")
                break
            }

            else -> println("некорректный ввод, выберите вариант 0 или 1 или 2")

        }
    }
}
