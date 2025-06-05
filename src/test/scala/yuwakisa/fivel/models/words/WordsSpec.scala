package yuwakisa.fivel.models.words

import munit.FunSuite
import yuwakisa.fivel.models.basic.Resource

import scala.util.Random

class WordsSpec extends FunSuite:

  implicit val random: Random = Random(1234567890)

  test("name"):
    val actual = Words.name("/blah/blah/blah/zing.txt")
    val expected = "zing"
    assertEquals(actual, expected)

  test("namePlain"):
    val actual = Words.name("zoop")
    val expected = "zoop"
    assertEquals(actual, expected)

  test("parseOne"):
    val (n, a) = Words.parseOne("/resources/a.txt")
    println(n)
    println(a)
    assertEquals(n, "a")
    assertEquals(a.words.head, "abx")

  test("load"):
    val r = Resource("/resources")
    println(r.toSeq)
    val actual = Words.load(r.paths)
    assertEquals(actual.toSeq, Seq("a", "b", "c"))
    assertEquals(actual.uniq("b"), Right("aced"))
