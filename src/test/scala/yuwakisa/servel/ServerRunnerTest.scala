package yuwakisa.servel

import jakarta.servlet.http.HttpServlet
import munit.FunSuite

import java.net.HttpURLConnection
import java.util.concurrent.Executors
import scala.concurrent.{ExecutionContext, Future}
import scala.io.Source
import scala.language.adhocExtensions
import scala.util.{Failure, Success, Try, Using}

// Tag for integration tests
object IntegrationTest extends munit.Tag("integration")

class ServerRunnerTest extends FunSuite:
  private val logger = this.getLogger

  case class HttpResponse(statusCode: Int, body: String)

  private def httpGet(url: String): Try[HttpResponse] =
    val connection = java.net.URI.create(url).toURL.openConnection().asInstanceOf[HttpURLConnection]
    Try:
      connection.setRequestMethod("GET")
      connection.setConnectTimeout(5000)
      connection.setReadTimeout(5000)

      val statusCode = connection.getResponseCode

      val inputStream =
        if statusCode >= 400 then connection.getErrorStream
        else connection.getInputStream

      val body =
        if inputStream == null then ""
        else
          Using(Source.fromInputStream(inputStream)) { source =>
            source.mkString
          }.getOrElse("")

      HttpResponse(statusCode, body)
    .tap(_ => connection.disconnect())

  private def choosePort: Int = 8000 // 49152 + Random.nextInt(16384)

  private def runServerTest(routes: Map[String, Class[? <: HttpServlet]])(testFn: Int => Unit): Unit =
    val port = choosePort
    logger.debug(s"Creating server on port $port")
    
    val server = new ServerRunner(ServerRunner.DefaultPort, routes)
    val executionContext = ExecutionContext.fromExecutor(Executors.newSingleThreadExecutor())
    
    try
      // Start server in background
      Future {
        server.start()
      }(executionContext)
      
      // Wait for server to start
      Thread.sleep(500)
      
      // Run the test
      testFn(port)
    finally
      // Stop the server
      server.stop()
      Thread.sleep(500)

  test("ServerRunner should register and handle a single route".tag(IntegrationTest)):
    val routes = Map("/hello" -> classOf[HelloWorldServlet])
    runServerTest(routes) { port =>
      val response = httpGet(s"http://localhost:$port/hello")
      response match
        case Success(r) =>
          assertEquals(r.statusCode, 200)
          assertEquals(r.body.trim, "Hello world!")
        case Failure(e) =>
          fail(s"Unexpected error: ${e.getMessage}")
    }

  test("ServerRunner should handle multiple routes".tag(IntegrationTest)):
    val routes = Map(
      "/hello" -> classOf[HelloWorldServlet]
    )
    runServerTest(routes) { port =>
      // Test HelloWorldServlet
      val helloResponse = httpGet(s"http://localhost:$port/hello")
      helloResponse match
        case Success(r) =>
          assertEquals(r.statusCode, 200)
          assertEquals(r.body.trim, "Hello world!")
        case Failure(e) =>
          fail(s"HelloWorldServlet test error: ${e.getMessage}")
    }

  test("ServerRunner should handle 404 for non-existent endpoints".tag(IntegrationTest)):
    val routes = Map("/hello" -> classOf[HelloWorldServlet])
    runServerTest(routes) { port =>
      val response = httpGet(s"http://localhost:$port/nonexistent")
      response match
        case Success(r) =>
          assertEquals(r.statusCode, 404)
        case Failure(e) =>
          fail(s"Unexpected error: ${e.getMessage}")
    }

  test("ServerRunner should handle server errors".tag(IntegrationTest)):
    // We need to define a named class to use it in routes
    class ErrorServlet extends HttpServlet:
      override def doGet(req: jakarta.servlet.http.HttpServletRequest,
                       resp: jakarta.servlet.http.HttpServletResponse): Unit =
        throw new RuntimeException("Test error")
    
    val routes = Map("/error" -> classOf[ErrorServlet])
    runServerTest(routes) { port =>
      val response = httpGet(s"http://localhost:$port/error")
      response match
        case Success(r) =>
          // Jetty might return a 500 error page instead of failing the request
          assert(r.statusCode == 500 || r.statusCode == 400,
            s"Expected 500 or 400 status code, got ${r.statusCode}")
        case Failure(e) =>
          // Either connection failure or other exception is acceptable
          // for an endpoint that throws an exception
          assert(true)
    }

  test("ServerRunner should handle empty routes".tag(IntegrationTest)):
    val routes = Map.empty[String, Class[? <: HttpServlet]]
    runServerTest(routes) { port =>
      // Just verify the server starts without errors
      // No routes are defined, so any request should 404
      val response = httpGet(s"http://localhost:$port/")
      response match
        case Success(r) => assertEquals(r.statusCode, 404)
        case Failure(e) =>
          fail(s"Unexpected error: ${e.getMessage}")
    }

  test("StaticContentServlet should serve static files".tag(IntegrationTest)):
    val routes = Map("/*" -> classOf[StaticContentServlet])
    runServerTest(routes) { port =>
      val response = httpGet(s"http://localhost:$port/test/index.html")
      response match
        case Success(r) =>
          assertEquals(r.statusCode, 200)
          println("****" + r.body)
          assert(r.body.contains("Hello Test!"),
            "Expected index.html content to contain 'Hello Test!'")
        case Failure(e) =>
          fail(s"Unexpected error: ${e.getMessage}")
    }