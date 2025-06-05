package yuwakisa.fivel.models.words

import munit.FunSuite
import yuwakisa.fivel.models.basic.Resource

import scala.collection.mutable
import scala.util.Random

class GenUniqSpec extends FunSuite:

  implicit val random: Random = Random()

  def counter: mutable.Map[String, Int] = new scala.collection.mutable.HashMap[String, Int]().withDefaultValue(0)

  test("rollUniq"):
    val a = Associator(3, Seq("abcd", "abcx", "abcy", "abcz")).build()
    val c = counter
    (0 until 4000).foreach { _ =>
      c(a.roll()) += 1
    }
    println(c)

  test("bondVillains"):
    val names = Resource("/words/bondvillains.txt").toSeq
    println(names)
    val a = Associator(5, names).build()
    for (i <- 0 until 20)
      println(a.roll())

/*
Horor Linic
Kongo Elede
Lee Frifex
Maxwiton Monro
Sereves
Fran Lior Dextex
Jeantin Hugmun
Hecto Safin
Sig Weisen
Mariguez
Gluggsy Maringo
Espadafina Severa
Volon von Budam
Goldovan Gol Kobudevicher
Nenasco Deng
*/

  test("english"):
    val names = Resource("/words/english.txt").toSeq
    val a = Associator(5, names).build()
    for (i <- 0 until 20)
      println(a.uniq())

/*
wacteriention
compty
sententist
convimpt
soluce
appainfuago
menunonoontinsise
inglishat
vilie
opretal
resebration
mobal
indmire
*/