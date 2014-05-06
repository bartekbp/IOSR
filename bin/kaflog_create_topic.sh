#!/bin/bash

~/kafka/bin/kafka-topics.sh --create --zookeeper cloudera-master:2181 --replication-factor 1 --partition 1 --topic kaflogtopic