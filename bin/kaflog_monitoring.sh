#!/bin/bash

# Enter the root directory (one above bin/)
RUNNER_SCRIPT_DIR=$(cd ${0%/*} && pwd)
cd $RUNNER_SCRIPT_DIR/..

# Generate classpath adding all jars in lib directory
CP=`ls lib | sed -e 's|^|lib/|' | tr '\n' ':'`

# Start the producer
# default MBean port is 9010
java -cp $CP pl.edu.agh.kaflog.master.Main