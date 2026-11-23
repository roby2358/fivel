package yuwakisa.servel

import jakarta.servlet.http.HttpServlet
import jakarta.servlet.{Filter, Servlet}
import org.eclipse.jetty.ee10.servlet.{FilterHolder, ServletContextHandler, ServletHolder}
import org.eclipse.jetty.server.Server

object ServerRunner:
  val DefaultPort = 8000

class ServerRunner(
  port: Int, 
  routes: Map[String, Class[? <: Servlet]] = Map.empty,
  filters: Map[String, Class[? <: Filter]] = Map.empty
):
  private val logger = this.getLogger

  private def createContext(): ServletContextHandler =
    new ServletContextHandler(ServletContextHandler.SESSIONS)
  private def createServer(port: Int): Server = new Server(port)

  private lazy val server = createServer(port)

  /** Starts the server and blocks until Enter is pressed (or forever, if stdin is closed) */
  def start(): Unit =
    ctrlCHook()
    startServer()
    logger.info("Press Enter to stop the server")
    if scala.io.StdIn.readLine() == null then
      // No console (batch, container, nohup): run until the process is stopped
      server.join()
    else
      logger.info("Server shutdown requested")
      server.stop()
      logger.info("Server stopped")

  /** Starts the server and returns once it is accepting connections */
  def startServer(): Unit =
    // Create a ServletContextHandler with context path "/"
    val context = createContext()
    context.setContextPath("/")

    // Add CORS filter
    val corsFilter = new FilterHolder(classOf[CorsFilter])
    context.addFilter(corsFilter, "/*", null)

    // Add custom filters
    filters.foreach:
      case (path: String, filterClass: Class[? <: Filter]) =>
        val filterHolder = new FilterHolder(filterClass)
        context.addFilter(filterHolder, path, null)

    // Add servlets to the context
    routes.foreach:
      case (path: String, servletClass: Class[? <: Servlet]) =>
        context.addServlet(new ServletHolder(servletClass), path)

    // Set the context as the handler for the server
    server.setHandler(context)

    // Start the server
    server.start()
    logger.info(s"Server started on localhost:$port")

  def stop(): Unit =
    logger.debug("Forced stop")
    server.stop()

  private def ctrlCHook(): Unit =
    Runtime.getRuntime.addShutdownHook(new Thread {
      override def run(): Unit = {
        logger.debug("Shutdown hook triggered")
        try {
          if (server.isRunning) {
            logger.debug("Stopping server from shutdown hook")
            server.stop()
          }
        } catch {
          case e: Exception => logger.error("Error stopping server in shutdown hook", e)
        }
        Thread.sleep(500)
      }
    })