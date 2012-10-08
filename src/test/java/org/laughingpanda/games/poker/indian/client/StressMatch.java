package org.laughingpanda.games.poker.indian.client;

import org.laughingpanda.games.poker.indian.client.examples.Cecconen;
import org.laughingpanda.games.poker.indian.client.examples.ExampleBot;
import org.laughingpanda.games.poker.indian.client.examples.NahkaFagottiBot;
import org.laughingpanda.games.poker.indian.client.examples.SuperMan;
import org.laughingpanda.games.poker.indian.server.IndianpokerCommentator;
import org.laughingpanda.games.poker.indian.server.StdOutCommentator;

/**
 * A stress test of the game. Uses many players, and no voice comments (for speed).
 * 
 * @author Paolo Perrotta
 */
public class StressMatch extends DemoMatch {

    public static void main(String[] args) throws Exception {
        DemoMatch match = new StressMatch();
        match.run();
    }

    @Override
    protected int getServerStartPause() {
        return 1;
    }

    @Override
    protected int getPlayerJoinPause() {
        return 0;
    }
    
    @Override
    protected Bot[] getPlayers() {
        return new Bot[] {
                new Cecconen(),
                new ExampleBot(),
                new SuperMan(),
                new NahkaFagottiBot(),
                new Cecconen(),
                new ExampleBot(),
                new SuperMan(),
                new NahkaFagottiBot(),
                new Cecconen(),
                new ExampleBot(),
                new SuperMan(),
                new NahkaFagottiBot(),
                new Cecconen(),
                new ExampleBot(),
                new SuperMan(),
        };
    }

    @Override
    protected IndianpokerCommentator[] getCommentators() {
        return new IndianpokerCommentator[] { new StdOutCommentator() };
    }
}
