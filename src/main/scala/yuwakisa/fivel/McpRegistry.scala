package yuwakisa.fivel

import yuwakisa.fivel.mcp.TableTool
import yuwakisa.fivel.models.tables.Tables
import yuwakisa.servel.mcp.handlers.tools.Tool
import yuwakisa.servel.mcp.handlers.resources.Resource
import yuwakisa.servel.mcp.handlers.prompts.Prompt

object McpRegistry extends Logging:
  logger.info("Building MCP registry")
  Tables.AllMcpNames.filterNot(n => n.matches("^[a-zA-Z0-9_-]{1,64}$")).foreach { n => 
    logger.warn(s"Invalid MCP name format: $n")
  }
  val tools: List[Tool] = Tables.AllMcpNames.map(TableTool(_)).toList
  val prompts: List[Prompt] = List.empty
  val resources: List[Resource] = List.empty 