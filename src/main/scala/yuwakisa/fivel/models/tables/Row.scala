package yuwakisa.fivel.models.tables

import scala.collection.mutable
import scala.util.Random

case class Row(indent: Int, freq: Int, string: String)(implicit val random: Random = Random()) extends Roller :
  def mkString(in: Int): String =
    s"${"-" * in} $freq $string\n" + rows.map(_.mkString(in + 2)).mkString("")
