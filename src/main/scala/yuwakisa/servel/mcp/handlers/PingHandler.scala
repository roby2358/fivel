package yuwakisa.servel.mcp.handlers

import yuwakisa.servel.mcp.McpMessageTypes.*

import scala.util.Try

class PingHandler extends MessageHandler:
  def canHandle(method: String): Boolean = method == "ping"
  
  def handle(request: JsonRpcRequest): Try[Option[JsonRpcMessage]] =
    Try {
      Some(JsonRpcResponse(
        result = Map.empty,
        id = request.id
      ))
    } 