package yuwakisa.fivel.models.tables

import yuwakisa.fivel.models.basic.Shuffle

import scala.collection.mutable
import scala.util.Random

trait Roller :
  def indent: Int
  def freq: Int
  def string: String
  def random: Random

  lazy val rows: mutable.ArrayBuffer[Roller] = mutable.ArrayBuffer[Roller]()

  lazy val shuffle: Shuffle = new Shuffle(cumulative.last, random.nextLong)

  lazy val cumulative: Seq[Int] =
    rows.foldLeft(Seq[Int]()) { case (a, r) =>
      a :+ (a.lastOption.getOrElse(0) + r.freq)
    }

  def mkString(in: Int): String

  def rollOne: Roller =
    if rows.length == 1 then
      return rows.head

    val n = shuffle.draw // random.nextInt(cumulative.last)
    var i = 0
    while n >= cumulative(i) do
      i += 1
    rows(i)