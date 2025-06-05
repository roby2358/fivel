package yuwakisa.fivel.models.tables

import munit.FunSuite
import yuwakisa.fivel.models.tables.*
import yuwakisa.fivel.models.tables.Parse.State

import java.io.*
import scala.collection.mutable
import scala.util.Random

class TableSpec extends FunSuite:

  implicit val random: Random = Random(1234)

  def given_input(s: String): Parse =
    val r = BufferedReader(StringReader(s))
    Parse(r).go

  test("noPick"):
    val p = given_input("= no-Pick")
    val expected = ""
    val actual = p.tables.head.roll.mkString(",")
    assertEquals(expected, actual)

  test("onePick"):
    val p = given_input("= one-Pick\n-1 o,ne")
    val expected = "o,ne"
    val actual = p.tables.head.roll.mkString(",")
    assertEquals(expected, actual)

  test("twoPick"):
    val p = given_input("= two-Pick\n-1 o,ne\n-1 t'wo")
    val expected = "t'wo"
    val actual = p.tables.head.roll.mkString(",")
    assertEquals(expected, actual)

  test("nestedOnePick"):
    val p = given_input("= nested-One-Pick\n-1 o,ne\n--1 t'wo\n--1 t*hree")
    val expected = "o,ne,t*hree"
    val actual = p.tables.head.roll.mkString(",")
    assertEquals(expected, actual)
