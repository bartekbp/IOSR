#!/bin/bash

# Enter the root directory (one above bin/)
RUNNER_SCRIPT_DIR=$(cd ${0%/*} && pwd)
cd $RUNNER_SCRIPT_DIR/..

# Generate classpath adding all jars in lib directory
CP=lib/kaflog-hiveviewcreator-0.1-jar-with-dependencies.jar

# Start hive_creator
java -Xdebug -Xrunjdwp:server=y,transport=dt_socket,address=5007,suspend=n -Dcom.sun.management.jmxremote -Dcom.sun.management.jmxremote.ssl=false -Dcom.sun.management.jmxremote.local.only=false -Dcom.sun.management.jmxremote.port=9015 -Dcom.sun.management.jmxremote.authenticate=false -cp $CP pl.edu.agh.kaflog.hiveviewcreator.Main
