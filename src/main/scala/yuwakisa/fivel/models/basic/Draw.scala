package yuwakisa.fivel.models.basic

import scala.util.Random

trait Draw:
  def draw: Int

object Draw:
  class RandomDraw(n: Int)(implicit val random: Random = Random()) extends Draw:
    def draw: Int = random.nextInt(n)
