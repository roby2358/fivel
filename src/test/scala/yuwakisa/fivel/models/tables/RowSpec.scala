package yuwakisa.fivel.models.tables

import munit.FunSuite

import java.io.*

class RowSpec extends FunSuite:
  def given_input(s: String): Parse =
    val r = BufferedReader(StringReader(s))
    Parse(r).go

  test("oneCumulaive"):
    val in = """= Dist
        |  10 o,ne
        |""".stripMargin
    val t = given_input(in)
    assertEquals(Seq(10), t.tables.head.cumulative)

  test("threeCumulaive"):
    val in = """= Dist
               |  10 o,ne
               |    2 o-ne
               |    4 o&ne
               |  20 o=ne
               |  30 o!ne
               |""".stripMargin
    val t = given_input(in)
    assertEquals(Seq(10, 30, 60), t.tables.head.cumulative)
