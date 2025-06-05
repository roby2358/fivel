package yuwakisa.fivel.models.tables

import scala.collection.mutable
import scala.util.Random

case class Table(name: String)(implicit val random: Random = Random()) extends Roller:
  val indent: Int = 0
  val freq: Int = 1
  val string: String = name

  def mkString(i: Int = indent): String =
    s"= $name\n" + rows.map(_.mkString(2)).mkString("")

  def roll: Seq[String] =
    val seq = mutable.ArrayBuilder.make[String]

    var r: Roller = this

    while r.rows.nonEmpty do
      val rr = r.rollOne
      seq.addOne(rr.string)
      r = rr

    seq.result.toSeq