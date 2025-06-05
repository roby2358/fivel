package yuwakisa.fivel.models.fibo

import munit.FunSuite

class FiboSpec extends FunSuite:

  val fibo: Array[Int] = Array.ofDim[Int](20)

  fibo(0) = 1
  fibo(1) = 1
  (0 until fibo.length - 2).foreach { i => fibo(i + 2) = fibo(i) + fibo(i + 1) }

  test("fibs1000") {
    (0 until fibo.length - 3).foreach { i =>
      if (fibo(i) + fibo(i + 1) + fibo(i + 2) < 3000)
        println(Seq(fibo(i), fibo(i + 1), fibo(i + 2)))
    }
  }

  test("phi1000") {
    val a = 194 * 3
    val total = a * (1 + 1.6 + 1.6 * 1.6)
    println(total)
    println(Seq(a, a * 1.6, a * 1.6 * 1.6))
  }

  test("split") {
    val count = 3000
    val sections = 6
    val scales = (0 until sections).map { a => math.pow(1.6, a) }
    println(scales)
    val base = count / scales.sum
    println(scales.sum * base)
    scales.foreach( a => println(math.floor(base * a)))
  }