package yuwakisa.fivel.models.tables

import munit.FunSuite

import java.io.*

class LexSpec extends FunSuite:

  def given_input(s: String): BufferedReader =
    BufferedReader(StringReader(s))

  // indent

  test("noIndent") {
    val r = given_input("x    y")
    val in = Lex(r).indent
    assertEquals(in, Some(0))
    assertEquals(r.readLine, "x    y")
  }

  test("indent") {
    val r = given_input("    xy")
    val in = Lex(r).indent
    assertEquals(in, Some(4))
    assertEquals(r.readLine, "xy")
  }

  test("eolIndent") {
    val r = given_input("")
    val in = Lex(r).indent
    assertEquals(in, Some(0))
    assertEquals(r.readLine, null)
  }

  // number

  test("noNumber") {
    val r = given_input("abc")
    val in = Lex(r).number
    assertEquals(in, None)
    assertEquals(r.readLine, "abc")
  }

  test("yesNumber") {
    val r = given_input("123abc")
    val in = Lex(r).number
    assertEquals(in, Some(123))
    assertEquals(r.readLine, "abc")
  }

  test("yesEolNumber") {
    val r = given_input("123")
    val in = Lex(r).number
    assertEquals(in, Some(123))
    assertEquals(r.readLine, null)
  }

  test("eolNumber") {
    val r = given_input("")
    val in = Lex(r).number
    assertEquals(in, None)
    assertEquals(r.readLine, null)
  }

  // Table Start
  test("noTableStart") {
    val r = given_input("blah")
    val in = Lex(r).tableStart
    assertEquals(in, false)
    assertEquals(r.readLine, "blah")
  }

  test("yesTableStart") {
    val r = given_input("= blah")
    val in = Lex(r).tableStart
    assertEquals(in, true)
    assertEquals(r.readLine, " blah")
  }

  // String

  test("yesString") {
    val r = given_input("a, s-trin&g\nb, s-trin&g")
    val in = Lex(r).string
    assertEquals(in, Some("a, s-trin&g"))
    assertEquals(r.readLine, "b, s-trin&g")
  }

  test("yesFunnyCharString") {
    val r = given_input("a, s\u2019trin&g\nb, s-trin&g")
    val in = Lex(r).string
    assertEquals(in, Some("a, s"))
    assertEquals(r.readLine, "trin&g")
  }

  test("yesEolString") {
    val r = given_input("a, s-trin&g")
    val in = Lex(r).string
    assertEquals(in, Some("a, s-trin&g"))
    assertEquals(r.readLine, null)
  }

  test("noString") {
    val r = given_input("")
    val in = Lex(r).string
    assertEquals(in, None)
    assertEquals(r.readLine, null)
  }

  // Comment

  test("noComment") {
    val r = given_input("no comment")
    val in = Lex(r).comment
    assertEquals(in, false)
    assertEquals(r.readLine, "no comment")
  }

  test("yesOneComment") {
    val r = given_input("# comment")
    val lex = Lex(r)
    val in = lex.comment
    assertEquals(in, true)
    assertEquals(r.readLine, null)
    assert(lex.done)
  }

  test("yesComment") {
    val r = given_input("\n\n\n\n# comment\nwoo")
    val in = Lex(r).comment
    assertEquals(in, true)
    assertEquals(r.readLine, "woo")
  }

  test("newlineComment") {
    val r = given_input("\n\n\n\n\nwoo")
    val in = Lex(r).comment
    assertEquals(in, false)
    assertEquals(r.readLine, "woo")
  }

  test("onlyComment") {
    val r = given_input("# comment\nwoo")
    val in = Lex(r).comment
    assertEquals(in, true)
    assertEquals(r.readLine, "woo")
  }

  test("eolComment") {
    val r = given_input("")
    val in = Lex(r).comment
    assertEquals(in, false)
    assertEquals(r.readLine, null)
  }

  // done
  test("noDone") {
    val r = given_input("a")
    val in = Lex(r).done
    assertEquals(in, false)
    assertEquals(r.readLine, "a")
  }

  test("yesDone") {
    val r = given_input("")
    val in = Lex(r).done
    assertEquals(in, true)
    assertEquals(r.readLine, null)
  }
