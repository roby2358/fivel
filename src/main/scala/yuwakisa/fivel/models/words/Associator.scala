package yuwakisa.fivel.models.words

import scala.collection.mutable
import scala.util.Random

/**
 * Companion object for Associator containing constants and utility methods
 */
object Associator:
  private val Start = "<"
  private val End = ">"

  /**
   * Removes the start and end markers from a string
   * @param s The string to clean
   * @return The cleaned string
   */
  def clean(s: String): String = s.replace(Start, "").replace(End, "")

/**
 * A Markov chain-based word generator that learns patterns from a sequence of words
 * @param maxN The maximum n-gram size to consider
 * @param words The sequence of words to learn from
 * @param random The random number generator to use
 */
class Associator(maxN: Int, val words: Seq[String])(implicit random: Random):
  // Map of n-grams to their possible next n-grams and their frequencies
  val links: mutable.Map[String, mutable.Map[String, Int]] =
    mutable.Map().withDefault(_ => mutable.Map().withDefault(_ => 0))

  /**
   * Generates all possible n-gram pairs from a word
   * @param word The word to generate n-gram pairs from
   * @return A sequence of (prefix, suffix) pairs
   */
  def ngramPairs(word: String): Seq[(String, String)] =
    val wordWithMarkers = Associator.Start + word + Associator.End
    
    for
      prefixLength <- 1 to math.min(maxN, wordWithMarkers.length - 1)
      suffixLength <- 1 to math.min(maxN, wordWithMarkers.length - prefixLength)
      startIndex <- 0 to wordWithMarkers.length - prefixLength - suffixLength
    yield
      val prefix = wordWithMarkers.substring(startIndex, startIndex + prefixLength)
      val suffix = wordWithMarkers.substring(startIndex + prefixLength, startIndex + prefixLength + suffixLength)
      (prefix, suffix)

  /**
   * Increments a counter, initializing it to 1 if it doesn't exist
   */
  private def incrementCounter(count: Option[Int]): Option[Int] = 
    count.map(_ + 1).orElse(Some(1))

  /**
   * Builds the Markov chain by analyzing the input words
   * @return this Associator instance
   */
  def build(): Associator =
    words.foreach { word =>
      ngramPairs(word).foreach { case (prefix, suffix) =>
        links.update(prefix, links(prefix))
        links(prefix).updateWith(suffix)(incrementCounter)
      }
    }
    this

  /**
   * Gets all possible next n-grams and their frequencies for a given prefix
   * @param prefix The prefix to find continuations for
   * @return A map of possible next n-grams to their frequencies
   */
  private def getPossibleContinuations(prefix: String): mutable.Map[String, Int] =
    val continuations: mutable.Map[String, Int] = mutable.Map().withDefault(_ => 0)
    
    for
      ngramLength <- 1 to math.min(maxN, prefix.length)
      (nextNgram, frequency) <- links(prefix.substring(prefix.length - ngramLength))
    do
      continuations.updateWith(nextNgram) { count => 
        count.map(_ + frequency).orElse(Some(frequency))
      }
    
    continuations

  /**
   * Randomly selects a next n-gram based on frequency weights
   * @param weightedChoices The possible choices and their weights
   * @return The selected n-gram
   */
  private def selectWeightedChoice(weightedChoices: Seq[(String, Int)]): String =
    val totalWeight = weightedChoices.map(_._2).sum
    val randomValue = random.nextInt(totalWeight)
    
    weightedChoices.foldLeft(("", randomValue)) {
      case ((selected, remaining), (choice, weight)) if remaining >= 0 => 
        (choice, remaining - weight)
      case ((selected, remaining), _) => 
        (selected, remaining)
    }._1

  /**
   * Generates a new word using the learned patterns
   * @return A generated word
   */
  def roll(): String =
    var currentWord = Associator.Start
    var continuations: mutable.Map[String, Int] = mutable.Map()
    
    while
      continuations = getPossibleContinuations(currentWord)
      continuations.nonEmpty
    do
      currentWord += selectWeightedChoice(continuations.toSeq)
    
    Associator.clean(currentWord)

  /**
   * Generates a new word that doesn't exist in the input word list
   * @return A unique generated word
   */
  def uniq(): String =
    LazyList.continually(roll())
      .find(!words.contains(_))
      .get
