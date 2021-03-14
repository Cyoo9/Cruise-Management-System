#!/bin/bash

# Indicate the path of the java compiler to use
export JAVA_HOME=/usr/csshare/pkgs/jdk1.7.0_17
export PATH=$JAVA_HOME/bin:$PATH

# Export classpath with the postgressql driver
export CLASSPATH=$CLASSPATH:$PWD/pg73jdbc3.jar
javac -cp ".;lib/postgresql-42.1.4.jar;" src/DBproject.java -d bin/
