package bots.cbot;

import java.util.Hashtable;
import java.util.Iterator;

import org.laughingpanda.games.poker.indian.client.AbstractBot;

public class Bot extends AbstractBot {
    private String[] CARDS = new String[] { "2", "3", "4", "5", "6",
            "7", "8", "9", "T", "J", "Q", "K", "A" };

    private int amountOfChips = 0, highestCard = 0;

    public String action(int priceToCall, int minimumRaise,
            int potSize) {
        return "raise by " + minimumRaise;
    }

    public void tournamentStarts(int initialStack, String players[]) {
        amountOfChips = initialStack;
    }

    public int getNumberOfChips() {
        return amountOfChips;
    }

    public void won(int amount) {
        amountOfChips += amount;
    }

    public void paidBlind(int amount) {
        amountOfChips -= amount;
    }

    public void handStarts(String arg0,
            Hashtable<String, Integer> arg1,
            Hashtable<String, String> cards,
            Hashtable<String, Integer> arg3) {
        // TODO Auto-generated method stub
        super.handStarts(arg0, arg1, cards, arg3);
        highestCard = 0;
        for (Iterator<String> i = cards.values().iterator(); i
                .hasNext();) {
            int card = findCardIndex(i.next().substring(0, 1));
        }
    }

    public int findCardIndex(String card) {
        for (int i = 0; i < CARDS.length; ++i) {
            if (card.equals(CARDS[i]))
                return i;
        }

        return 0;
    }

    public void performed(String arg0) {
        // TODO Auto-generated method stub
        super.performed(arg0);
        System.out.println(arg0);
    }

    public void playerActed(String player, String action) {
        super.playerActed(player, action);
        System.out.println(player + " > " + action);
    }

    public void showCard(String arg0) {
        // TODO Auto-generated method stub
        super.showCard(arg0);
    }

    public String getHighestCard() {
        return CARDS[highestCard];
    }
}
