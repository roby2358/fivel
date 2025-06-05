package yuwakisa.fivel.models.tables

import yuwakisa.fivel.models.tables.Evalinator

trait TestEvalinator extends Evalinator :

  def evaluate(s: String): String = s.toUpperCase

  def rollDice(n: Int, sides: Int): Int = n * 1000 + sides