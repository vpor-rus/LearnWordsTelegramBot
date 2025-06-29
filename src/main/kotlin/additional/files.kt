package additional

import java.io.File

fun main() {
       val wordFile: File = File("word.txt")
        wordFile.createNewFile()

    val lines = wordFile.readLines()

    for (line in lines) {
        println(line)
    }
}