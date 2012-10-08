package org.laughingpanda.games.poker.indian.client;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.laughingpanda.games.poker.indian.client.examples.Cecconen;
import org.laughingpanda.games.poker.indian.client.examples.ExampleBot;
import org.laughingpanda.games.poker.indian.client.examples.NahkaFagottiBot;
import org.laughingpanda.games.poker.indian.client.examples.SuperMan;
import org.laughingpanda.games.poker.indian.server.Connector;
import org.laughingpanda.games.poker.indian.server.IndianpokerCommentator;
import org.laughingpanda.games.poker.indian.server.Server;
import org.laughingpanda.games.poker.indian.server.ServerUi;
import org.laughingpanda.games.poker.indian.server.StdOutCommentator;
import org.laughingpanda.games.poker.indian.server.SystemOutUI;
import org.laughingpanda.games.poker.indian.server.TextToSpeech;
import org.laughingpanda.games.poker.indian.server.XmlRpcConnector;

/**
 * @author Paolo Perrotta
 */
public class DemoMatch {

    private static final int SERVER_PORT = 5000;

    public static void main(String[] args) throws Exception {
        DemoMatch match = new DemoMatch();
        match.run();
    }

    protected void run() {
        startServer(SERVER_PORT);
        pause(getServerStartPause());
        Bot[] players = getPlayers();
        for (int i = 0; i < players.length; i++) {
            startClient(players[i], i + 1);
            pause(getPlayerJoinPause());
        }
    }

    protected int getServerStartPause() {
        return 7;
    }

    protected int getPlayerJoinPause() {
        return 3;
    }

    protected Bot[] getPlayers() {
        return new Bot[] {
                new Cecconen(),
                new ExampleBot(),
                new SuperMan(),
                new NahkaFagottiBot(),
        };
    }

    protected IndianpokerCommentator[] getCommentators() {
        return new IndianpokerCommentator[] { new TextToSpeech(), new StdOutCommentator() };
    }
    
    private String localHost() {
        try {
            return InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }
    }

    private void pause(int seconds) {
        try {
            Thread.sleep(seconds * 1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private void startClient(final Bot bot, final int clientId) {
        start(new Runnable() {
            public void run() {
                int port = SERVER_PORT + clientId;
                String name = bot.getClass().getSimpleName() + clientId;
                PokerClient.join(localHost(), SERVER_PORT, name, bot, localHost(), port);
            }
        });
    }

    private void startServer(final int serverPort) {
        start(new Runnable() {
            public void run() {
                Server server = new Server();
                Connector connector = new XmlRpcConnector(localHost(), serverPort);
                connector.connectTo(server);
                connector.start();
                ServerUi ui = new SystemOutUI(server, getCommentators());
                ui.start();
            }
        });
    }

    private void start(Runnable runnable) {
        new Thread(runnable).start();
    }
}
