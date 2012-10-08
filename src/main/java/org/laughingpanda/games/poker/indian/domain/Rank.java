package org.laughingpanda.games.poker.indian.domain;

/**
 * @author Paolo Perrotta
 */
class Rank {

    private final int value;
    
    public Rank(int rank) {
        this.value = (rank == 1 ? 14 : rank);
    }

    public int value() {
        return value;
    }
    
    @Override
    public boolean equals(Object obj) {
        Rank other = (Rank) obj;
        return other != null && value == other.value;
    }

    @Override
    public int hashCode() {
        return value;
    }

    @Override
    public String toString() {
        switch (value) {
        case 10:
            return "T";
        case 11:
            return "J";
        case 12:
            return "Q";
        case 13:
            return "K";
        case 14:
            return "A";
        default:
            return "" + value;
        }
    }
    
    public Object toSpokenString() {
        switch (value) {
        case 11:
            return "Jack";
        case 12:
            return "Queen";
        case 13:
            return "King";
        case 14:
            return "Ace";
        default:
            return "" + value;
        }
    }
}
