package additional

import java.io.File

data class Word(
    val original: String,
    val translate: String,
    var correctAnswerCount: Int = 0,
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

fun saveDictionary(dictionary: List<Word>) {

    val fileWord = File("word.txt")
    val lines = dictionary.map { "${it.original}|${it.translate}|${it.correctAnswerCount}" }
    fileWord.writeText(lines.joinToString("\n"))
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
                while (true) {
                val notLearnedList = dictionary.filter { it.correctAnswerCount < CRITERION_OF_STUDY }

                if (notLearnedList.isEmpty()) {
                    println("Вы выучили все слова, поздравляем")
                    continue
                }


                    val questionWord =
                        notLearnedList.shuffled().take(NUMBER_VARIANTS_IN_ANSWERS)
                    val correctAnswer = questionWord.random()

                    println("Как переводится: ${correctAnswer.original} ?\n" +
                            "1 - ${correctAnswer[0]}, 2 - ${correctAnswer[1]}, " +
                            "3 - ${correctAnswer[2]}, 4 - ${correctAnswer[3]}\n" +
                            "0 - Выход.")

                    println("Введите номер ответа: ")
                    val answer = readLine()?.toInt()

                    if (answer != null && answer in 1..NUMBER_VARIANTS_IN_ANSWERS) {
                        if (answer - 1 == correctAnswer[questionWord]) {
                            println("Правильно.\nОтвет: ${correctAnswer[answer - 1]}")

                            val indexInDict = dictionary.indexOfFirst { it.original == correctAnswer.original }
                            if (indexInDict != -1) {
                                dictionary[indexInDict].correctAnswerCount++
                                saveDictionary(dictionary)
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
                val totalCount = dictionary.size
                val learnedCount = dictionary.count { it.correctAnswerCount >= CRITERION_OF_STUDY }
                val percentCount = if (totalCount != 0) {
                    (learnedCount * 100) / totalCount
                } else 0

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