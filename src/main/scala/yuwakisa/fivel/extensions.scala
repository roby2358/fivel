package yuwakisa.fivel

/**
 * Extension method for tap
 */
extension[A] (a: A)
  def tap(f: A => Unit): A =
    f(a)
    a