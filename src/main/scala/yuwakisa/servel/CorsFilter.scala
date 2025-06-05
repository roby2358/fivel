package yuwakisa.servel

import jakarta.servlet.http.{HttpServletRequest, HttpServletResponse}
import jakarta.servlet.{Filter, FilterChain, ServletRequest, ServletResponse}

/** Filter that handles CORS preflight requests and adds CORS headers to all responses */
class CorsFilter extends Filter:
  override def doFilter(request: ServletRequest, response: ServletResponse, chain: FilterChain): Unit =
    val httpRequest = request.asInstanceOf[HttpServletRequest]
    val httpResponse = response.asInstanceOf[HttpServletResponse]
    
    addCorsHeaders(httpResponse)
    
    if httpRequest.getMethod == "OPTIONS" then
      httpResponse.setStatus(HttpServletResponse.SC_OK)
    else
      chain.doFilter(request, response)

  private def addCorsHeaders(resp: HttpServletResponse): Unit =
    resp.setHeader("Access-Control-Allow-Origin", "*")
    resp.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS")
    resp.setHeader("Access-Control-Allow-Headers", "Content-Type, Authorization, X-Requested-With")
    resp.setHeader("Access-Control-Max-Age", "3600") 