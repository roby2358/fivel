package yuwakisa.servel.mcp.handlers

import yuwakisa.fivel.McpRegistry
import yuwakisa.servel.mcp.handlers.notification.{CancelledNotificationHandler, InitializedNotificationHandler, ProgressNotificationHandler}
import yuwakisa.servel.mcp.handlers.prompts.{PromptsGetHandler, PromptsListHandler}
import yuwakisa.servel.mcp.handlers.resources.{ResourcesListHandler, ResourcesReadHandler}
import yuwakisa.servel.mcp.handlers.tools.{ToolsCallHandler, ToolsListHandler}

object MessageHandlerRegistry:
  private val handlers: List[MessageHandler] = List(
    new CancelledNotificationHandler,
    new InitializedNotificationHandler,
    new InitializeHandler,
    new PingHandler,
    new ProgressNotificationHandler,
    new PromptsGetHandler(using McpRegistry.prompts),
    new PromptsListHandler(using McpRegistry.prompts),
    new ResourcesListHandler(using McpRegistry.resources),
    new ResourcesReadHandler(using McpRegistry.resources),
    new ToolsCallHandler,
    new ToolsListHandler,
  )
  
  def getHandler(method: String): Option[MessageHandler] =
    handlers.find(_.canHandle(method)) 