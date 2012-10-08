Requirements
------------
* Java JDK 5.0 for server and client 
* Ruby 1.8 or newer for client

Server
------

* Server can be started by running server.sh command line script
(It starts on port 5000, modify the script if you want the change default port)
* Server can also be started directly from IDE by running 
  org.laughingpanda.games.poker.indian.server.Main class.

Java client
------------

* Ant command "ant package" will build the pokerclient.jar.
  In addition to that you need xmlrpc-2.0.jar, commons-codec-1.3.jar and freetts.jar for
  running the client.
* Client must implement the org.laughingpanda.games.poker.indian.client.Bot interface
  or extend org.laughingpanda.games.poker.indian.client.AbstractBot abstract class. 
* Client can be started by running org.laughingpanda.games.poker.indian.client.PokerClient class.

Ruby client
-----------

TODO