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
                        break
                    }

                    val questionWord = notLearnedList.shuffled().take(NUMBER_VARIANTS_IN_ANSWERS).shuffled()
                    val correctAnswerWord = questionWord.random()

                    println(
                        "Как переводится: ${correctAnswerWord.original} ?\n" + "1 - ${questionWord[0].translate}, 2 - ${questionWord[1].translate}, " + "3 - ${questionWord[2].translate}, 4 - ${questionWord[3].translate}\n" + "0 - Выход."
                    )

                    println("Введите номер ответа: ")
                    val userAnswerInput = readLine()?.toIntOrNull()
                    if (userAnswerInput == null) {
                        println("Некорректный ввод\nВведите ответы 1 или 2 или 3 или 4 или 0")
                        break
                    }

                    if (userAnswerInput == 0) break
                    val correctAnswerIndex = questionWord.indexOf(correctAnswerWord)

                    if (userAnswerInput == correctAnswerIndex + 1) {

                        correctAnswerWord.correctAnswerCount++
                        saveDictionary(dictionary)
                        println("Правильно!\nответ ${questionWord[correctAnswerIndex}")
                    } else {
                        println("Неправильно!\nПравильный ответ ${questionWord[correctAnswerIndex]}")
                    }
                }
            }

            2 -> {
                val learnedCount = dictionary.count { it.correctAnswerCount >= CRITERION_OF_STUDY }
                val totalCount = dictionary.size
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