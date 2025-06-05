package yuwakisa.servel.mcp

import com.fasterxml.jackson.annotation.{JsonInclude, JsonProperty}

object McpMessageTypes {
  // Base message types
  trait JsonRpcMessage {
    val jsonrpc: String = "2.0"
    val id: Option[String]
  }

  @JsonInclude(JsonInclude.Include.NON_NULL)
  case class JsonRpcRequest(
    method: String,
    params: Option[Map[String, Any]] = None,
    override val id: Option[String] = None
  ) extends JsonRpcMessage

  @JsonInclude(JsonInclude.Include.NON_NULL)
  case class JsonRpcResponse(
    result: Any,
    override val id: Option[String]
  ) extends JsonRpcMessage

  @JsonInclude(JsonInclude.Include.NON_NULL)
  case class JsonRpcErrorResponse(
    error: JsonRpcError,
    override val id: Option[String]
  ) extends JsonRpcMessage

  @JsonInclude(JsonInclude.Include.NON_NULL)
  case class JsonRpcError(
    code: Int,
    message: String,
    data: Option[Any] = None
  )

  // MCP specific message types
  case class InitializeParams(
    capabilities: Map[String, Any]
  )

  case class InitializeResult(
    capabilities: Map[String, Any]
  )

  case class ToolCallParams(
    name: String,
    parameters: Map[String, Any]
  )

  case class ResourceRequestParams(
    path: String
  )

  case class PromptRequestParams(
    prompt: String
  )
} 