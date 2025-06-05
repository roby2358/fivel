package yuwakisa.fivel.models.tables

import yuwakisa.fivel.Logging

import java.io.Reader
import scala.annotation.tailrec
import scala.collection.mutable

object Lex :
  val Ascii: Set[Int] = (0 to 127).toSet
  val CommentStart: Char = '#'
  val CR: Char = '\r'
  val CRLF: String = "\r\n"
  val Digits: Set[Char] = ('0' to '9').toSet
  val Indent: String = " -"
  val InlineWhitespace: String = " \t"
  val LF: Char = '\n'
  val SomeEmpty: Option[String] = Some("")
  val TableStart: Char = '='

case class Lex(r: Reader) extends Logging :
  import Lex.*

  // for diagnostic output
  def position = "Here"

  def tableStart: Boolean =
    r.mark(1)
    (r.read.toChar == TableStart) match
      case false =>
        r.reset
        false
      case true =>
        true

  /**
   * @return the indent count
    */
  def indent: Option[Int] =
    var n = 0
    r.mark(1)
    while Indent.contains(r.read.toChar) do
      r.mark(1)
      n += 1

    r.reset

    Some(n)

  /**
   * @return an optional number
   */
  def number: Option[Int] =
    parseNumber(None).map(_.toInt)

  @tailrec
  private def parseNumber(a: Option[String]): Option[String] =
    r.mark(1)
    r.read.toChar match
      case c if Digits.contains(c) =>
        parseNumber(a.orElse(SomeEmpty).map(aa => s"$aa$c"))
      case _ =>
        r.reset
        a

  /**
   * @return the string to end of line
   */
  def string: Option[String] =
    optionalWhitespace
    parseString(None)

  @tailrec
  private def parseString(a: Option[String]): Option[String] =
    r.read.toChar match
      case c if c == LF || c == 65535 =>
        a
      case c if c == CR =>
        parseString(a)
      case c if !Ascii.contains(c) =>
        logger.warn(s"Non-ascii $c ${c.toInt}")
        a
      case c =>
        parseString(a.orElse(SomeEmpty).map(aa => s"$aa$c"))

  def optionalBlanks: Boolean =
    while
      r.mark(1)
      CRLF.contains(r.read.toChar)
    do ()

    r.reset
    true

  def optionalWhitespace: Boolean =
    while
      r.mark(1)
      InlineWhitespace.contains(r.read.toChar)
    do ()

    r.reset
    true

  /**
   * @return true if the line is a comment (to end of line)
   */
  def comment: Boolean =
    optionalBlanks
    r.mark(1)

    if CommentStart != r.read then
      r.reset
      false
    else
      while
        var c = r.read.toChar
        Ascii.contains(c) && LF != c
      do ()
      true

  /**
   * @return true when the buffer is done
   */
  def done: Boolean =
    r.mark(1)
    val c = r.read.toChar
    if c.toInt == 65535 then
      true
    else if Ascii.contains(c) then
      r.reset()
      false
    else
      logger.warn(s"Non-ASCII character encountered: $c ${c.toInt}")
      true
