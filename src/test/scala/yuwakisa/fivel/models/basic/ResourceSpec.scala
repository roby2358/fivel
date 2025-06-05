package yuwakisa.fivel.models.basic

import munit.FunSuite
import yuwakisa.fivel.models.basic.Resource

class ResourceSpec extends FunSuite:

  test("directoryReader"):
    val actual = Resource("/testtables").reader.get.readLine
    val expected = "info.txt"
    assertEquals(actual, expected)

  test("readerFile"):
    val actual = Resource("/testtables/info.txt").reader.get.readLine
    val expected = "= Info"
    assertEquals(actual, expected)

  test("directoryToSeq"):
    val actual = Resource("/testtables").toSeq
    val expected = Seq("info.txt")
    assertEquals(actual, expected)

  test("content"):
    val r = Resource("/testtables/info.txt")
    val actual = r.reader.map(r.readAll)
    val expected = Some(List("= Info", "  1 Facts", "  1 History", "  1 Math", "  1 Stories", "  1 Banannas"))
    assertEquals(actual, expected)

  test("paths"):
    val actual = Resource("/resources").paths
    val expected = Seq("/resources/a.txt", "/resources/b.txt", "/resources/c.txt")
    assertEquals(actual, expected)

  test("pathsTrailingSlash"):
    val actual = Resource("/resources/").paths
    val expected = Seq("/resources/a.txt", "/resources/b.txt", "/resources/c.txt")
    assertEquals(actual, expected)
