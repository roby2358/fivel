package yuwakisa.fivel.models.tables

import munit.FunSuite
import yuwakisa.fivel.models.basic.Resource

import java.io.*

class PickSpec extends FunSuite:

  test("powers") {
    val p = Resource("/tables/comicbookpowers.txt").reader.map(Parse(_).go).get
    println(p.tables.head.roll.mkString(", "))
  }

  test("grievances") {
    val p = Resource("/tables/medevial_grievance.txt").reader.map(Parse(_).go).get
    println(p.tables.head.roll.mkString(", "))
    println(p.tables.last.roll.mkString(", "))
  }
