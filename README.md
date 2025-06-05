# Warning

This is a toy! There are no promises and no guarantees associated with this project!

# Running

## Run as webapp + HTTP server

From the command line
    sbt run

In your browser, open
    http://localhost:8000

Note: This also supports streamable HTTP MCP at http://localhost:8000/mcp

## Run as MCP stdio in Claude Desktop

Build the executable jar

```sh
cd <your_path>/fivel
sbt assembly
```

This builds `<your_path>/fivel/target/scala-3.3.6/fivel.jar`

Launch Claude Desktop. In `hamburger -> File -> Settings -> Developer`

Edit configs to open `claude_desktop_config.json`

**Note: there are 3 config files. Use `_desktop_config`**

Add

```json
{
  "mcpServers": {
    "fivel": {
       "command": "java",
       "args": ["-jar", "<your_path>/fivel/target/scala-3.3.6/fivel.jar"]
     }
  }
}
```

I usually prompt "Describe the fivel tools" to get Claude on track. That's also a way to get an overview of what's there.
