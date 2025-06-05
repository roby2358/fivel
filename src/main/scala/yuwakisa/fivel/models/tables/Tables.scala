package yuwakisa.fivel.models.tables

import yuwakisa.fivel.Logging

import scala.util.Random
import yuwakisa.fivel.models.basic.Resource

object Tables extends Logging :
  lazy val Sources: Seq[String] = Resource("/tables").paths
  lazy val All: Tables = Tables.load(Sources)
  lazy val AllNames: Seq[String] = All.toSeq.filter { n => !n.startsWith("|") }
  lazy val AllMcpNames: Seq[String] = AllNames
    .map { n => n.replace(" ", "_") }
  
  /**
   * Parse a single resource for a table
   * @param path path to the resource
   * @return tables
   */
  def parseOne(path: String): Seq[Table] =
    logger.debug(s"parse $path")
    Resource(path).reader
      .map(Parse(_).go.tables.toSeq)
      .getOrElse(Seq())

  /**
   * Turn a list of tables into a map by name
   * @param s sequence of tables
   * @return map of the tables by name
   */
  def seqToMap(s: Seq[Table]): Map[String, Table] =
    s.map(t => (t.name.toLowerCase, t))
      .toMap

  /**
   * Load all the tables from a list of sources
   * @param sources list of resource paths
   * @return tables from all the sources
   */
  def load(sources: Seq[String])(implicit random: Random = Random()): Tables =
    val tables = sources.flatMap(parseOne)
    val tablesByName = seqToMap(tables)
    Tables(tablesByName)

  def mkString(ss: Seq[String]): String =
    ss.mkString(", ").replace("""\n""", "\n")

class Tables(val tablesByName: Map[String, Table])(implicit val random: Random = Random()) :

  trait TableEvalinator extends Evalinator :

    def evaluate(s: String): String =
      apply(s)(Tables.mkString).getOrElse("Nope")

    def rollDice(n: Int, sides: Int): Int =
      (0 until n).map({_ => 1 + random.nextInt(sides)}).sum

  object Ev extends Eval with TableEvalinator

  def toSeq: Seq[String] = tablesByName.keys.toSeq.sorted

  /**
   * Roll the table with the given name
   * @param name name of the table to roll
   * @param f transform to apply to the rolled values
   * @return the transformed rolled values
   */
  def apply(name: String)(f: Seq[String] => String): Either[String, String] =
    tablesByName.get(name.toLowerCase) match
      case None =>
        Left(s"No table for $name")
      case Some(t) =>
        val a = f(t.roll)
        val b = Ev.eval(a)
        Right(b)
