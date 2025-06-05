package yuwakisa.servel

import jakarta.servlet.http.HttpServlet

/** Type definitions for servlet routing */
object Types:
  /** Represents a class that extends HttpServlet */
  type ServletClass = Class[? <: HttpServlet]
  
  /** Maps URL paths to servlet classes */
  type Routes = Map[String, ServletClass] 