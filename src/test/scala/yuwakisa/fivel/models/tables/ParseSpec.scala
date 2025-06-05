package yuwakisa.fivel.models.tables

import munit.Assertions.*
import munit.FunSuite
import yuwakisa.fivel.models.tables.*
import yuwakisa.fivel.models.tables.Parse.State

import java.io.*
import scala.collection.mutable

class ParseSpec extends FunSuite:

  def given_input(s: String): (BufferedReader, Parse) =
    val r = BufferedReader(StringReader(s))
    (r, Parse(r))

  test("yesComment") {
    // for now, there has to be a newline or it loops infinitely :\
    val input = "# comment\n"
    val (r, p) = given_input(input)
    p.go
    assertEquals(p.state, State.StartLine)
    assert(p.lex.done)
    assertEquals(null, r.readLine)
  }

  test("oneTable") {
    val input = "o,neT-able"
    val expected = """= o,neT-able
      |""".stripMargin.replaceAll("[\r]","")
    val (r, p) = given_input(input)
    p.go
    assertEquals(null, r.readLine)
    assertEquals(p.mkString, expected)
    assertEquals(p.tables.length, 1)
    assertEquals(p.tables(0), Table("o,neT-able"))
  }

  test("twoTables") {
    val input = "t,woT-ables\nb, t-able"
    val expected = """= t,woT-ables
                     |= b, t-able
                     |""".stripMargin.replaceAll("[\r]","")
    val (r, p) = given_input(input)
    p.go
    assertEquals(p.mkString, expected)
    assertEquals(p.tables.length, 2)
    assertEquals(null, r.readLine)
  }

  test("missingTable") {
    val input = "  1 row\n"
    val (r, p) = given_input(input)
    p.go
    assert(p.tables.isEmpty)
  }

  test("oneRow") {
    val input = "oneRow\n 10 row"
    val expected = """= oneRow
                     |-- 10 row
                     |""".stripMargin.replaceAll("[\r]","")
    val (r, p) = given_input(input)
    p.go
    assertEquals(p.mkString, expected)
    assertEquals(p.tables.length, 1)
    assertEquals(p.tables(0).rows.length, 1)
    assertEquals(null, r.readLine)

    val table0 = p.tables(0)
    assertEquals(table0.name, "oneRow")

    assertEquals(table0.rows.length, 1)
    val row0 = table0.rows(0)
    assertEquals(row0.indent, 1)
    assertEquals(row0.freq, 10)
    assertEquals(row0.string, "row")
  }

  test("oneRowNoCount") {
    val input = "singleRow\n first"
    val expected = """= singleRow
                     |-- 1 first
                     |""".stripMargin.replaceAll("[\r]","")
    val (r, p) = given_input(input)
    p.go
    assertEquals(p.mkString, expected)
    assertEquals(p.tables.length, 1)
    assertEquals(p.tables(0).rows.length, 1)
    assertEquals(null, r.readLine)

    val table0 = p.tables(0)
    assertEquals(table0.name, "singleRow")

    assertEquals(table0.rows.length, 1)
    val row0 = table0.rows(0)
    assertEquals(row0.indent, 1)
    assertEquals(row0.freq, 1)
    assertEquals(row0.string, "first")
  }

  test("twoRows") {
    val input = "twoRows\n 1 row\n 2 wor"
    val expected = """= twoRows
                     |-- 1 row
                     |-- 2 wor
                     |""".stripMargin.replaceAll("[\r]","")
    val (r, p) = given_input(input)
    p.go
    assertEquals(p.mkString, expected)
    println(p.tables)
    assertEquals(p.tables.length, 1)
    assertEquals(p.tables(0).rows.length, 2)
    assertEquals(null, r.readLine)
  }

  test("twoTablesWitRows") {
    val input = "twoTablesWitRows\n 1 baluga\nother table\n   1 some row"
    val expected = """= twoTablesWitRows
                     |-- 1 baluga
                     |= other table
                     |-- 1 some row
                     |""".stripMargin.replaceAll("[\r]","")
    val (r, p) = given_input(input)
    p.go
    assertEquals(p.mkString, expected)
    assertEquals(p.tables.length, 2)
    assertEquals(p.tables(0).rows.length, 1)
    assertEquals(p.tables(1).rows.length, 1)
    assertEquals(null, r.readLine)
  }

  test("nestedRows") {
    val input = "nestedRows\n-2 wombat\n----3 tiger\n----4 duck\n-5 zebra"
    val expected = """= nestedRows
                     |-- 2 wombat
                     |---- 3 tiger
                     |---- 4 duck
                     |-- 5 zebra
                     |""".stripMargin.replaceAll("[\r]","")
    val (r, p) = given_input(input)
    p.go
    assertEquals(p.mkString, expected)

    assertEquals(p.tables.length, 1)

    val table0 = p.tables(0)
    assertEquals(table0.name, "nestedRows")

    assertEquals(table0.rows.length, 2)
    val row0 = table0.rows(0)
    assertEquals(row0.indent, 1)
    assertEquals(row0.freq, 2)
    assertEquals(row0.string, "wombat")

    assertEquals(row0.rows.length, 2)

    assertEquals(row0.rows(0).indent, 4)
    assertEquals(row0.rows(0).freq, 3)
    assertEquals(row0.rows(0).string, "tiger")

    assertEquals(row0.rows(1).indent, 4)
    assertEquals(row0.rows(1).freq, 4)
    assertEquals(row0.rows(1).string, "duck")

    val row1 = table0.rows(1)
    assertEquals(row1.indent, 1)
    assertEquals(row1.freq, 5)
    assertEquals(row0.string, "wombat")

    assertEquals(null, r.readLine)
  }

  test("nestedRows2") {
    val input = "nestedRows2\n 2 aaa\n    3 bbb\n  4 ccc"
    val expected = """= nestedRows2
                     |-- 2 aaa
                     |---- 3 bbb
                     |---- 4 ccc
                     |""".stripMargin.replaceAll("[\r]","")
    val (r, p) = given_input(input)
    p.go
    assertEquals(p.mkString, expected)
  }

  test("yesParse") {
    val input = """

# some stuff
# more stuff
bings
  1   bing
    1    bang
      # more
      1 zing
----1 bling

booms
  # woo
--1 boom
  # done
"""
    val expected = """= bings
                     |-- 1 bing
                     |---- 1 bang
                     |------ 1 zing
                     |---- 1 bling
                     |= booms
                     |-- 1 boom
                     |""".stripMargin.replaceAll("[\r]","")

    val (r, p) = given_input(input)
    p.go
    assertEquals(p.mkString, expected)
    assertEquals(null, r.readLine)
  }

  test("roundTrip") {
    val input = """= roundTrip
               |-- 1234567890 bingo bongo
               |""".stripMargin.replaceAll("[\r]","")
    val (r0, p0) = given_input(input)
    p0.go
    val (r1, p1) = given_input(p0.mkString)
    p1.go
    assertEquals(p1.mkString, input)
    assertEquals(null, r1.readLine)
  }
