package yuwakisa.servel.mcp

import jakarta.servlet.http.{HttpServlet, HttpServletRequest, HttpServletResponse}
import yuwakisa.servel.Content

class HealthServlet extends HttpServlet:
  override protected def doGet(request: HttpServletRequest, response: HttpServletResponse): Unit =
    val jsonRpcResponse = Map(
      "status" -> "ok",
    )
    Content.okJson(response, jsonRpcResponse)