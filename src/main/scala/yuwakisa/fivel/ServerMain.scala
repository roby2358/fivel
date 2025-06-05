package yuwakisa.fivel

import yuwakisa.servel.mcp.{ConfigServlet, HealthServlet, McpServlet}
import yuwakisa.servel.{HelloWorldServlet, ServerRunner, StaticContentServlet}

object ServerMain:
  def main(args: Array[String]): Unit =
    val routes = Map( "/" -> classOf[StaticContentServlet],
      "/hello" -> classOf[HelloWorldServlet],
      "/tables/*" -> classOf[TablesServlet],
      "/words/*" -> classOf[WordsServlet],
      "/health" -> classOf[HealthServlet],
      "/config" -> classOf[ConfigServlet],
      "/mcp" -> classOf[McpServlet]
    )
  
    val runner = new ServerRunner(routes=routes, port=8000)
    runner.start()
