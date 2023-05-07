import java.io.File
import kotlin.math.round


//_________________________________SCRUTIN MAJORITAIRE A DEUX TOURS_________________________________
fun readVotes (file:File):  List<List<String>> { //Retourne la liste de liste de candidats
    val candidatesList= mutableListOf<List<String>>()
        file.forEachLine {line->
            if (line.isNotBlank()) {
                candidatesList.add(line.split(","," ").map { name -> name.trim() })
            }
        }
        return candidatesList

}

fun candidates(votes: List<List<String>>): List<String> { //Retourne la liste de candidats par l'ordre alphabétique
    val candidates = mutableSetOf<String>()
    votes.forEach { candidates.addAll(it) }
    return candidates.sorted()
}

data class FirstRoundResult(val candidate: String, val score: Int) //Retourne le nombre de votes par candidat
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
    return firstRoundResults.entries.map { FirstRoundResult(it.key, it.value) }.sortedBy { it.candidate }
}


fun finalists(firstRoundResults: List<FirstRoundResult>): List<String> { //Retourne les deux candidats finalistes
    return firstRoundResults.take(2).map { it.candidate }
}

//Refaire la fonction finalists sa

data class SecondRoundResult(val candidat_1: String, val score_1: Int, val candidat_2: String, val score_2: Int, val abstentions: Int)
fun secondRound(votes: List<List<String>>, finalCandidates: List<String>): SecondRoundResult {
    var score1 = 0
    var score2 = 0

    votes.forEach { ballot -> //On parcourt les votes
        val preferredCandidate = ballot.firstOrNull { it in finalCandidates }
        when (preferredCandidate) { //On incrémente le score du candidat préféré
            finalCandidates[0] -> score1++
            finalCandidates[1] -> score2++
        }
    }

    val abstentions = votes.size - (score1 + score2) //On calcule le nombre d'abstentions

    return SecondRoundResult(finalCandidates[0], score1, finalCandidates[1], score2, abstentions)
}


fun displayresults(firstRoundResults: List<FirstRoundResult>, secondRoundResults: SecondRoundResult) { //Affiche les résultats
    println("\n Premier tour ")
    firstRoundResults.forEach { result ->
        println("${result.candidate} : ${result.score} voix")
    }

    var total = secondRoundResults.score_1 + secondRoundResults.score_2
    var pourcentage_first = round((secondRoundResults.score_1.toDouble() / total) * 100 * 100) / 100
    var pourcentage_second = round((secondRoundResults.score_2.toDouble() / total) * 100 * 100) / 100


    println("\n Second tour entre ${secondRoundResults.candidat_1} et ${secondRoundResults.candidat_2}" +
            "\n ${secondRoundResults.candidat_1} : ${secondRoundResults.score_1} voix (${pourcentage_first}%)" +
            "\n ${secondRoundResults.candidat_2} : ${secondRoundResults.score_2} voix (${pourcentage_second}%)" +
            "\n Abstentions : ${secondRoundResults.abstentions} voix \n")
}
//_________________________________END_________________________________




//_________________________________METHODE DE CONDORCET_________________________________
fun pairwiseComparisons(candidates: List<String>, ballot: List<String>): List<List<Int>> { //Retourne la matrice d'entiers
    val pairwiseComparisons = mutableListOf<List<Int>>()
    candidates.forEach { candidate ->
        val pairwiseComparison = mutableListOf<Int>()
        candidates.forEach { otherCandidate ->
            if (candidate == otherCandidate) {
                pairwiseComparison.add(0)
            } else {
                val candidateIndex = ballot.indexOf(candidate)
                val otherCandidateIndex = ballot.indexOf(otherCandidate)
                if (candidateIndex < otherCandidateIndex || otherCandidateIndex == -1) {
                    pairwiseComparison.add(1)
                } else {
                    pairwiseComparison.add(0)
                }
            }

        }
        pairwiseComparisons.add(pairwiseComparison)
    }
    return pairwiseComparisons
}

