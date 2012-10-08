#!/bin/sh
export CLASSPATH=target/classes/main:lib/xmlrpc-2.0.jar:lib/commons-codec-1.3.jar:lib/freetts.jar
java org.laughingpanda.games.poker.indian.client.PokerClient

