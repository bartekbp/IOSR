#!/bin/bash

DIR=`dirname $0`
nohup ${DIR}/kaflog_create_topic.sh &
sleep 1
nohup ${DIR}/kaflog_producer.sh &
