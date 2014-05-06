#!/bin/bash

DIR=`dirname $0`
echo ${DIR}
nohup ${DIR}/kaflog_broker.sh &
nohup ${DIR}/kaflog_create_topic.sh &
nohup ${DIR}/kaflog_producer.sh &