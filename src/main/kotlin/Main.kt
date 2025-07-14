import java.io.File

data class Word(
    val original: String,
    val translate: String,
    val correctAnswerCount: Int = 0,
)

const val CRITERION_OF_STUDY = 3

const val NUMBER_VARIANTS_IN_ANSWERS = 4

fun main() {

    val trainer = LearnWordsTrainer()

    println("Программа предназначена для изучения иностранных слов\n")

    while (true) {
        println(
            "выберите ваше действие\n" + "1 - Учить слова\n2 - статистика\n0 - выход"
        )
        val choice = readLine()?.toInt()

        when (choice) {
            1 -> {
                val notLearnedList = trainer.dictionary.filter { it.correctAnswerCount < CRITERION_OF_STUDY }

                if (notLearnedList.isEmpty()) {
                    println("Вы выучили все слова, поздравляем")
                    continue
                }

                val needToAddInVariantsAnswer = NUMBER_VARIANTS_IN_ANSWERS - notLearnedList.size
                val learnedList = trainer.dictionary.filter { it.correctAnswerCount >= CRITERION_OF_STUDY }
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
                val statistics = trainer.getStatistics()

                println("результат изучения:\nвыучено ${statistics.learned}/${statistics.total}, ${statistics.percent}%")
            }

            0 -> {
                println("выбрал выход")
                break
            }

            else -> println("некорректный ввод, выберите вариант 0 или 1 или 2")

        }
    }
}

