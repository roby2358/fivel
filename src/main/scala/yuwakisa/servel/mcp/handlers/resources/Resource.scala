package yuwakisa.servel.mcp.handlers.resources

import scala.util.Try

enum ResourceContent:
  case Text(text: String)
  case Blob(data: String, mimeType: String) 

trait Resource:
  val uri: String
  val name: String
  val description: Option[String]
  val mimeType: Option[String]
  def read(): Try[List[ResourceContent]] 
  