package org.laughingpanda.games.poker.indian.server;

/**
 * @author Paolo Perrotta
 */
public class StdOutCommentator implements IndianpokerCommentator {

    public void say(String speech) {
        System.out.println(speech);
    }
}
