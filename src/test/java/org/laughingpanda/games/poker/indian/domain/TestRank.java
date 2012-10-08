package org.laughingpanda.games.poker.indian.domain;

import junitx.extensions.EqualsHashCodeTestCase;

/**
 * @author Paolo Perrotta
 */
public class TestRank extends EqualsHashCodeTestCase {

    public TestRank(String name) {
        super(name);
    }

    public void testRanksPrintOutNicely() {
        assertEquals("A", new Rank(1).toString());
        assertEquals("K", new Rank(13).toString());
        assertEquals("Q", new Rank(12).toString());
        assertEquals("J", new Rank(11).toString());
        assertEquals("T", new Rank(10).toString());
        for (int r = 2; r < 10; r++) {
            assertEquals(String.valueOf(r), new Rank(r).toString());
        }
    }

    public void testRanksPrintInSpokenFormat() {
        assertSpokenAs("5", 5);
        assertSpokenAs("6", 6);
        assertSpokenAs("10", 10);
        assertSpokenAs("Jack", 11);
        assertSpokenAs("Queen", 12);
        assertSpokenAs("King", 13);
        assertSpokenAs("Ace", 1);
    }

    public void testEachRankHasAValue() {
        assertEquals(2, new Rank(2).value());
        assertEquals(13, new Rank(13).value());
    }

    public void testTheValueOfAnAceIs14() {
        assertEquals(14, new Rank(1).value());
    }
    
    private void assertSpokenAs(String expected, int rank) {
        assertEquals(expected, new Rank(rank).toSpokenString());
    }

    @Override
    protected Object createInstance() {
        return new Rank(1);
    }

    @Override
    protected Object createNotEqualInstance() {
        return new Rank(2);
    }
}
