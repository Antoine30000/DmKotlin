import java.io.File

fun readVotes (file:File):  List<List<String>> {
val candidatesList= mutableListOf<List<String>>()
    file.forEachLine {line->
        if (line.isNotBlank()) {
            candidatesList.add(line.split(","," ").map { name -> name.trim() })
        }
    }
    return candidatesList

}
fun candidates(votes: List<List<String>>): List<String> {
    val candidates = mutableSetOf<String>()
    votes.forEach { candidates.addAll(it) }
    return candidates.sorted()
}

fun firstRound(candidates: List<String>, votes: List<List<String>>): Map<String, Int> {
    val results = mutableMapOf<String, Int>()
    candidates.forEach { candidate ->
        results[candidate] = 0
    }
    votes.forEach { ballot ->
        results[ballot[0]] = results[ballot[0]]!! + 1
    }
    return results
}

fun finalists(firstRoundResults: Map<String, Int>): List<String> {
    return firstRoundResults.entries
        .sortedByDescending { it.value }
        .take(2)
        .map { it.key }
}
fun main() {

    val file = File("src/votes/300x6.txt")
    val votes = readVotes(file)
    val candidateList = candidates(votes)
    val firstRoundResults = firstRound(candidateList, votes)
    val finalCandidates = finalists(firstRoundResults)
    println("First round results: $firstRoundResults")
    println("Finalists: $finalCandidates")

}


