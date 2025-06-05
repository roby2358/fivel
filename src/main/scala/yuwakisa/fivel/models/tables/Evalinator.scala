package yuwakisa.fivel.models.tables

trait Evalinator :

  def evaluate(s: String): String

  def rollDice(n: Int, sides: Int): Int
