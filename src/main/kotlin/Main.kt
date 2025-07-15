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
                while (true) {

                    val question = trainer.getNextQuestion()


                    if (question == null) {
                        println("Вы выучили все слова, поздравляем")
                        continue
                    }

                    println("Как переводится: ${question.correctionAnswer.original} ?")
                    question.variants.forEachIndexed { index, option -> println("${index + 1}. $option") }

                    println("Введите номер ответа: ")
                    val answer = readLine()?.toInt()

                    if (answer != null && answer in 1..question.variants.size) {
                        if (question.variants[answer - 1].toString() == question.correctionAnswer.translate) {
                            println("Правильно.\nОтвет: ${question.variants[answer - 1]}")
                        } else {
                            println("Неверно.\nПравильный ответ: ${question.correctionAnswer.translate}")
                        }
                    } else {
                        println("Некорректный ввод")
                    }
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

