package yuwakisa.servel.mcp.handlers.prompts

case class PromptArgument(
  name: String,
  description: String,
  required: Boolean,
  schema: Option[Map[String, Any]] = None
)

case class PromptMessage(
  role: String,
  content: Map[String, Any]
)

trait Prompt:
  def name: String
  def description: String
  def arguments: List[PromptArgument]
  def includeContext: String = "none"
  def annotations: Option[Map[String, Any]] = None
  def getMessages(arguments: Map[String, Any]): List[PromptMessage] 