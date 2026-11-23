# Fivel tasks. Run `just` to list them.

opt := env("HOME") / ".local/opt"
jdk_url := "https://api.adoptium.net/v3/binary/latest/21/ga/linux/x64/jdk/hotspot/normal/eclipse"
sbt_version := `sed -n 's/^sbt.version *= *//p' project/build.properties`

# Use the JDK and sbt from `just setup` when they're there, otherwise whatever is on PATH
export JAVA_HOME := if path_exists(opt / "jdk21") == "true" { opt / "jdk21" } else { env("JAVA_HOME", "") }
export PATH := opt / "jdk21/bin" + ":" + opt / "sbt/bin" + ":" + env("PATH")

default:
    @just --list

# Install JDK 21 and sbt into ~/.local/opt (no sudo; skips what's already there)
setup:
    #!/usr/bin/env bash
    set -euo pipefail
    mkdir -p "{{opt}}"
    if [ ! -x "{{opt}}/jdk21/bin/java" ]; then
      echo "Installing JDK 21..."
      mkdir -p "{{opt}}/jdk21"
      curl -fsSL "{{jdk_url}}" | tar xz -C "{{opt}}/jdk21" --strip-components=1
    fi
    if [ ! -x "{{opt}}/sbt/bin/sbt" ]; then
      echo "Installing sbt {{sbt_version}}..."
      curl -fsSL "https://github.com/sbt/sbt/releases/download/v{{sbt_version}}/sbt-{{sbt_version}}.tgz" | tar xz -C "{{opt}}"
    fi
    java -version 2>&1 | head -1
    echo "sbt {{sbt_version}} at {{opt}}/sbt"

# Run the web server on http://localhost:8925 (Enter or Ctrl-C to stop)
up:
    sbt "runMain yuwakisa.fivel.ServerMain"

# Run all tests
test:
    sbt test

# Check the table files for broken {references} and duplicate names
check-tables:
    sbt "testOnly yuwakisa.fivel.models.tables.TableIntegritySpec"

# Build the executable jar (target/scala-3.3.6/fivel.jar) for MCP stdio use
assembly:
    sbt assembly
