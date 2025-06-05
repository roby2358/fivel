package yuwakisa.servel.mcp

import yuwakisa.servel.mcp.McpMessageTypes.*
import yuwakisa.servel.mcp.handlers.MessageHandlerRegistry

import scala.util.{Failure, Success, Try}
import org.slf4j.LoggerFactory

class StdioServer extends AutoCloseable:
  private val logger = LoggerFactory.getLogger(getClass)
  private val transport = new StdioTransport()
  private var isRunning = false

  def start(): Unit =
    logger.info("Starting StdioServer")
    isRunning = true
    while isRunning do {
      transport.readMessage() match
        case Success(request) =>
          handleRequest(request)
        case Failure(e) =>
          e match
            case _: IllegalStateException if e.getMessage == "End of input stream" =>
              logger.info("End of input stream detected, stopping server")
              isRunning = false
            case _ =>
              logger.error("Failed to read message", e)
              transport.writeError(s"Failed to read message: ${e.getMessage}")
      // Continue running despite the error
      logger.info("FIN")
    }

  private def handleRequest(request: JsonRpcRequest): Unit =
    logger.debug(s"Handling request: ${request.method}")
    MessageHandlerRegistry.getHandler(request.method) match
      case Some(handler) =>
        handler.handle(request) match
          case Success(Some(response)) =>
            transport.writeMessage(response) match
              case Success(_) => 
                logger.debug(s"Successfully sent response for method: ${request.method}")
              case Failure(e) =>
                logger.error(s"Failed to send response for method: ${request.method}", e)
                transport.writeError(s"Failed to send response: ${e.getMessage}")
          case Success(None) =>
            logger.debug(s"Processed notification: ${request.method}")
            ()
          case Failure(e) =>
            logger.error(s"Error handling request: ${request.method}", e)
            val errorResponse = JsonRpcErrorResponse(
              error = JsonRpcError(
                code = -32603,
                message = s"Internal error: ${e.getMessage}"
              ),
              id = request.id
            )
            transport.writeMessage(errorResponse) match
              case Success(_) => 
                logger.debug("Successfully sent error response")
              case Failure(e) =>
                logger.error("Failed to send error response", e)
                transport.writeError(s"Failed to send error response: ${e.getMessage}")
      case None =>
        logger.warn(s"Method not found: ${request.method}")
        val errorResponse = JsonRpcErrorResponse(
          error = JsonRpcError(
            code = -32601,
            message = s"Method not found: ${request.method}"
          ),
          id = request.id
        )
        transport.writeMessage(errorResponse) match
          case Success(_) => 
            logger.debug("Successfully sent method not found error")
          case Failure(e) =>
            logger.error("Failed to send method not found error", e)
            transport.writeError(s"Failed to send error response: ${e.getMessage}")

  def stop(): Unit =
    logger.info("Stopping StdioServer")
    isRunning = false

  override def close(): Unit =
    logger.info("Closing StdioServer")
    stop()
    transport.close() 