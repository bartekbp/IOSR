#!/bin/bash

# This script must be run as root
# sudo sh kafka-node.sh

# Adjust /etc/hosts for the nodes to see each other
cp -f /vagrant/files/hosts /etc/hosts

# Set up the VM

# Install curl and other useful utils
apt-get install curl -y
apt-get install -y bc

# Install Java 1.7
cp /vagrant/files/jdk-7u55-linux-x64.tar.gz .
tar zxvf jdk-7u55-linux-x64.tar.gz
mkdir -p /usr/lib/jvm/
mv jdk1.7.0_55 /usr/lib/jvm/java-7-oracle/
ln -s /usr/lib/jvm/java-7-oracle/bin/java /usr/bin/java
echo 'export JAVA_HOME=/usr/lib/jvm/java-7-oracle' >> /home/vagrant/.bashrc
rm jdk-7u55-linux-x64.tar.gz 

# Install and setup syslog-ng
apt-get install -y syslog-ng
cp /vagrant/files/syslog-ng.conf /etc/syslog-ng/syslog-ng.conf
service syslog-ng restart


#  Prepare kafka binaries
cp /vagrant/files/kafka_2.10-0.8.1.tgz .
tar xzf kafka_2.10-0.8.1.tgz
rm -f kafka_2.10-0.8.1.tgz
mv kafka_2.10-0.8.1 kafka
chown -R vagrant kafka