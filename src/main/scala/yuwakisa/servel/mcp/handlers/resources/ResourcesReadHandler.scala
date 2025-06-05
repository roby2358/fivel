package yuwakisa.servel.mcp.handlers.resources

import yuwakisa.servel.mcp.McpMessageTypes.*
import yuwakisa.servel.mcp.handlers.MessageHandler

import java.time.Instant
import scala.util.{Failure, Success, Try}

class ResourcesReadHandler(using resources: List[Resource]) extends MessageHandler:
  def canHandle(method: String): Boolean = method == "resources/read"

  private def readResource(uri: String): Try[List[ResourceContent]] =
    resources.find(_.uri == uri) match
      case Some(resource) =>
        Success(List(
          ResourceContent.Text(Instant.now().toString)
        ))
      case None =>
        Failure(new IllegalArgumentException(s"Resource not found: $uri"))

  private def readResourceSuccess(contents: List[ResourceContent], requestId: Option[String]): JsonRpcResponse =
    JsonRpcResponse(
      result = Map(
        "contents" -> contents.map {
          case ResourceContent.Text(text) =>
            Map("uri" -> "resource://current_time", "text" -> text)
          case ResourceContent.Blob(data, mimeType) =>
            Map("uri" -> "resource://current_time", "blob" -> data, "mimeType" -> mimeType)
        }
      ),
      id = requestId
    )

  private def readResourceFailure(errorMessage: String, requestId: Option[String]): JsonRpcErrorResponse =
    JsonRpcErrorResponse(
      error = JsonRpcError(
        code = -32002,
        message = errorMessage
      ),
      id = requestId
    )

  def handle(request: JsonRpcRequest): Try[Option[JsonRpcMessage]] =
    Try:
      val uri = request.params.flatMap(_.get("uri").map(_.toString))
      
      uri match
        case Some(u) =>
          readResource(u) match
            case Success(contents) =>
              Some(readResourceSuccess(contents, request.id))
            case Failure(e) =>
              Some(readResourceFailure(e.getMessage, request.id))
        case None =>
          Some(JsonRpcErrorResponse(
            error = JsonRpcError(
              code = -32602,
              message = "Missing required parameter: uri"
            ),
            id = request.id
          ))
