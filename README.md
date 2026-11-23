# Warning

This is a toy! There are no promises and no guarantees associated with this project!

## Notes

Some of the tables are broken after I enabled MCP. Will fix as I go.

In order to refresh the tables in Claude Desktop, you need to rebuild the jar, then choose

`hamburger -> File -> Exit`

to exit Claude Desktop all the way.

# Running

## Setup

Fivel needs Java 21 and sbt. If you have [just](https://github.com/casey/just), this installs both
into `~/.local/opt` (Linux x64, no sudo; skips anything already installed):

```sh
just setup
```

Run `just` to list the other recipes (`test`, `check-tables`, `assembly`).

## Run as webapp + HTTP server

From the command line
```sh
just up
```

or, without just,
```sh
sbt "runMain yuwakisa.fivel.ServerMain"
```

It prints the URL when it's ready. In your browser, open
    http://localhost:8925

Note: This also supports streamable HTTP MCP at http://localhost:8925/mcp

## Run as MCP stdio in Claude Desktop

Build the executable jar

```sh
cd <your_path>/fivel
just assembly    # or: sbt assembly
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
