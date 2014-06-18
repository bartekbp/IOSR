#!/bin/bash

~/storm-0.8.1/bin/storm nimbus &
~/storm-0.8.1/bin/storm ui &
sleep 5
ssh cloudera-slave1 "nohup ~/storm-0.8.1/bin/storm supervisor &"
