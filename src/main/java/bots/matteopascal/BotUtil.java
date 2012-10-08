package bots.matteopascal;

public class BotUtil {

	public static int numericValue(String cardName) {
		String faceValue = cardName.substring(0, 1);
		if ("T".equals(faceValue)) return 10;
		if ("J".equals(faceValue)) return 11;
		if ("Q".equals(faceValue)) return 12;
		if ("K".equals(faceValue)) return 13;
		if ("A".equals(faceValue)) return 14;
		return Integer.parseInt(faceValue);
	}

}
