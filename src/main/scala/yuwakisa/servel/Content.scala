package yuwakisa.servel

import com.fasterxml.jackson.databind.json.JsonMapper
import com.fasterxml.jackson.module.scala.DefaultScalaModule
import jakarta.servlet.http.HttpServletResponse

object Content:
  val Mapper: JsonMapper = JsonMapper.builder()
    .addModule(DefaultScalaModule)
    .build()

  object Type:
    val Text = "text/plain"
    val Json = "application/json"

  def okText(response: HttpServletResponse, content: String): Unit =
    response.setContentType(Content.Type.Text)
    response.setStatus(HttpServletResponse.SC_OK)
    response.getWriter.println(content)

  def okJson(response: HttpServletResponse, obj: AnyRef): Unit =
    val json = Mapper.writeValueAsString(obj)
    response.setContentType(Content.Type.Json)
    response.setStatus(HttpServletResponse.SC_OK)
    response.getWriter.println(json)
