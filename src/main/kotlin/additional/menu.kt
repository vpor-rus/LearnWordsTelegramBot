package additional

import java.io.File

fun main() {
    val menuFile = File("menu.txt")
    menuFile.createNewFile()

    while (true) {
        menuFile.writeText("1 - Учить слова\n2 - Статистика\n0 - Выход")
        println(
            "Программа предназначена для изучения иностранных слов,\n" + "Выберите ваше действие (введите 0 или 1 или 2):"
        )
        val menu = menuFile.readLines()
        for (point in menu) {
            println(point)
        }

        val choice = readLine()?.toInt()
        when (choice) {
            0 -> {
                println("Выбран пункт\"выход\"")
                break
            }
            1 -> println("Выбран пункт \"учить слова\"")
            2 -> println("Выбран пункт \"Статистика\"")
            else -> println("Введите 0 или 1 или 2")
        }
        println()
    }
}
