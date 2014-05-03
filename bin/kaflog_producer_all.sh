#!/bin/bash

nohup ./kaflog_broker.sh &
nohup ./kaflog_create_topic.sh &
nohup ./kaflog_producer.sh &