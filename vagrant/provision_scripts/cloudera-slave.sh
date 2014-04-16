#!/bin/bash

# This script must be run as root
# sudo sh cloudera-slave.sh

# Adjust /etc/hosts for the nodes to see each other
cp -f /vagrant/files/hosts /etc/hosts

# No setup needed as cloudera master can configure it's slaves remotely
# TODO: Is java needed??