import java.io.File

fun readVotes (file:File):  List<List<String>> {
val candidatesList= mutableListOf<List<String>>()
    file.forEachLine {
        candidatesList.add(it.split(","," ").map { name-> name.trim()
            })
    }
    return candidatesList
    println(candidatesList)
}


fun main() {

    val file = File("src/votes/300x6.txt")
    readVotes(file);

}