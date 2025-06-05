package yuwakisa.fivel

import jakarta.servlet.http.{HttpServlet, HttpServletRequest, HttpServletResponse}
import yuwakisa.fivel.models.basic.Resource
import yuwakisa.fivel.models.words.Words
import yuwakisa.servel.Content

import scala.util.matching.Regex

object WordsServlet :
  lazy val WordSources: Seq[String] = Resource("/words").paths
  lazy val AllWords: Words = Words.load(WordSources)
  lazy val AllWordsNames: Seq[String] = AllWords.toSeq

class WordsServlet extends HttpServlet :

  import WordsServlet.*

  val rollRegex: Regex = """/roll/(.*)""".r

  override protected def doGet(request: HttpServletRequest, response: HttpServletResponse):Unit =
    Option(request.getPathInfo) match
      case None =>
        Content.okJson(response, Map("words" -> AllWordsNames))
      case _ =>
        val name = rollRegex.findFirstMatchIn(request.getPathInfo).map(_.group(1))
        val m = AllWords.uniq(name.get) match
          case Left(error) =>
            Map("rolled" -> error)
          case Right(string) =>
            Map("rolled" -> string)
        Content.okJson(response, m)
