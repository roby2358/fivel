package yuwakisa.servel.mcp

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.scala.DefaultScalaModule
import jakarta.servlet.http.{HttpServlet, HttpServletRequest, HttpServletResponse}
import jakarta.servlet.{ServletException, ServletOutputStream}
import yuwakisa.servel.mcp.McpMessageTypes.*
import yuwakisa.servel.mcp.handlers.MessageHandlerRegistry

import java.io.IOException
import java.util.concurrent.{ConcurrentHashMap, ConcurrentLinkedQueue}
import scala.jdk.CollectionConverters.*
import scala.util.{Failure, Success, Try}

object McpServlet:
  private val sessions = new ConcurrentHashMap[String, Session]()
  private val objectMapper = new ObjectMapper().registerModule(DefaultScalaModule)
  
  private class Session:
    val eventQueue = new ConcurrentLinkedQueue[String]()
    var lastEventId = 0L
    var hasActiveStream = false
    
  private def sendEvent(out: ServletOutputStream, eventId: Long, data: String): Unit =
    out.write(s"id: $eventId\n".getBytes("UTF-8"))
    out.write(s"data: $data\n\n".getBytes("UTF-8"))
    out.flush()

class McpServlet extends HttpServlet:
  override def doPost(req: HttpServletRequest, resp: HttpServletResponse): Unit =
    val sessionId = Option(req.getHeader("Mcp-Session-Id")).getOrElse("1")
    
    // Get or create session
    val session = McpServlet.sessions.computeIfAbsent(
      sessionId,
      _ => new McpServlet.Session()
    )
    
    // Read and parse JSON-RPC message
    val jsonRpc = Try:
      val reader = req.getReader()
      McpServlet.objectMapper.readValue(reader, classOf[JsonRpcRequest])
    
    jsonRpc match
      case Success(request) =>
        // Check if it's a batch request
        if request.method == null then
          // Handle batch request
          resp.setStatus(HttpServletResponse.SC_OK)
          resp.setContentType("application/json")
          // TODO: Process batch requests
        else
          // Get appropriate handler
          MessageHandlerRegistry.getHandler(request.method) match
            case Some(handler) =>
              // Check if we need to stream the response
              val needsStreaming = request.method.startsWith("tools/") || request.method.startsWith("resources/")
              
              if needsStreaming then
                // Get or create session
                val session = McpServlet.sessions.computeIfAbsent(
                  sessionId,
                  _ => new McpServlet.Session()
                )
                
                // Check for existing stream only if we have a session ID
                if sessionId != "1" && session.hasActiveStream then
                  resp.setStatus(HttpServletResponse.SC_CONFLICT)
                  return
                
                session.hasActiveStream = true
                
                resp.setStatus(HttpServletResponse.SC_OK)
                resp.setContentType("text/event-stream")
                resp.setHeader("Cache-Control", "no-cache")
                resp.setHeader("Connection", "keep-alive")
                
                val out = resp.getOutputStream
                
                try
                  // Handle the request
                  handler.handle(request) match
                    case Success(response) =>
                      // Set session ID header for initialize method
                      if request.method == "initialize" then
                        resp.setHeader("Mcp-Session-Id", sessionId)
                      
                      // Send initial response
                      McpServlet.sendEvent(out, session.lastEventId, McpServlet.objectMapper.writeValueAsString(response))
                      session.lastEventId += 1
                      
                      // For long-running operations, send progress updates
                      if request.method.startsWith("tools/") || request.method.startsWith("resources/") then
                        // TODO: Implement progress tracking and streaming
                        for i <- 1 to 5 do
                          Thread.sleep(1000) // Simulate work
                          val progressJson = McpServlet.objectMapper.createObjectNode()
                            .put("jsonrpc", "2.0")
                            .put("method", "progress")
                            .put("params", s"Processing step $i of 5")
                          
                          McpServlet.sendEvent(out, session.lastEventId, progressJson.toString)
                          session.lastEventId += 1
                    case Failure(e) =>
                      val errorResponse = JsonRpcErrorResponse(
                        error = JsonRpcError(
                          code = -32603,
                          message = s"Internal error: ${e.getMessage}"
                        ),
                        id = request.id
                      )
                      McpServlet.sendEvent(out, session.lastEventId, McpServlet.objectMapper.writeValueAsString(errorResponse))
                      session.lastEventId += 1
                finally
                  session.hasActiveStream = false
              else
                // Handle immediate response
                resp.setStatus(HttpServletResponse.SC_OK)
                resp.setContentType("application/json")
                
                handler.handle(request) match
                  case Success(response) =>
                    // Set session ID header for initialize method
                    if request.method == "initialize" then
                      resp.setHeader("Mcp-Session-Id", sessionId)
                    
                    resp.getWriter.write(McpServlet.objectMapper.writeValueAsString(response))
                  case Failure(e) =>
                    val errorResponse = JsonRpcErrorResponse(
                      error = JsonRpcError(
                        code = -32603,
                        message = s"Internal error: ${e.getMessage}"
                      ),
                      id = request.id
                    )
                    resp.getWriter.write(McpServlet.objectMapper.writeValueAsString(errorResponse))
            case None =>
              // Unknown method
              resp.setStatus(HttpServletResponse.SC_OK)
              resp.setContentType("application/json")
              val errorResponse = JsonRpcErrorResponse(
                error = JsonRpcError(
                  code = -32601,
                  message = s"Method not found: ${request.method}"
                ),
                id = request.id
              )
              resp.getWriter.write(McpServlet.objectMapper.writeValueAsString(errorResponse))
      
      case Failure(e) =>
        resp.setStatus(HttpServletResponse.SC_BAD_REQUEST)
        resp.getWriter.write(s"Invalid JSON-RPC message: ${e.getMessage}")
  
  override def doGet(req: HttpServletRequest, resp: HttpServletResponse): Unit =
    val sessionId = Option(req.getHeader("Mcp-Session-Id")).getOrElse("1")
    
    // Get or create session
    val session = McpServlet.sessions.computeIfAbsent(
      sessionId,
      _ => new McpServlet.Session()
    )
    
    // Check for existing stream
    if session.hasActiveStream then
      resp.setStatus(HttpServletResponse.SC_CONFLICT)
      return
    
    session.hasActiveStream = true
    
    // Set up SSE
    resp.setStatus(HttpServletResponse.SC_OK)
    resp.setContentType("text/event-stream")
    resp.setHeader("Cache-Control", "no-cache")
    resp.setHeader("Connection", "keep-alive")
    
    val lastEventId = Option(req.getHeader("Last-Event-ID")).map(_.toLong)
    val out = resp.getOutputStream
    
    try
      // Send a heartbeat to keep the connection alive
      val heartbeatJson = McpServlet.objectMapper.createObjectNode()
        .put("jsonrpc", "2.0")
        .put("method", "heartbeat")
        .put("params", "connection established")
      
      McpServlet.sendEvent(out, session.lastEventId, heartbeatJson.toString)
      session.lastEventId += 1
      
      // Keep the connection open and send periodic heartbeats
      while !Thread.currentThread.isInterrupted do
        try
          Thread.sleep(30000) // Send heartbeat every 30 seconds
          val heartbeat = McpServlet.objectMapper.createObjectNode()
            .put("jsonrpc", "2.0")
            .put("method", "heartbeat")
            .put("params", "still alive")
          
          McpServlet.sendEvent(out, session.lastEventId, heartbeat.toString)
          session.lastEventId += 1
        catch
          case _: InterruptedException =>
            Thread.currentThread.interrupt()
            return
          case e: IOException =>
            // Client disconnected
            return
    finally
      session.hasActiveStream = false
  
  override def doDelete(req: HttpServletRequest, resp: HttpServletResponse): Unit =
    val sessionId = Option(req.getHeader("Mcp-Session-Id")).getOrElse("1")
    McpServlet.sessions.remove(sessionId)
    resp.setStatus(HttpServletResponse.SC_OK) 