#!/bin/bash

/home/vagrant/storm-0.8.1/bin/storm jar /vagrant/files/kaflog-0.1/lib/kaflog-0.1-jar-with-dependencies.jar \
    pl.edu.agh.kaflog.stormconsumer.KaflogStorm

