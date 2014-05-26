#!/bin/bash

# This script must be run as root
# sudo sh cloudera-master.sh

# Adjust /etc/hosts for the nodes to see each other
cp -f /vagrant/files/hosts /etc/hosts

# Set up the VM

# Install curl
apt-get install curl -y

# Install Java 1.7
cp /vagrant/files/jdk-7u55-linux-x64.tar.gz .
tar zxvf jdk-7u55-linux-x64.tar.gz
mkdir -p /usr/lib/jvm/
mv jdk1.7.0_55 /usr/lib/jvm/java-7-oracle/
ln -s /usr/lib/jvm/java-7-oracle/bin/java /usr/bin/java
echo 'export JAVA_HOME=/usr/lib/jvm/java-7-oracle' >> /home/vagrant/.bashrc
ln -s /usr/lib/jvm/java-7-oracle /usr/lib/jvm/default-java
source /home/vagrant/.bashrc
rm jdk-7u55-linux-x64.tar.gz 

cp /etc/sudoers /etc/sudoers.copy
echo "Defaults env_keep+=JAVA_HOME" >> /etc/sudoers.copy
mv /etc/sudoers.copy /etc/sudoers

# Install cloudera
REPOCM=${REPOCM:-cm5}
CM_REPO_HOST=${CM_REPO_HOST:-archive.cloudera.com}
CM_MAJOR_VERSION=`echo $REPOCM | sed -e 's/cm\\([0-9]\\).*/\\1/'`
CM_VERSION=`echo $REPOCM | sed -e 's/cm\\([0-9][0-9]*\\)/\\1/'`
OS_CODENAME=`lsb_release -sc`
OS_DISTID=`lsb_release -si | tr '[A-Z]' '[a-z]'`
if [ $CM_MAJOR_VERSION -ge 4 ]; then
      cat > /etc/apt/sources.list.d/cloudera-$REPOCM.list <<EOF
deb [arch=amd64] http://$CM_REPO_HOST/cm$CM_MAJOR_VERSION/$OS_DISTID/$OS_CODENAME/amd64/cm $OS_CODENAME-$REPOCM contrib
deb-src http://$CM_REPO_HOST/cm$CM_MAJOR_VERSION/$OS_DISTID/$OS_CODENAME/amd64/cm $OS_CODENAME-$REPOCM contrib
EOF
	echo "deb [arch=amd64] http://$CM_REPO_HOST/cm$CM_MAJOR_VERSION/$OS_DISTID/$OS_CODENAME/amd64/cm $OS_CODENAME-$REPOCM contrib"
	echo "deb-src http://$CM_REPO_HOST/cm$CM_MAJOR_VERSION/$OS_DISTID/$OS_CODENAME/amd64/cm $OS_CODENAME-$REPOCM contrib"
    curl -s http://$CM_REPO_HOST/cm$CM_MAJOR_VERSION/$OS_DISTID/$OS_CODENAME/amd64/cm/archive.key > key
    apt-key add key
    rm key
fi
apt-get update
export DEBIAN_FRONTEND=noninteractive
apt-get -q -y --force-yes install cloudera-manager-server-db cloudera-manager-server cloudera-manager-daemons
service cloudera-scm-server-db initdb
service cloudera-scm-server-db start
service cloudera-scm-server start

#  Prepare kafka binaries
cp /vagrant/files/kafka_2.10-0.8.1.tgz .
tar xzf kafka_2.10-0.8.1.tgz
rm -f kafka_2.10-0.8.1.tgz
mv kafka_2.10-0.8.1 kafka
chown -R vagrant kafka

# sudo -u hdfs hadoop fs -mkdir /user/vagrant
# sudo -u hdfs hadoop fs -chown vagrant /user/vagrant


# Alternatywa - instalacja CDH5 bez cloudera managera (reczna instalacja i konfiguracja wszystkich nodeow) - czy chcemy?
# dpkg -i cdh5-repository_1.0_all.deb
# curl -s http://archive.cloudera.com/cdh5/ubuntu/precise/amd64/cdh/archive.key | sudo apt-key add -
# apt-get update
# apt-get install -y zookeeper hadoop-yarn-resourcemanager hadoop-hdfs-namenode hadoop-hdfs-secondarynamenode hadoop-0.20-mapreduce-tasktracker hadoop-hdfs-datanode hadoop-client

# Set timezone to proper
sudo ln -sf /usr/share/zoneinfo/Europe/Warsaw /etc/localtime