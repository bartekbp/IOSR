#!/bin/bash

~/kafka/bin/kafka-console-consumer.sh --zookeeper cloudera-master:2181 --topic kaflogtopic --from-beginning