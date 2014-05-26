#!/bin/bash

# This script must be run as root
# sudo sh cloudera-slave.sh

# Adjust /etc/hosts for the nodes to see each other
cp -f /vagrant/files/hosts /etc/hosts


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

# Set timezone to proper
sudo ln -sf /usr/share/zoneinfo/Europe/Warsaw /etc/localtime