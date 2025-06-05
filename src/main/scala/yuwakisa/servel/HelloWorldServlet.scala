package yuwakisa.servel

import jakarta.servlet.http.{HttpServlet, HttpServletRequest, HttpServletResponse}
import yuwakisa.fivel.Logging

class HelloWorldServlet extends HttpServlet, Logging :

  override protected def doGet(request: HttpServletRequest, response: HttpServletResponse):Unit =
    logger.debug("Hello world servlet")
    Content.okText(response, "Hello world!")
