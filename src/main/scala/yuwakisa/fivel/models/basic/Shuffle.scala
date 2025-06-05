package yuwakisa.fivel.models.basic

import scala.collection.mutable
import scala.collection.mutable.ArrayBuffer
import scala.util.Random

object Shuffle:
  class Deck(n: Int, seed: Long):
    val deck: mutable.Seq[Int] = ArrayBuffer.from(0 until n)
    private val rr = new Random(seed)

    /** swap an item with a random item */
    def swap(i: Int): Unit =
      val j = i + rr.nextInt(deck.length - i)
      val t = deck(i)
      deck(i) = deck(j)
      deck(j) = t

    def length: Int = deck.length

    def apply(n: Int): Int = deck(n)

class Shuffle(n: Int, seed: Long) extends Draw:

  var m: Int = 0
  private var current_seed: Long = seed

  /**
   * Draw the next item by walking through 0..m
   *
   * Trades memory for CPU
   */
  override def draw: Int =
    // if out of cards, back to start
    if m >= n then
      m = 0
      current_seed = new Random(current_seed).nextLong

    val deck = new Shuffle.Deck(n, current_seed)

    (0 to m).foreach({ i => deck.swap(i) })
    val v = deck(m)
    m = m + 1
    v
