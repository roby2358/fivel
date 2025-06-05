package yuwakisa.fivel

import yuwakisa.servel.mcp.{ConfigServlet, HealthServlet, McpServlet, StdioServer}
import yuwakisa.servel.{HelloWorldServlet, ServerRunner, StaticContentServlet}

object Main:
  def main(args: Array[String]): Unit =
    try
      new StdioServer().start()
    catch
      case e: Exception =>
        System.err.println(s"Fatal error: ${e.getMessage}")
        e.printStackTrace()
        System.exit(1)
