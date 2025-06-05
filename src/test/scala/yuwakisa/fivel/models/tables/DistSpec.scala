package yuwakisa.fivel.models.tables

import munit.FunSuite

import java.io.*
import scala.collection.mutable

class DistSpec extends FunSuite:

  def given_input(s: String): Parse =
    val r = BufferedReader(StringReader(s))
    Parse(r).go

  def counter: mutable.Map[String, Int] = new scala.collection.mutable.HashMap[String, Int]().withDefaultValue(0)

  test("onetwothree") {
    val in = """= Dist
        |  1 one
        |  2 two
        |  3 three
        |""".stripMargin

    val t = given_input(in)
    val c = counter
    (0 until 6000).foreach { _ =>
        c(t.tables.head.roll.mkString(":")) += 1
    }
    println(c)
  }

  test("nested") {
    val in = """= Dist
               |  1 one
               |  2 two
               |    1 twoA
               |    1 twoB
               |  3 three
               |    1 threeA
               |    1 threeB
               |    1 threeC
               |""".stripMargin

    val t = given_input(in)
    val c = counter
    (0 until 6000).foreach {
      _ =>
        c(t.tables.head.roll.mkString(":")) += 1
    }
    println(c)
  }