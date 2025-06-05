# Model Context Protocol (MCP) Implementation

This directory contains the implementation of the Model Context Protocol (version 2024-11-05), a JSON-RPC 2.0 based protocol for LLM agent communication.

> **Note on Version Compatibility**: While this implementation uses MCP version 2024-11-05 for stdio transport (for Claude Desktop compatibility), it maintains support for the streamable HTTP transport features from version 2025-03-26.

## Overview

The implementation provides a servlet that handles MCP communication over HTTP, supporting both immediate responses and streaming via Server-Sent Events (SSE). It enables standardized communication between LLM agents and their tools/resources.

## Features

- JSON-RPC 2.0 compliant message handling
- Server-Sent Events (SSE) for streaming responses
- Session-based communication
- Comprehensive tool and resource management
- Built-in security features
- Support for cancellation and progress tracking

## Key Components

### Core Components
- `McpServlet`: Main entry point that handles `/mcp/*` requests and manages sessions
- `MessageHandler`: Base interface for handling different types of JSON-RPC messages
- `MessageHandlerRegistry`: Registry for all available message handlers

### Message Handlers

#### Core Handlers
- `InitializeHandler`: Handles protocol initialization and capability negotiation
- `PingHandler`: Handles keep-alive ping messages

#### Notification Handlers
- `CancelledNotificationHandler`: Handles cancellation notifications
- `ProgressNotificationHandler`: Handles progress update notifications
- `InitializedNotificationHandler`: Handles initialization completion notifications

#### Tools Handlers
- `ToolsListHandler`: Lists available tools
- `ToolsCallHandler`: Executes tool calls

#### Resource Handlers
- `ResourcesListHandler`: Lists available resources
- `ResourcesReadHandler`: Reads resource contents

## Setup and Configuration

### Quick Start

1. Add the `McpServlet` to your `ServerRunner`:
```scala
val server = new ServerRunner()
server.addServlet(new McpServlet(), "/mcp/*")
```

2. Configure security settings:
```scala
// Configure CORS
servlet.setCorsOrigins(List("https://your-domain.com"))

// Configure authentication
servlet.setAuthenticationRequired(true)
```

3. Implement required capabilities based on your needs:
```scala
// Example: Enable tools capability
servlet.enableCapability("tools")
```

### Configuration Options

- `sessionTimeout`: Duration before inactive sessions expire (default: 30 minutes)
- `maxConcurrentSessions`: Maximum number of concurrent sessions (default: 100)
- `rateLimit`: Requests per minute per session (default: 60)
- `enableStreaming`: Enable/disable SSE streaming (default: true)

## Protocol Details

For detailed protocol specifications, see:
- `streamablehttp.txt`: HTTP transport details and streaming support
- `mcpmessages.txt`: JSON-RPC message formats and capabilities

### Message Flow

1. Client initiates connection with `initialize` request
2. Server responds with supported capabilities
3. Client can then make tool calls and resource requests
4. Server streams responses via SSE when appropriate
5. Either party can cancel operations using `cancelled` notification

## Security Considerations

### Current Implementation
- Basic CORS headers (configurable)
- Session-based authentication via Mcp-Session-Id
- Input validation for all requests
- Rate limiting support
- Safe output handling

### Production Recommendations
1. Security Configuration:
   - Configure CORS to only allow specific origins
   - Implement CSRF protection
   - Add proper authentication before processing JSON-RPC requests
   - Bind server to 127.0.0.1 instead of 0.0.0.0

2. Additional Security Measures:
   - Implement request signing
   - Add request logging and monitoring
   - Set up proper SSL/TLS configuration
   - Implement IP-based rate limiting

## Error Handling

The implementation uses standard JSON-RPC error codes:
- `-32700`: Parse error
- `-32600`: Invalid request
- `-32601`: Method not found
- `-32602`: Invalid params
- `-32603`: Internal error
- `-32000` to `-32099`: Server error

## Contributing

1. Fork the repository
2. Create a feature branch
3. Submit a pull request

## License

[Add your license information here]

For detailed security requirements and protocol specifications, refer to the official MCP documentation. 