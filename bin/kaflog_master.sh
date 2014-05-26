#!/bin/bash

# Enter the root directory (one above bin/)
RUNNER_SCRIPT_DIR=$(cd ${0%/*} && pwd)
cd $RUNNER_SCRIPT_DIR/..

# Generate classpath adding all jars in lib directory
CP=lib/kaflog-mastermonitoring-0.1.war

# Start master monitoring
java -Xdebug -Xrunjdwp:server=y,transport=dt_socket,address=5005,suspend=n -jar $CP
