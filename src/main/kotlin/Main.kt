package additional

import java.io.File

data class Word(
    val original: String,
    val translate: String,
    var correctAnswerCount: Int = 0,
)

fun questionToString(question: Question): String{
    val variants = question.variants.mapIndexed { index: Int, word: Word -> "${index + 1} - ${word.translate}"  }.joinToString("\n")
    return question.correctAnswer.original + "\n" + variants + "\n\n0 - выйти в меню"
}

const val CRITERION_OF_STUDY = 3

const val NUMBER_VARIANTS_IN_ANSWERS = 4

fun main() {

    val trainer = LearnWordTrainer()

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
                        break
                    }
                    println("Как переводится слово: ")
                    println(
                        questionToString(question)
                    )

                    println("Введите номер ответа: ")
                    val userAnswerInput = readLine()?.toIntOrNull()

                    if (userAnswerInput == 0) break


                    if (trainer.checkAnswer(userAnswerInput?.minus(1))) {

                        println("Правильно!")
                    } else {
                        println("Неправильно!")
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