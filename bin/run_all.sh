#!/bin/bash

set -e
DIR=`dirname $0`
nohup ${DIR}/kaflog_broker.sh &
sleep 10
nohup ${DIR}/kaflog_master.sh &
sleep 10
ssh kafka-node1 "nohup ${DIR}/kaflog_producer_all.sh &"
nohup ${DIR}/storm_setup.sh &
sleep 20
nohup ${DIR}/kaflog_storm_consumer.sh &
