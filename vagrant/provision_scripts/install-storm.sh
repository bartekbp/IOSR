#!/bin/bash

#install storm dependencies
#   install ZeroMQ
apt-get install -y curl
cat > /etc/apt/sources.list.d/cloudera-$REPOCM.list <<EOF
deb [arch=amd64] http://$CM_REPO_HOST/cm$CM_MAJOR_VERSION/$OS_DISTID/$OS_CODENAME/amd64/cm $OS_CODENAME-$REPOCM contrib
deb-src http://$CM_REPO_HOST/cm$CM_MAJOR_VERSION/$OS_DISTID/$OS_CODENAME/amd64/cm $OS_CODENAME-$REPOCM contrib
EOF
echo "deb [arch=amd64] http://$CM_REPO_HOST/cm$CM_MAJOR_VERSION/$OS_DISTID/$OS_CODENAME/amd64/cm $OS_CODENAME-$REPOCM contrib"
echo "deb-src http://$CM_REPO_HOST/cm$CM_MAJOR_VERSION/$OS_DISTID/$OS_CODENAME/amd64/cm $OS_CODENAME-$REPOCM contrib"
curl -s http://$CM_REPO_HOST/cm$CM_MAJOR_VERSION/$OS_DISTID/$OS_CODENAME/amd64/cm/archive.key > key
apt-key add key
rm key

apt-get update
apt-get install -y libtool autoconf automake uuid-dev build-essential
wget -nv http://download.zeromq.org/zeromq-2.2.0.tar.gz
tar -xzf zeromq-2.2.0.tar.gz
cd zeromq-2.2.0
./configure
make && make install
cd -
rm -rf zeromq-2.2.0 zeromq-2.2.0.tar.gz

##   install jzmq
apt-get install -y git pkg-config g++
export JAVA_HOME=/usr/lib/jvm/java-7-oracle
git clone https://github.com/nathanmarz/jzmq
cd jzmq
# bugfix https://github.com/zeromq/jzmq/issues/114
sed -i 's/classdist_noinst.stamp/classnoinst.stamp/g' src/Makefile.am
# bugfix end
./autogen.sh
./configure
make
cd -

#   install unzpi
sudo apt-get install unzip

#install storm
wget -nv https://github.com/downloads/nathanmarz/storm/storm-0.8.1.zip
unzip storm-0.8.1.zip
rm -rf storm-0.8.1.zip
cp /vagrant/files/config/storm/storm.yaml storm-0.8.1/conf
mkdir /mnt/storm
chown vagrant /mnt/storm
chgrp vagrant /mnt/storm
chown -R vagrant storm-0.8.1
chgrp -R vagrant storm-0.8.1