fun displayMatrice(pairwiseComparisons: List<List<Int>>) { //Affiche la matrice de pairwiseComparisons
    pairwiseComparisons.forEach { row ->
        row.forEach { value ->
            print("$value ")
        }
        println()
    }
}

fun sum(pairwiseComparisons1: List<List<Int>>, pairwiseComparisons2: List<List<Int>>): List<List<Int>> { //Retourne la matrice somme
    val sum = mutableListOf<List<Int>>()
    pairwiseComparisons1.forEachIndexed { rowIndex, row ->
        val sumRow = mutableListOf<Int>()
        row.forEachIndexed { colIndex, value ->
            sumRow.add(value + pairwiseComparisons2[rowIndex][colIndex])
        }
        sum.add(sumRow)
    }
    return sum
}

fun condorcetMatrix(candidates: List<String>, ballots: List<List<String>>): List<List<Int>> {
    val pairwiseComparisons = MutableList(candidates.size) { MutableList(candidates.size) {0} }

    for (ballot in ballots) {
        for (i in candidates.indices) {
            for (j in i + 1 until candidates.size) {
                val candidate1 = candidates[i]
                val candidate2 = candidates[j]
                if (ballot.indexOf(candidate1) < ballot.indexOf(candidate2)) {
                    pairwiseComparisons[i][j]++
                } else {
                    pairwiseComparisons[j][i]++
                }
            }
        }
    }
    return pairwiseComparisons
}

fun condorcetWinner(candidates: List<String>, condorcetMatrix: List<List<Int>>): String? {
    for (i in candidates.indices) {
        var wins = true
        for (j in candidates.indices) {
            if (i == j) continue
            if (condorcetMatrix[i][j] <= condorcetMatrix[j][i]) {
                wins = false
                break
            }
        }
        if (wins) return candidates[i]
    }
    return null
}


//_________________________________END_________________________________

fun main() {

    //_________________________________SCRUTIN MAJORITAIRE A DEUX TOURS_________________________________

    val file = File("src/votes/sample.txt")
    val votes = readVotes(file) //Retourne la liste de liste de candidats avec la fonction readVotes (EXERCICE 1)
    //println(votes)

    val candidatesSort = candidates(votes) //Retourne la liste de candidats par l'ordre alphabétique (EXERCICE 2)
    //println(candidates_sort)

    val firstRoundResults = firstRound(candidatesSort, votes) //Retourne le nombre de votes par candidat (EXERCICE 3)
    //println(firstRoundResults)

    val finalCandidates = finalists(firstRoundResults) //Retourne les deux candidats finalistes (EXERCICE 4)
    //println(finalCandidates)

    val secondRoundResults = secondRound(votes, finalCandidates) //Retourne le résultat du second tour (EXERCICE 5)
    //println(secondRoundResults)

    displayresults(firstRoundResults, secondRoundResults) //Affiche les résultats (EXERCICE 6)

    //_________________________________METHODE DE CONDORCET_________________________________

    val pairwiseComparisonsResult = pairwiseComparisons(candidatesSort, votes[0]) //Retourne la matrice 1(EXERCICE 7)
    //println("Resultat matrice 1: ${displayMatrice(pairwiseComparisonsResult)}")

    val pairwiseComparisonsResult2 = pairwiseComparisons(candidatesSort, votes[1]) //Retourne la matrice 2(EXERCICE 7)
    //println("Resultat matrice 2: ${displayMatrice(pairwiseComparisonsResult2)}")

    val sumResult = sum(pairwiseComparisonsResult, pairwiseComparisonsResult2) //Retourne la matrice somme (EXERCICE 8)
    println("Resultat matrice: ${displayMatrice(pairwiseComparisonsResult2)}")

    val condorcetMatrixResult = condorcetMatrix(candidatesSort, votes)
    println("Résultlat matrice de Condorcet: ${displayMatrice(condorcetMatrixResult)}")

    val condorsetWinnerResult = condorcetWinner(candidatesSort, condorcetMatrixResult)
    println("Le gagnant de l'élection de Condorcet est : $condorsetWinnerResult")
}