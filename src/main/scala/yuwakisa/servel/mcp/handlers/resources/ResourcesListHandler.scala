package yuwakisa.servel.mcp.handlers.resources

import yuwakisa.servel.mcp.McpMessageTypes.*
import yuwakisa.servel.mcp.handlers.MessageHandler

import scala.util.Try

class ResourcesListHandler(using resources: List[Resource]) extends MessageHandler:
  def canHandle(method: String): Boolean = method == "resources/list"

  private def listResources(cursor: Option[String]): (List[Resource], Option[String]) =
    // Simple pagination - just return all resources for now
    (resources, None)

  def handle(request: JsonRpcRequest): Try[Option[JsonRpcMessage]] =
    Try:
      val cursor = request.params.flatMap(_.get("cursor").map(_.toString))
      val (resources, nextCursor) = listResources(cursor)
      
      val result = Map("resources" -> resources) ++ 
        nextCursor.map(cursor => Map("nextCursor" -> cursor)).getOrElse(Map.empty)
      
      Some(JsonRpcResponse(
        result = result,
        id = request.id
      )) 