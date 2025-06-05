package yuwakisa.fivel.models.words

import munit.FunSuite

import scala.collection.mutable
import scala.util.Random

class AssociatorSpec extends FunSuite:

  implicit val random: Random = Random(1234)

  test("ngramPairsLong") {
    val actual = Associator(3, null).ngramPairs("bcde")
    val expected = Seq(
      ("<","b"), ("b","c"), ("c","d"), ("d","e"), ("e",">"),
      ("<","bc"), ("b","cd"), ("c","de"), ("d","e>"),
      ("<","bcd"), ("b","cde"), ("c","de>"),
      ("<b","c"), ("bc","d"), ("cd","e"), ("de",">"),
      ("<b","cd"), ("bc","de"), ("cd","e>"),
      ("<b","cde"), ("bc","de>"),
      ("<bc","d"), ("bcd","e"), ("cde",">"),
      ("<bc","de"), ("bcd","e>"),
      ("<bc","de>"))
    assertEquals(actual, expected)
  }

  test("ngramPairs2") {
    val actual = Associator(3, null).ngramPairs("b")
    val expected = Seq(
      ("<","b"), ("b",">"),
      ("<","b>"),
      ("<b",">"))
    assertEquals(actual, expected)
  }

  test("ngramPairs1") {
    val actual = Associator(3, null).ngramPairs("")
    val expected = Seq(("<",">"))
    assertEquals(actual, expected)
  }

  test("build2") {
    val actual = Associator(3, Seq("")).build().links
    val expected = mutable.Map("<" -> mutable.Map(">" -> 1))
    assertEquals(actual, expected)
  }

  test("build3") {
    val actual = Associator(3, Seq("b")).build().links
    val expected = mutable.Map(
      "<" -> mutable.Map("b" -> 1, "b>" -> 1),
      "<b" -> mutable.Map(">" -> 1),
      "b" -> mutable.Map(">" -> 1))
    assertEquals(actual, expected)
  }

  test("linksStart") {
    val actual = Associator(3, Seq("a")).build().links("<").toSet
    val expected = Set(("a",1), ("a>",1))
    assertEquals(actual, expected)
  }

  test("linksLast") {
    val actual = Associator(3, Seq("abc")).build().links("c").toSet
    val expected = Set((">",1))
    assertEquals(actual, expected)
  }

  test("linksMiddle") {
    val actual = Associator(3, Seq("abc")).build().links("a").toSet
    val expected = Set(("b",1), ("bc",1), ("bc>",1))
    assertEquals(actual, expected)
  }

  test("linksEnd") {
    val actual = Associator(3, Seq("abc")).build().links(">").toSet
    val expected: Set[(String, Int)] = Set()
    assertEquals(actual, expected)
  }

  test("gen1") {
    val actual = Associator(3, Seq("b")).build().roll()
    val expected = "b"
    assertEquals(actual, expected)
  }

  test("gen2") {
    val actual = Associator(3, Seq("abcde")).build().roll()
    val expected = "abcde"
    assertEquals(actual, expected)
  }

  test("gen3") {
    val actual = Associator(3, Seq("abcx", "abcx", "abcy", "abcz")).build().roll()
    val expected = "abcz"
    assertEquals(actual, expected)
  }

  test("uniq1") {
    val actual = Associator(3, Seq("abcx", "abcx", "abcy", "abcz", "abbc", "accb")).build().uniq()
    val expected = "abc"
    assertEquals(actual, expected)
  }
