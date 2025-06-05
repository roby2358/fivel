package yuwakisa.servel.mcp.handlers

import yuwakisa.servel.mcp.McpMessageTypes.*
import scala.util.Try

trait MessageHandler:
  def canHandle(method: String): Boolean
  def handle(request: JsonRpcRequest): Try[Option[JsonRpcMessage]] 