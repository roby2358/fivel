package yuwakisa.servel.mcp.handlers.notification

import yuwakisa.servel.mcp.McpMessageTypes.*
import yuwakisa.servel.mcp.handlers.MessageHandler

import scala.util.Try

class CancelledNotificationHandler extends MessageHandler:
  def canHandle(method: String): Boolean = method == "notifications/cancelled"
  
  def handle(request: JsonRpcRequest): Try[Option[JsonRpcMessage]] =
    Try {
      val requestId = request.params.flatMap(_.get("requestId").map(_.toString))
      val reason = request.params.flatMap(_.get("reason").map(_.toString))
      
      // TODO: Implement cancellation logic
      
      // This is a notification, so we return None to indicate no response should be sent
      None
    } 