package additional

import java.io.File

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
                    val notLearnedList = trainer.dictionary.filter { it.correctAnswerCount < CRITERION_OF_STUDY }

                    if (notLearnedList.isEmpty()) {
                        println("Вы выучили все слова, поздравляем")
                        continue
                    }

                    val questionWord =
                        (notLearnedList.shuffled().take(NUMBER_VARIANTS_IN_ANSWERS)).shuffled()
                    val correctAnswer = questionWord.random()

                    println("Как переводится: ${correctAnswer.original} ?\n" +
                            "1 - ${questionWord[0].translate}, 2 - ${questionWord[1].translate}, " +
                            "3 - ${questionWord[2].translate}, 4 - ${questionWord[3].translate}\n" +
                            "0 - Выход.")

                    println("Введите номер ответа: ")
                    val answer = readLine()?.toInt()

                    if (answer != null && answer in 1..NUMBER_VARIANTS_IN_ANSWERS) {
                        if (questionWord[answer - 1] == correctAnswer) {
                            println("Правильно.\nОтвет: ${questionWord[answer - 1].translate}")

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
                val stetistics = trainer.getStatistics()

                println("результат изучения: $learnedCount/$totalCount $percentCount%")
            }

            0 -> {
                println("выбрал выход")
                break
            }

            else -> println("некорректный ввод, выберите вариант 0 или 1 или 2")

        }
    }
}