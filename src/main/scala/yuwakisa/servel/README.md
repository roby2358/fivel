# Servel Package

The `servel` package provides a lightweight HTTP server implementation with servlet support, built on top of Jetty. It includes utilities for handling HTTP requests, managing static content, and implementing the Model Context Protocol (MCP).

## Core Components

### Server Components
- `ServerRunner`: Main server implementation that manages servlet contexts, routes, and filters
- `Content`: Utility object for handling different content types (text, JSON) in HTTP responses
- `StaticContent`: Handles serving static files from the classpath's `/public` directory
- `StaticContentServlet`: Servlet implementation for serving static content

### Servlet Implementations
- `HelloWorldServlet`: Simple example servlet that returns "Hello world!"
- `HealthServlet`: Health check endpoint that returns server status
- `ConfigServlet`: Provides server configuration information
- `McpServlet`: Implements the Model Context Protocol (MCP) for LLM agent communication

### Filters
- `CorsFilter`: Handles CORS preflight requests and adds CORS headers to responses

### Utilities
- `Types`: Type definitions for servlet routing
- `extensions`: Extension methods for logging and functional programming

## Model Context Protocol (MCP)

The package includes a comprehensive implementation of the Model Context Protocol (MCP), which is a JSON-RPC 2.0 based protocol for LLM agent communication. The MCP implementation is located in the `mcp` subpackage and includes:

- Message handlers for different types of requests
- Support for both immediate responses and streaming via Server-Sent Events (SSE)
- Session management
- Tool and resource handling

For more details about the MCP implementation, see the [MCP README](mcp/README.md).

## Usage

To use the server:

1. Create a `ServerRunner` instance with your desired port and routes
2. Add servlets and filters as needed
3. Call `start()` to begin serving requests

Example:
```scala
val runner = ServerRunner(
  port = 8000,
  routes = Map(
    "/hello" -> classOf[HelloWorldServlet],
    "/static/*" -> classOf[StaticContentServlet]
  ),
  filters = Map(
    "/*" -> classOf[CorsFilter]
  )
)
runner.start()
```

## Dependencies

- Jetty EE10 for servlet container
- Jackson for JSON processing
- SLF4J for logging
- Apache Commons IO for file operations 