package yuwakisa.servel

import munit.FunSuite

class PathRegexSpec extends FunSuite:
  private val pathRegex = """(/zing/[^/]*)?(/zot/[^/]*)?(/zot/.*)?""".r

  test("shouldMatchEmptyString"):
    val m = pathRegex.findFirstMatchIn("")
    assert(m.isDefined)
    assertEquals(m.get.group(0), "")
    assert(m.get.group(1) == null)
    assert(m.get.group(2) == null)
    assert(m.get.group(3) == null)

  test("shouldMatchZingPath"):
    val m = pathRegex.findFirstMatchIn("/zing/woo")
    assert(m.isDefined)
    assertEquals(m.get.group(0), "/zing/woo")
    assertEquals(m.get.group(1), "/zing/woo")
    assert(m.get.group(2) == null)
    assert(m.get.group(3) == null)

  test("shouldMatchZotPath"):
    val m = pathRegex.findFirstMatchIn("/zot/bar")
    assert(m.isDefined)
    assertEquals(m.get.group(0), "/zot/bar")
    assert(m.get.group(1) == null)
    assertEquals(m.get.group(2), "/zot/bar")
    assert(m.get.group(3) == null)

  test("shouldMatchBothPaths"):
    val input = "/zing/woo/zot/bar"
    val m = pathRegex.findFirstMatchIn(input)
    assert(m.isDefined)
    println(s"Input: $input")
    println(s"Group 0: ${m.get.group(0)}")
    println(s"Group 1: ${m.get.group(1)}")
    println(s"Group 2: ${m.get.group(2)}")
    println(s"Group 3: ${m.get.group(3)}")
    assertEquals(m.get.group(0), "/zing/woo/zot/bar")
    assertEquals(m.get.group(1), "/zing/woo")
    assertEquals(m.get.group(2), "/zot/bar")
    assertEquals(m.get.group(3), null)