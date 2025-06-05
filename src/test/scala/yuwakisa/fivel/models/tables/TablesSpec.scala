package yuwakisa.fivel.models.tables

import munit.Assertions.*
import munit.FunSuite
import yuwakisa.fivel.models.tables.*
import yuwakisa.fivel.models.tables.Parse.State

import java.io.*
import scala.collection.mutable
import scala.util.Random

class TablesSpec extends FunSuite:

  implicit val random: Random = Random(1234)

  def parseOne(s: String): Seq[Table] =
    val r = BufferedReader(StringReader(s))
    Parse(r).go.tables.toSeq

  def given_input(s: Seq[String]): Tables =
    val tables = s.flatMap(parseOne)
    val tbyn = Tables.seqToMap(tables)
    Tables(tbyn)

  val tableA: String = "= a\n- 1 a-a"
  val tableB: String = "= b\n- 1 b,b{a}"
  val tableC: String = "= c\n- 1 [] [2d6] [5d12]"

  test("one") {
    val t = given_input(Seq(tableA))
    val expected = Right("a-a")
    val actual = t.apply("a")(_.mkString(","))
    assertEquals(actual, expected)
  }

  test("two") {
    val t = given_input(Seq(tableA, tableB))
    val expected = Right("b,ba-a")
    val actual = t.apply("b")(_.mkString(","))
    assertEquals(actual, expected)
  }

  test("oneDice") {
    val t = given_input(Seq(tableC))
    val expected = Right("3 12 18")
    val actual = t.apply("c")(_.mkString(","))
    assertEquals(actual, expected)
  }
