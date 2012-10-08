package bots.ourbot;

import java.util.HashMap;

import org.laughingpanda.games.poker.indian.client.AbstractBot;

public class Bot extends AbstractBot {
    static int count = 0;

    private HashMap<String, String> _cards;

    @Override
    public String action(int priceToCall, int minimumRaise,
            int potSize) {
        count++;
        if (higherThan8(_cards))
            return "fold";

        if (count % 2 != 0)
            return "call";
        else
            return "raise by " + Integer.toString(minimumRaise);
    }

    private boolean higherThan8(HashMap<String, String> _cards2) {
        if (_cards2 == null)
            return false;

        for (String card : _cards2.values()) {
            if (card == "9")
                return true;
            if (card == "10")
                return true;
            if (card == "T")
                return true;
            if (card == "J")
                return true;
            if (card == "Q")
                return true;
            if (card == "K")
                return true;
            if (card == "A")
                return true;
        }
        return false;
    }

    public void handStarts(String string,
            HashMap<String, Integer> blinds,
            HashMap<String, String> cards,
            HashMap<String, Integer> stacks) {
        _cards = cards;
    }
}
