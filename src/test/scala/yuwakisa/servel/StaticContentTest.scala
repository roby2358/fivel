package yuwakisa.servel

import munit.FunSuite

import java.io.ByteArrayOutputStream
import java.nio.charset.StandardCharsets

class StaticContentTest extends FunSuite:
  test("read content from test file"):
    val content = new StaticContent("/test/info.txt")
    println(content.resourcePath)
    println(content.resourceStream)
    val out = new ByteArrayOutputStream()
    content.stream(out)
    val result = new String(out.toByteArray, StandardCharsets.UTF_8)
    // note: line separators must be LF!
    assertEquals(result, "banana\nboat\nbutton\n")

  test("handle directory paths by serving index.html"):
    val content = new StaticContent("/test/")
    assertEquals(content.resourcePath, "/public/test/index.html")

  test("detect correct js mime types"):
    val content = new StaticContent("/public/test/script.js")
    assertEquals(content.mimeType, "text/javascript")

  test("detect correct js mime types"):
    val content = new StaticContent("/public/test/script.js")
    assertEquals(content.mimeType, "text/javascript")

  test("detect correct js mime types"):
    val content = new StaticContent("/public/test/script.js")
    assertEquals(content.mimeType, "text/javascript")

  test("detect correct png mime types"):
    val content = new StaticContent("/public/test/image.png")
    assertEquals(content.mimeType, "image/png")

  test("detect correct standard mime types"):
    val txtContent = new StaticContent("/public/test/info.txt")
    assert(txtContent.mimeType != null)
    assertEquals(txtContent.mimeType, "text/plain")

  test("handle files without extensions"):
    val txtContent = new StaticContent("/public/test/other")
    assertEquals(txtContent.mimeType, "application/octet-stream")

  test("handle non-existent resources"):
    val content = new StaticContent("/nonexistent.txt")
    assertEquals(content.resourceStream, None) 