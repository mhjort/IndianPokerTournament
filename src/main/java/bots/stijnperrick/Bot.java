package bots.stijnperrick;

import java.util.Hashtable;

import org.laughingpanda.games.poker.indian.client.AbstractBot;

public class Bot extends AbstractBot {
    private int stack;

    private String[] players;

    private Hashtable<String, Integer> blinds;

    private String dealer;

    private Hashtable<String, String> cards;

    private Hashtable<String, String> actions;

    private Hashtable<String, Integer> stacks;

    public Bot() {
        actions = new Hashtable<String, String>();
    }

    public String checkIfAllPlayersFold() {
        String result = "check";
        for (int i = 0; i < players.length; i++) {
            if (actions.get(players[i]) != "fold") {
                result = "";
                break;
            }
        }

        return result;
    }

    public int getMaxStack() {
        int maxStack = 0;
        for (int i = 0; i < players.length; i++) {
            maxStack = Math.max(maxStack, stacks.get(players[i]));
        }

        return maxStack;
    }

    public void playerActed(java.lang.String player,
            java.lang.String action) {
        actions.put(player, action);
    }

    @Override
    public String action(int priceToCall, int minimumRaise,
            int potSize) {
        String result = "raise by " + minimumRaise;
        int raise = 0;

        for (int i = 0; i < players.length; i++) {
            switch (cards.get(players[i]).charAt(0)) {
            case '2':
            case '3':
            case '4':
                raise = 100;
                break;
            case '5':
            case '6':
            case '7':
                raise = 50;
                break;
            case '8':
            case '9':
            case 'T':
                raise = minimumRaise;
                break;
            case 'J':
            case 'Q':
            case 'K':
            case 'A':
                raise = 0;
                break;
            }
            if (raise == 0) {
                break;
            }
        }

        if (raise > 0) {
            raise = Math.max(raise, stack);
            if (getMaxStack() < stack) {
                raise = getMaxStack();
            }

            result = "raise by " + raise;
        } else {
            if (checkIfAllPlayersFold() == "") {
                result = "fold";
            } else {
                result = checkIfAllPlayersFold();
            }
        }

        return result;
    }

    @Override
    public void tournamentStarts(int stack, String[] players) {
        this.stack = stack;
        this.players = players;

    }

    public void handStarts(
            java.lang.String dealer,
            java.util.Hashtable<java.lang.String, java.lang.Integer> blinds,
            java.util.Hashtable<java.lang.String, java.lang.String> cards,
            java.util.Hashtable<java.lang.String, java.lang.Integer> stacks) {
        this.dealer = dealer;
        this.blinds = blinds;
        this.cards = cards;
        this.stacks = stacks;
    }
}
