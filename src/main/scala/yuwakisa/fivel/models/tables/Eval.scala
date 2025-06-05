package yuwakisa.fivel.models.tables

object Eval :
  enum State:
    case Plain, Evaluated, DiceNum, DiceSides

  val EvaluatedStart = '{'
  val EvaluatedEnd = '}'
  val DiceStart = '['
  val LowerDice = 'd'
  val UpperDice = 'D'
  val DiceEnd = ']'

abstract class Eval extends Evalinator :

  import Eval.*
  import Eval.State.*

  def clear(): StringBuffer = StringBuffer()

  def addDigit(n: Int, c: Char): Int = n * 10 + c.toInt - '0'.toInt

  def eval(s : String): String =
    var state = Plain
    var i = 0
    var a = clear()
    var aa = clear()
    var diceNum = 0
    var diceSides = 0

    while i < s.length do
      state = (state, s(i)) match

        case (Plain, EvaluatedStart) =>
          aa = clear()
          Evaluated

        case (Evaluated, EvaluatedEnd) =>
          a.append(evaluate(aa.toString))
          aa = clear()
          Plain

        case (Plain, DiceStart) =>
          diceNum = 0
          diceSides = 0
          DiceNum

        case (DiceNum, LowerDice) | (DiceNum, UpperDice) =>
          DiceSides

        case (Plain, DiceEnd) | (DiceNum, DiceEnd) | (DiceSides, DiceEnd) =>
          a.append(rollDice(
            if diceNum > 0 then diceNum else 1,
            if diceSides > 0 then diceSides else 6))
          Plain

        case (Plain, c) =>
          a.append(c)
          Plain

        case (Evaluated, c) =>
          aa.append(c)
          Evaluated

        case (DiceNum, c) =>
          diceNum = addDigit(diceNum, c)
          DiceNum

        case (DiceSides, c) =>
          diceSides = addDigit(diceSides, c)
          DiceSides

      i += 1

    a.append(aa.toString)
    a.toString
