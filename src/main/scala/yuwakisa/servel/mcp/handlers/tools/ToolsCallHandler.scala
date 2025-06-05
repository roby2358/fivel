package yuwakisa.servel.mcp.handlers.tools

import yuwakisa.fivel.McpRegistry
import yuwakisa.servel.mcp.McpMessageTypes.*
import yuwakisa.servel.mcp.handlers.MessageHandler

import scala.util.{Failure, Success, Try}

class ToolsCallHandler(using tools: List[Tool] = McpRegistry.tools) extends MessageHandler:
  def canHandle(method: String): Boolean = method == "tools/call"
  
  private def callTool(name: String, arguments: Map[String, Any]): Try[ToolResult] =
    tools.find(_.name == name) match
      case Some(tool) =>
        Try {
          val result = tool.call(arguments)
          ToolResult(
            content = result("content").asInstanceOf[List[Map[String, Any]]],
            structuredContent = result("structuredContent").asInstanceOf[Map[String, Any]],
            isError = result("isError").asInstanceOf[Boolean]
          )
        }
      case None =>
        Failure(new IllegalArgumentException(s"Tool not found: $name"))

  private def callToolSuccess(result: ToolResult, requestId: Option[String]): JsonRpcResponse =
    JsonRpcResponse(
      result = Map(
        "content" -> result.content,
        "structuredContent" -> result.structuredContent,
        "isError" -> result.isError
      ),
      id = requestId
    )

  private def callToolFailure(error: Throwable | String, requestId: Option[String], code: Int = -32603): JsonRpcErrorResponse =
    JsonRpcErrorResponse(
      error = JsonRpcError(
        code = code,
        message = error match
          case e: Throwable => e.getMessage
          case s: String => s
      ),
      id = requestId
    )
  
  def handle(request: JsonRpcRequest): Try[Option[JsonRpcMessage]] =
    Try {
      val name = request.params.flatMap(_.get("name").map(_.toString))
      val arguments = request.params.flatMap(_.get("arguments").map(_.asInstanceOf[Map[String, Any]]))
      
      (name, arguments) match
        case (Some(n), Some(args)) =>
          callTool(n, args) match
            case Success(result) => Some(callToolSuccess(result, request.id))
            case Failure(e) => Some(callToolFailure(e, request.id))
        case _ =>
          Some(callToolFailure("Missing required parameters: name and arguments", request.id, -32602))
    } 