#!/bin/bash

hive -e "drop table kaflogdata;"
sudo -u hdfs hadoop fs -rm -R /user/hive/warehouse
