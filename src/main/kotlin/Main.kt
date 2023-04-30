import java.io.File

fun main() {
    val file = File("src/votes/300x6.txt")
    file.forEachLine { println(it) }
}