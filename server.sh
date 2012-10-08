#!/bin/sh
export CLASSPATH=dist:target/classes/main:lib/xmlrpc-2.0.jar:lib/commons-codec-1.3.jar:lib/freetts.jar:lib/forms-1.0.7.jar:lib/cmulex.jar:lib/cmu_us_kal.jar

java org.laughingpanda.games.poker.indian.server.Main localhost 5000

