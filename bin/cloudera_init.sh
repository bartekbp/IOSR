#!/bin/bash

sudo -u hdfs hadoop fs -mkdir /user/vagrant
sudo -u hdfs hadoop fs -chmod 777 /user/hive
sudo -u hdfs hadoop fs -chown vagrant /user/vagrant
