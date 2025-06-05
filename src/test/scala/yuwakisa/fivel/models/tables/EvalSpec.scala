package yuwakisa.fivel.models.tables

import munit.FunSuite

import java.io.*

class EvalSpec extends FunSuite:

  object Ev extends Eval with TestEvalinator

  test("plain"):
    val got = Ev.eval("a,aa")
    assertEquals("a,aa", got)

  test("evaluated"):
    val got = Ev.eval("{b-bb}")
    assertEquals("B-BB", got)

  test("mix1"):
    val got = Ev.eval("a,aa{b-bb}")
    assertEquals("a,aaB-BB", got)

  test("mix2"):
    val got = Ev.eval("{b-bb}c,cc")
    assertEquals("B-BBc,cc", got)

  test("mix3"):
    val got = Ev.eval("a,aa{b-bb}c*cc{d=dd}e&ee")
    assertEquals("a,aaB-BBc*ccD=DDe&ee", got)

  test("dice0"):
    val got = Ev.eval("[]")
    assertEquals("1006", got)

  test("dice1"):
    val got = Ev.eval("[2d5]")
    assertEquals("2005", got)

  test("dice2"):
    val got = Ev.eval("[d7]")
    assertEquals("1007", got)

  test("dice3"):
    val got = Ev.eval("[3d]")
    assertEquals("3006", got)

  test("dice4"):
    val got = Ev.eval("[d]")
    assertEquals("1006", got)
