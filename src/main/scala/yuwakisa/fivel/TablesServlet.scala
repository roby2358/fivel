package yuwakisa.fivel

import jakarta.servlet.http.{HttpServlet, HttpServletRequest, HttpServletResponse}
import yuwakisa.fivel.models.basic.Resource
import yuwakisa.fivel.models.tables.Tables
import yuwakisa.servel.Content

import scala.util.matching.Regex

class TablesServlet extends HttpServlet :
  private val logger = this.getLogger

  val rollRegex: Regex = """/roll/(.*)""".r

  override protected def doGet(request: HttpServletRequest, response: HttpServletResponse):Unit =
    logger.info(s"GET tables ${request.getRequestURI} ${request.getPathInfo}")
    Option(request.getPathInfo) match
      case None =>
        Content.okJson(response, Map("tables" -> Tables.AllNames))
      case _ =>
        val name = rollRegex.findFirstMatchIn(request.getPathInfo).map(_.group(1)).headOption
        val result: Map[String, String] = Tables.All(name.get)(Tables.mkString) match
          case Left(error) =>
            Map("error" -> error)
          case Right(string) =>
            Map("rolled" -> string)
        Content.okJson(response, result)
