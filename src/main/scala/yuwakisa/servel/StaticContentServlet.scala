package yuwakisa.servel

import jakarta.servlet.http.{HttpServlet, HttpServletRequest, HttpServletResponse}

import java.io.{InputStream, OutputStream}
import java.nio.file.{Files, Paths}

class StaticContentServlet extends HttpServlet :
  private val logger = this.getLogger

  override protected def doGet(request: HttpServletRequest, response: HttpServletResponse):Unit =
    val c = StaticContent(request.getRequestURI)

    Option(c.resourceStream).match
      case None => response.setStatus(HttpServletResponse.SC_NOT_FOUND)
      case Some(in) =>
        response.setStatus(HttpServletResponse.SC_OK)
        response.setContentType(c.mimeType)
        logger.debug(s"path=${c.resourcePath} type=${c.mimeType}")
        c.stream(response.getOutputStream)
