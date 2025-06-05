package yuwakisa.servel

import jakarta.servlet.http.HttpServletRequest
import org.apache.commons.io.IOUtils

import java.io.{InputStream, OutputStream}
import java.nio.file.{Files, Paths}
import scala.io.{Codec, Source}

class StaticContent(uri: String) :

  val PublicPath = "/public"
  val IndexHtml = "index.html"

  val resourcePath: String =
    PublicPath + (if uri.endsWith("/") then uri + IndexHtml else uri)

  val resourceStream: Option[InputStream] =
    Option(this.getClass.getResourceAsStream(resourcePath))

  /**
   * The mime type of the content. Augments the native java mime type detector.
   */
  val mimeType: String =
    val at = resourcePath.lastIndexOf(".")
    val extension = if at > 0 then resourcePath.substring(at + 1) else resourcePath
    extension match
      case "js" => "text/javascript"
      case "png" => "image/png"
      case _ => Option(Files.probeContentType(Paths.get(resourcePath))).getOrElse("application/octet-stream")

  /**
   * Note: Won't do anything if resourceStream is not found
   * @param out the OutputStream to write to
   */
  def stream(out: OutputStream): Unit =
    resourceStream.foreach({ in => IOUtils.copy(in, out) })
