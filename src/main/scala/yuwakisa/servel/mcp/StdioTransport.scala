package yuwakisa.servel.mcp

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.scala.DefaultScalaModule
import yuwakisa.servel.mcp.McpMessageTypes.*

import java.io.{BufferedReader, IOException, InputStreamReader, PrintWriter}
import scala.util.{Failure, Success, Try}

class StdioTransport extends AutoCloseable:
  private val objectMapper = new ObjectMapper().registerModule(DefaultScalaModule)
  private val reader = new BufferedReader(new InputStreamReader(System.in))
  private val writer = new PrintWriter(System.out, true)
  private val errorWriter = new PrintWriter(System.err, true)

  private def trace(message: String): Unit =
    errorWriter.println(s"[TRACE] $message")
    errorWriter.flush()

  def readMessage(): Try[JsonRpcRequest] =
    Try:
      trace("Reading message")
      val line = reader.readLine()
      trace(s"Read line from stdin: $line")
      if line == null then
        trace("Received null line - end of input stream")
        throw new IllegalStateException("End of input stream")
      objectMapper.readValue(line, classOf[JsonRpcRequest])

  def writeMessage(message: JsonRpcMessage): Try[Unit] =
    Try:
      trace("Writing message")
      val json = objectMapper.writeValueAsString(message)
      trace(s"Writing message to stdout: $json")
      writer.println(json)
      writer.flush()
    .recoverWith { case e: IOException =>
      trace(s"Failed to write message: ${e.getMessage}")
      Failure(new IOException(s"Failed to write message: ${e.getMessage}", e))
    }

  def writeError(message: String): Try[Unit] =
    Try:
      trace(s"Writing error: $message")
      errorWriter.println(s"Error: $message")
      errorWriter.flush()
    .recoverWith { case e: IOException =>
      trace(s"Failed to write error: ${e.getMessage}")
      Failure(new IOException(s"Failed to write error: ${e.getMessage}", e))
    }

  override def close(): Unit =
    try
      reader.close()
      writer.close()
      errorWriter.close()
    catch
      case e: IOException =>
        // Log error but don't throw since this is called in finally block
        System.err.println(s"Error closing transport: ${e.getMessage}") 