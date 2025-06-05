package yuwakisa.servel.mcp.handlers.notification

import yuwakisa.servel.mcp.McpMessageTypes.*
import yuwakisa.servel.mcp.handlers.MessageHandler

import scala.util.Try

class ProgressNotificationHandler extends MessageHandler:
  def canHandle(method: String): Boolean = method == "notifications/progress"
  
  def handle(request: JsonRpcRequest): Try[Option[JsonRpcMessage]] =
    Try {
      val progressToken = request.params.flatMap(_.get("progressToken").map(_.toString))
      val progress = request.params.flatMap(_.get("progress").map(_.asInstanceOf[Number].doubleValue()))
      val total = request.params.flatMap(_.get("total").map(_.asInstanceOf[Number].doubleValue()))
      val message = request.params.flatMap(_.get("message").map(_.toString))
      
      // TODO: Implement progress tracking logic
      
      // This is a notification, so we return None to indicate no response should be sent
      None
    } 