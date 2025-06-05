package yuwakisa.servel

import jakarta.servlet.http.HttpServletResponse
import munit.FunSuite
import org.scalamock.clazz.Mock
import yuwakisa.servel.adapters.MunitMockFactory

import java.io.{PrintWriter, StringWriter}

// Requirements:
// This class MUST use munit and scalamock together
// It MUST NOT create a boilerplate stub

class ContentTest extends FunSuite with MunitMockFactory with Mock {

    test("okText should set correct content type and status") {
        // Wrap the entire test in withExpectations
        withExpectations {
            // Given
            val response = mock[HttpServletResponse]
            val writer = new StringWriter()
            val printWriter = new PrintWriter(writer)

            // Configure expectations with Scala 3 syntax
            (() => response.getWriter).expects().returning(printWriter)
            (response.setContentType(_: String)).expects(Content.Type.Text)
            (response.setStatus(_: Int)).expects(HttpServletResponse.SC_OK)

            val content = "Hello, World!"

            // When
            Content.okText(response, content)

            // Then
            assertEquals(writer.toString.trim, content)
        }
    }

    test("okJson should serialize object and set correct content type and status") {
        // Wrap the entire test in withExpectations
        withExpectations {
            // Given
            val response = mock[HttpServletResponse]
            val writer = new StringWriter()
            val printWriter = new PrintWriter(writer)

            // Configure expectations with Scala 3 syntax
            (() => response.getWriter).expects().returning(printWriter)
            (response.setContentType(_: String)).expects(Content.Type.Json)
            (response.setStatus(_: Int)).expects(HttpServletResponse.SC_OK)

            case class TestData(name: String, value: Int)
            val testData = TestData("public/test", 42)
            val expectedJson = """{"name":"public/test","value":42}"""

            // When
            Content.okJson(response, testData)

            // Then
            assertEquals(writer.toString.trim, expectedJson)
        }
    }
}