package bots.matteopascal;

import java.util.Enumeration;
import java.util.Hashtable;

import org.laughingpanda.games.poker.indian.client.AbstractBot;

public class Bot extends AbstractBot {

	private Hashtable<String, String> cards = new Hashtable<String, String>();

	@Override
	public String action(int priceToCall, int minimumRaise, int potSize) {
		double assessment = getCardsAssessment();
		int raise = (int) Math.round(potSize * 0.2);
		if (assessment <= 0.4) return "raise by " + raise;
		return assessment <= 0.5 ? "call" : "fold";
	}

	Object getCards() {
		return cards;
	}

	public void handStarts(String dealer, Hashtable<String, Integer> blinds, Hashtable<String, String> cards, Hashtable<String, Integer> stacks) {
		this.cards = cards;
	}

	public double getCardsAssessment() {
		double sum = 0.0;
		
		for (Enumeration<String> e = cards.keys(); e.hasMoreElements(); ) {
			String card = cards.get(e.nextElement());
			sum += BotUtil.numericValue(card);
		}
		
		double average = sum / cards.size();
		
		return ((average - 2.0) / 12.0);
	}
}
