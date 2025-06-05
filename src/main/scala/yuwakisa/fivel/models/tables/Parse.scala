package yuwakisa.fivel.models.tables

import yuwakisa.fivel.Logging

import java.io.{BufferedReader, InputStreamReader, Reader}
import scala.collection.mutable
import scala.io.Source
import scala.util.Random

object Parse:
  enum State:
    case StartLine, StartTable, Indented, Counted

case class Parse(r: Reader)(implicit val random: Random = Random()) extends Logging :

  import Parse.State.*
  import Parse.*

  val lex: Lex = Lex(r)
  var state: State = StartLine
  val tables: mutable.ArrayBuffer[Table] = mutable.ArrayBuffer[Table]()

  private var indent: Option[Int] = None
  private var count: Option[Int] = None
  private var string: Option[String] = None
  private var stack = mutable.ArrayBuffer[Row]()
  private var current: Option[Row] = None

  def mkString: String =
    tables.map(_.mkString(0)).mkString("")

  def clear: State =
    lex.string
    StartLine

  def parseIndent: State =
    indent = lex.indent
    if indent.isEmpty then
      logger.warn(s"Expected indent ${lex.position}")
      clear
    else
      Indented

  def parseCount: State =
    count = lex.number
    if count.isEmpty then
      // treat as 1 if no count
      count = Some(1)

    Counted

  def parseTable: State =
    string = lex.string
    if string.isEmpty then
      logger.warn(s"Expected table name ${lex.position}")
      clear
    else
      val table = Table(string.get)
      tables.addOne(table)
      stack = mutable.ArrayBuffer()

      StartLine

  def parseRow: State =
    string = lex.string
    if string.isEmpty then
      logger.warn(s"Expected row value ${lex.position}")
      clear
    else if tables.isEmpty then
      logger.warn(s"No table defined at ${lex.position}")
      clear
    else
      val current = Row(indent.get, count.get, string.get)

      // pop the stack until we're at the right level
      while
        stack.nonEmpty && current.indent <= stack.last.indent
      do
        stack.remove(stack.length - 1)

      if stack.isEmpty then
        tables.last.rows.addOne(current)
        stack.addOne(current)
      else
        stack.last.rows.addOne(current)
        stack.addOne(current)

      StartLine

  def go: Parse =
    while !lex.done do
      state = state match
        case StartLine if lex.comment => StartLine
        case StartLine if lex.tableStart => StartTable
        case StartLine => parseIndent
        case StartTable => parseTable
        case Indented if lex.comment => StartLine
        case Indented if indent.get == 0 => parseTable
        case Indented => parseCount
        case Counted => parseRow
    this
