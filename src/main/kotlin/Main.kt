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

data class FirstRoundResult(val candidate: String, val score: Int)
fun firstRound(candidates: List<String>, votes: List<List<String>>): List<FirstRoundResult> {
    val firstRoundResults = mutableMapOf<String, Int>()
    candidates.forEach { candidate -> firstRoundResults[candidate] = 0 }
    votes.forEach { ballot ->
        ballot.forEachIndexed { index, candidate ->
            if (index == 0) {
                firstRoundResults[candidate] = firstRoundResults[candidate]!! + 1
            }
        }
    }
    return firstRoundResults.entries.map { FirstRoundResult(it.key, it.value) }.sortedByDescending { it.score }
}




fun finalists(firstRoundResults: List<FirstRoundResult>): List<String> {
    return firstRoundResults.take(2).map { it.candidate }
}

data class SecondRoundResult(val candidat_1: String, val score_1: Int, val candidat_2: String, val score_2: Int, val abstentions: Int)
fun secondRound(votes: List<List<String>>, finalCandidat: List<String>): SecondRoundResult {
    var score1 = 0
    var score2 = 0
    var abstentions = 0

    votes.forEach { ballot ->
        when (val firstPreference = ballot[0]) {
            finalCandidat[0] -> score1++
            finalCandidat[1] -> score2++
            else -> {
                val secondPreference = ballot.getOrElse(1) { firstPreference }
                when (secondPreference) {
                    finalCandidat[0] -> score1++
                    finalCandidat[1] -> score2++
                    else -> abstentions++
                }
            }
        }
    }

    return SecondRoundResult(finalCandidat[0], score1, finalCandidat[1], score2, abstentions)
}

//Écrire une fonction qui affiche dans la console le résultat de l'élection. Pour le deuxième tour,
//les pourcentages des deux candidats seront calculés sur la base des suffrages exprimés. (2
//points)

fun displayresults(firstRoundResults: List<FirstRoundResult>, secondRoundResults: SecondRoundResult) {
    println("\n Premier tour ")
    firstRoundResults.forEach { result ->
        println("${result.candidate} : ${result.score} voix")
    }
    println("\n Second tour \n ${secondRoundResults.candidat_1} : ${secondRoundResults.score_1} voix" +
            "\n ${secondRoundResults.candidat_2} : ${secondRoundResults.score_2} voix" +
            "\n Abstentions : ${secondRoundResults.abstentions} voix")
}




fun main() {

    val file = File("src/votes/300x6.txt")
    val votes = readVotes(file) //Retourne la liste de liste de candidats avec la fonction readVotes (EXERCICE 1)

    val candidates_sort = candidates(votes) //Retourne la liste de candidats par l'ordre alphabétique (EXERCICE 2)


    val firstRoundResults = firstRound(candidates_sort, votes) //Retourne le nombre de votes par candidat (EXERCICE 3)

    val finalCandidates = finalists(firstRoundResults) //Retourne les deux candidats finalistes (EXERCICE 4)

    val secondRoundResults = secondRound(votes, finalCandidates) //Retourne le résultat du second tour (EXERCICE 5)


    displayresults(firstRoundResults, secondRoundResults) //Affiche les résultats (EXERCICE 6)

    //val candidateList = candidates(votes)
    //val firstRoundResults = firstRound(candidateList, votes)
    //val finalCandidates = finalists(firstRoundResults)
    //println("First round results: $firstRoundResults")
    //println("Finalists: $finalCandidates")

}


