package yuwakisa.servel.mcp.handlers

import yuwakisa.servel.mcp.McpMessageTypes.*

import scala.util.Try

class InitializeHandler extends MessageHandler:
  def canHandle(method: String): Boolean = method == "initialize"
  
  def handle(request: JsonRpcRequest): Try[Option[JsonRpcMessage]] =
    Try {
      val params = request.params.getOrElse(Map.empty)
      val capabilities = params.get("capabilities").map(_.asInstanceOf[Map[String, Any]]).getOrElse(Map.empty)
      
      // TODO: Validate protocol version and other params
      
      Some(JsonRpcResponse(
        result = Map(
          "protocolVersion" -> "2024-11-05",
          "capabilities" -> Map(
            "roots" -> Map("listChanged" -> true),
            "resources" -> Map("listChanged" -> true, "subscribe" -> true),
            "tools" -> Map("listChanged" -> true),
            "prompts" -> Map("listChanged" -> true),
            "sampling" -> Map(),
            "logging" -> Map(),
            "experimental" -> Map()
          ),
          "serverInfo" -> Map(
            "name" -> "Servel MCP Server",
            "version" -> "0.1.0"
          )
        ),
        id = request.id
      ))
    } 