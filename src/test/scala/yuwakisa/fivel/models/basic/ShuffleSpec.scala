package yuwakisa.fivel.models.basic

import munit.FunSuite
import yuwakisa.fivel.models.basic.Shuffle

import scala.collection.mutable.ArrayBuffer
import scala.util.Random

class ShuffleSpec extends FunSuite:

  val seed = 1234

  test("deckLength"):
    val deck = new Shuffle.Deck(5, seed)
    assertEquals(deck.length, 5)

  test("swap"):
    val deck = new Shuffle.Deck(5, seed)
    deck.swap(0)
    deck.swap(2)
    val expected = ArrayBuffer(3, 1, 4, 0, 2)
    assertEquals(deck.deck, expected)

  test("swapAll"):
    val deck = new Shuffle.Deck(5, seed)
    deck.swap(0)
    assertEquals(deck.deck, ArrayBuffer(3, 1, 2, 0, 4))
    deck.swap(1)
    assertEquals(deck.deck, ArrayBuffer(3, 2, 1, 0, 4))
    deck.swap(2)
    assertEquals(deck.deck, ArrayBuffer(3, 2, 4, 0, 1))
    deck.swap(3)
    assertEquals(deck.deck, ArrayBuffer(3, 2, 4, 0, 1))
    deck.swap(4)
    assertEquals(deck.deck, ArrayBuffer(3, 2, 4, 0, 1))

  test("pull0"):
    val shuffle = new Shuffle(5, seed)
    val actual = shuffle.draw
    val expected = 3
    assertEquals(actual, expected)

  test("pullSome"):
    val shuffle = new Shuffle(5, seed)
    val actual = (0 until 10).map({ i => shuffle.draw })
    assertEquals(actual, Seq(3, 2, 4, 0, 1, 3, 1, 2, 0, 4))
