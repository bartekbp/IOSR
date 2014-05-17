#!/bin/bash

DIR=`dirname $0`
echo ${DIR}
nohup ${DIR}/kaflog_broker.sh &
sleep 3
nohup ${DIR}/kaflog_create_topic.sh &
sleep 1
nohup ${DIR}/kaflog_producer.sh &