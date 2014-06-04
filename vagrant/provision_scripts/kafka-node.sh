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
echo 'export PATH=$JAVA_HOME/bin:$PATH' >> /home/vagrant/.bashrc
source /home/vagrant/.bashrc
ln -s /usr/lib/jvm/java-7-oracle /usr/lib/jvm/default-java
rm jdk-7u55-linux-x64.tar.gz 

cp /etc/sudoers /etc/sudoers.copy
echo "Defaults env_keep+=JAVA_HOME" >> /etc/sudoers.copy
mv /etc/sudoers.copy /etc/sudoers

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


# Set timezone to proper
sudo ln -sf /usr/share/zoneinfo/Europe/Warsaw /etc/localtime

# Install and configure stunnel 4 for secure connection between producer and broker
sudo apt-get install stunnel4
mkdir -p /var/chroot/stunnel4/ 
chown stunnel4:stunnel4 /var/chroot/stunnel4/ 
cp /vagrant/files/config/kafka-node1/stunnel4_default /etc/default/stunnel4
cp /vagrant/files/config/kafka-node1/stunnel4_kafka_client.conf /etc/stunnel/
cp /vagrant/files/config/kafka-node1/client.pem /etc/stunnel/
cp /vagrant/files/config/kafka-node1/cacert.pem /etc/stunnel/

/etc/init.d/stunnel4 start