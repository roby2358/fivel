package yuwakisa.servel.mcp.handlers.tools

case class ToolResult(
                       content: List[Map[String, Any]], // Must have at least one item
                       structuredContent: Map[String, Any] = Map.empty,
                       isError: Boolean = false
                     )

trait Tool:
  val name: String
  val description: String
  val inputSchema: Map[String, Any]
  val annotations: Map[String, Any]
  
  def call(input: Map[String, Any]): Map[String, Any]
