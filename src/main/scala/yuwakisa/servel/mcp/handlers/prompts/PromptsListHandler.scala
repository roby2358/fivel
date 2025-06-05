package yuwakisa.servel.mcp.handlers.prompts

import yuwakisa.servel.mcp.McpMessageTypes.*
import yuwakisa.servel.mcp.handlers.MessageHandler

import scala.util.Try

class PromptsListHandler(using prompts: List[Prompt]) extends MessageHandler:
  def canHandle(method: String): Boolean = method == "prompts/list"

  private def listPrompts(cursor: Option[String]): (List[Prompt], Option[String]) =
    // Simple pagination - just return all prompts for now
    (prompts, None)

  def handle(request: JsonRpcRequest): Try[Option[JsonRpcMessage]] =
    Try:
      val cursor = request.params.flatMap(_.get("cursor").map(_.toString))
      val (promptsList, nextCursor) = listPrompts(cursor)
      
      val result = Map("prompts" -> promptsList) ++ 
        nextCursor.map(cursor => Map("nextCursor" -> cursor)).getOrElse(Map.empty)
      
      Some(JsonRpcResponse(
        result = result,
        id = request.id
      )) 