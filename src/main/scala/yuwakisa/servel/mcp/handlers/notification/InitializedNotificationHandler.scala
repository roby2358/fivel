package yuwakisa.servel.mcp.handlers.notification

import yuwakisa.servel.mcp.McpMessageTypes.*
import yuwakisa.servel.mcp.handlers.MessageHandler

import scala.util.Try

class InitializedNotificationHandler extends MessageHandler:
  def canHandle(method: String): Boolean = method == "notifications/initialized"
  
  def handle(request: JsonRpcRequest): Try[Option[JsonRpcMessage]] =
    Try {
      // This is a notification, so we return None to indicate no response should be sent
      None
    } 