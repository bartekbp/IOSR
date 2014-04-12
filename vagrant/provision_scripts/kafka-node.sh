#!/bin/bash

# This script must be run as root
# sudo sh kafka-node.sh

# Adjust /etc/hosts for the nodes to see each other
cp -f /vagrant/files/hosts /etc/hosts

# Set up the VM - install curl and java
apt-get install curl -y
REPOCM=${REPOCM:-cm4}
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
    curl -s http://$CM_REPO_HOST/cm$CM_MAJOR_VERSION/$OS_DISTID/$OS_CODENAME/amd64/cm/archive.key > key
    apt-key add key
    rm key
fi
apt-get update
export DEBIAN_FRONTEND=noninteractive
apt-get -q -y --force-yes install oracle-j2sdk1.6

#  Prepare kafka binaries
cp /vagrant/files/kafka_2.10-0.8.1.tgz .
tar xzf kafka_2.10-0.8.1.tgz
rm -f kafka_2.10-0.8.1.tgz
mv kafka_2.10-0.8.1 kafka