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
                val notLearnedList = trainer.dictionary.filter { it.correctAnswerCount < CRITERION_OF_STUDY }

                if (notLearnedList.isEmpty()) {
                    println("Вы выучили все слова, поздравляем")
                    continue
                }

                while (true) {
                    val needToAddInVariantsAnswer = NUMBER_VARIANTS_IN_ANSWERS - notLearnedList.size
                    val learnedList = trainer.dictionary.filter { it.correctAnswerCount >= CRITERION_OF_STUDY }


                    val questionWord = if (needToAddInVariantsAnswer > 0) {
                        (notLearnedList + learnedList.shuffled().take(needToAddInVariantsAnswer).shuffled())
                    } else {
                        notLearnedList.shuffled().take(NUMBER_VARIANTS_IN_ANSWERS)
                    }
                    val correctAnswer = questionWord.random()
                    val options = questionWord.map { it.translate }.shuffled()
                    val correctAnswerID = options.indexOf(correctAnswer.translate) + 1

                    println("Как переводится: ${correctAnswer.original} ?\n" +
                            "1 - ${options[0]}, 2 - ${options[1]}, 3 - ${options[2]}, 4 - ${options[3]}\n" +
                            "0 - Выход.")

                    println("Введите номер ответа: ")
                    val answer = readLine()?.toInt()

                    if (answer != null && answer in 1..options.size) {
                        if (answer == correctAnswerID) {
                            println("Правильно.\nОтвет: ${options[answer - 1]}")

                            val indexInDict = trainer.dictionary.indexOfFirst { it.original == correctAnswer.original }
                            if (indexInDict != -1) {
                                trainer.dictionary[indexInDict].correctAnswerCount++
                                trainer.saveDictionary(trainer.dictionary)
                            }
                        } else {
                            println("Неверно.\nПравильный ответ: ${correctAnswer.translate}")
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

                println("результат изучения: ${statistics.learnedCount}/${statistics.totalCount} ${statistics.percentCount}%")
            }

            0 -> {
                println("выбрал выход")
                break
            }

            else -> println("некорректный ввод, выберите вариант 0 или 1 или 2")

        }
    }
}
