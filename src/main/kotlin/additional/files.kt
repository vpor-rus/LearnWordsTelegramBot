package additional

import java.io.File
import java.util.Dictionary

data class Word(
    val original: String,
    val translate: String,
    var answerCount: Int = 0,
)

fun loadWordFromFile(): MutableList<Word> {
    val fileWord = File("word.txt")
    val linesWord = fileWord.readLines()

    val dictionary = mutableListOf<Word>()

    for (line in linesWord) {
        val separateWord = line.split("|")
        val original = separateWord.getOrNull(0) ?: ""
        val translate = separateWord.getOrNull(1) ?: ""
        val correctAnswerCount = separateWord.getOrNull(2)?.toIntOrNull() ?: 0

        val word = Word(
            original = original, translate = translate, answerCount = correctAnswerCount
        )
        dictionary.add(word)
    }
    return dictionary
}

fun saveDictionary(dictionary: List<Word>) {
    val fileWord = File("word.txt")
    val lines = dictionary.map { "${it.original}|${it.translate}|${it.answerCount}" }
    fileWord.writeText(lines.joinToString("\n"))
}

const val CRITERION_OF_STUDY = 3

fun main() {

    println("Программа предназначена для изучения иностранных слов")

    while (true) {
        val dictionary = loadWordFromFile()

        println("Выберите вариант действия: \n1 - изучение слов\n2 - статистика\n0 - выход")
        val choice = readLine()?.toInt()
        when (choice) {
            1 -> {
                val notLearnedList = dictionary.filter { it.answerCount < CRITERION_OF_STUDY }

                if (notLearnedList.isEmpty()) {
                    println("Выучены все слова")
                    continue
                } else {
                    while (true) {
                        val listQuestionWord = notLearnedList.shuffled().take(4)
                        val correctAnswerWord = listQuestionWord.random()
                        val options = listQuestionWord.map { it.translate }.shuffled()

                        val correctAnswerID = options.indexOf(correctAnswerWord.translate) + 1

                        println("Как переводится ${correctAnswerWord.original}: ")
                        options.forEachIndexed { index, option -> println("${index + 1} - $option") }
                        println()
                        println("0 - меню")

                            println("Введите номер  ответа: ")
                            val inputAnswer = readLine()?.toInt()

                            if ((inputAnswer != null) && (inputAnswer in 1..options.size)) {
                                if (inputAnswer == correctAnswerID) {
                                    println("Правильно. Ответ \"${options[inputAnswer - 1]}\"")
                                    println()
                                    val indexInDict =
                                        dictionary.indexOfFirst { it.original == correctAnswerWord.original }
                                    if (indexInDict != -1) {
                                        dictionary[indexInDict].answerCount++
                                        saveDictionary(dictionary)
                                    }
                                } else {
                                    println("Неправильно. Ответ \"${correctAnswerWord.translate}\"")
                                    println()
                                }
                            } else if (inputAnswer == 0) {
                                println("Возврат в меню.")
                                break
                            } else println("некорректный ввод")
                            continue

                    }
                }
            }

            2 -> {
                val totalCount = dictionary.size
                val learnedCount = dictionary.count { it.answerCount >= CRITERION_OF_STUDY }
                val percentLearned = if (totalCount > 0) {
                    (learnedCount * 100) / totalCount
                } else 0
                println("Из $totalCount слов, выучено $learnedCount слов($percentLearned%)")
            }

            0 -> {
                println("выбор выход")
                break
            }

            else -> println("некорректный ввод, выберите вариант 1 или 2 или 0")
        }
    }
}
