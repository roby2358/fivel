package yuwakisa.servel.mcp.handlers.tools

import yuwakisa.fivel.McpRegistry
import yuwakisa.servel.mcp.McpMessageTypes.*
import yuwakisa.servel.mcp.handlers.MessageHandler

import scala.util.Try

class ToolsListHandler(using tools: List[Tool] = McpRegistry.tools) extends MessageHandler:
  def canHandle(method: String): Boolean = method == "tools/list"
  
  private def listTools(cursor: Option[String]): (List[Tool], Option[String]) =
    // Simple pagination - just return all tools for now
    (tools, None)
  
  def handle(request: JsonRpcRequest): Try[Option[JsonRpcMessage]] =
    Try {
      val cursor = request.params.flatMap(_.get("cursor").map(_.toString))
      val (toolsList, nextCursor) = listTools(cursor)
      
      val toolsMetadata = toolsList.map(tool => Map(
        "name" -> tool.name,
        "description" -> tool.description,
        "inputSchema" -> tool.inputSchema,
        "annotations" -> tool.annotations
      ))
      
      val result = Map("tools" -> toolsMetadata) ++ 
        nextCursor.map(cursor => Map("nextCursor" -> cursor)).getOrElse(Map.empty)
      
      Some(JsonRpcResponse(
        result = result,
        id = request.id
      ))
    } 