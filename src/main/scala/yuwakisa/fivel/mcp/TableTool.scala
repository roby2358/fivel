package yuwakisa.fivel.mcp

import yuwakisa.fivel.models.tables.Tables
import yuwakisa.servel.mcp.handlers.tools.Tool

class TableTool(val name: String) extends Tool {
  val description: String = s"A table to roll $name"

  val inputSchema: Map[String, Any] = Map(
    "type" -> "object",
    "properties" -> Map.empty,
    "required" -> List.empty
  )

  val annotations: Map[String, Any] = Map(
    "title" -> s"Table $name",
    "readOnlyHint" -> true,
    "openWorldHint" -> false
  )

  def call(input: Map[String, Any]): Map[String, Any] = {
    val fixedName = name.replace("_", " ")
    val result: String = Tables.All(fixedName)(Tables.mkString) match
      case Left(error) => s"error: $error"
      case Right(string) => string

    Map(
      "content" -> List(
        Map(
          "type" -> "text",
          "text" -> result
        )
      ),
      "structuredContent" -> Map.empty,
      "isError" -> false
    )
  }
} 