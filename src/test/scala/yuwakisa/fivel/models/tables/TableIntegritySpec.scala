package yuwakisa.fivel.models.tables

import munit.FunSuite

import scala.collection.mutable
import scala.util.Random

/**
 * Checks the shipped table resources for authoring mistakes that otherwise
 * only show up as "Nope" in generated text, or not at all:
 *  - a {reference} to a table that doesn't exist
 *  - the same table name defined twice (the last one loaded silently wins)
 *
 * Uses the real loader, parser and evaluator, so it sees exactly what the
 * generator sees.
 */
class TableIntegritySpec extends FunSuite:

  implicit val random: Random = Random(1234)

  case class Parsed(path: String, table: Table)

  /** Records the name inside each {...} instead of rolling it */
  class RefCollector extends Eval:
    val refs: mutable.ArrayBuffer[String] = mutable.ArrayBuffer[String]()

    def evaluate(s: String): String =
      refs.addOne(s)
      ""

    def rollDice(n: Int, sides: Int): Int = 0

  def refsIn(s: String): Seq[String] =
    val c = RefCollector()
    c.eval(s)
    c.refs.toSeq

  /** Every row string in a table, including nested rows */
  def strings(r: Roller): Seq[String] =
    r.rows.toSeq.flatMap { row => row.string +: strings(row) }

  lazy val parsed: Seq[Parsed] =
    Tables.Sources.flatMap { path => Tables.parseOne(path).map(Parsed(path, _)) }

  def report(title: String, lines: Seq[String]): String =
    lines.sorted.mkString(s"$title (${lines.size}):\n  ", "\n  ", "")

  test("table resources are found") {
    assert(parsed.nonEmpty, "No tables loaded from /tables")
  }

  test("every {reference} names a table that exists") {
    val known = parsed.map(_.table.name.toLowerCase).toSet
    val broken =
      for
        p <- parsed
        s <- strings(p.table)
        ref <- refsIn(s)
        if !known.contains(ref.toLowerCase)
      yield s"${p.path}: = ${p.table.name} -> {$ref}"

    assert(broken.isEmpty, report("Unresolved references", broken.distinct))
  }

  test("no table name is defined twice") {
    val dups = parsed
      .groupBy(_.table.name.toLowerCase)
      .collect { case (name, ps) if ps.size > 1 => s"$name: ${ps.map(_.path).mkString(", ")}" }
      .toSeq

    assert(dups.isEmpty, report("Duplicate table names", dups))
  }
