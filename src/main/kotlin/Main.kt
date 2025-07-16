package additional

data class Word(
    val original: String,
    val translate: String,
    var correctAnswerCount: Int = 0,
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

                    println("Как переводится: ${question.correctAnswer.original} ?\n" +
                            "1 - ${question.variants[0].translate}, 2 - ${question.variants[1].translate}, " +
                            "3 - ${question.variants[2].translate}, 4 - ${question.variants[3].translate}\n" +
                            "0 - Выход."
                    )

                    println("Введите номер ответа: ")
                    val answer = readLine()?.toInt()

                    if (answer != null && answer in 1..NUMBER_VARIANTS_IN_ANSWERS) {
                        if (question.variants[answer - 1] == question.correctAnswer) {
                            println("Правильно.\nОтвет: ${question.variants[answer - 1].translate}")

                            val indexInDict = trainer.dictionary.indexOfFirst { it.original == question.correctAnswer.original }
                            if (indexInDict != -1) {
                                trainer.dictionary[indexInDict].correctAnswerCount++
                                trainer.saveDictionary(trainer.dictionary)
                            }
                        } else {
                            println("Неверно.\nПравильный ответ: ${question.correctAnswer.translate}")
                        }
                    } else if(answer == 0) {
                        println("выход в меню")
                        break
                    } else {
                        println("Некорректный ввод")
                    }
                }
            }

            2 -> {
                val statistics = trainer.getStatistics()

                println("результат изучения: ${statistics.learnedCount}/${statistics.totalCount}" +
                        " ${statistics.percentCount}%")
            }

            0 -> {
                println("выбрал выход")
                break
            }

            else -> println("некорректный ввод, выберите вариант 0 или 1 или 2")

        }
    }
}