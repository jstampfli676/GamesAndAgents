package cards;

import java.util.HashMap;
import java.util.Map;

public class Card {
    private int suit;
    private int value;

    public static final Map<Integer, String> suits = Map.of(0, "Hearts", 1, "Diamonds",
            2, "Spades", 3, "Clubs");
    public static final Map<Integer, String> values = new HashMap<Integer, String>()
    {{
        put(1, "1");
        put(2, "2");
        put(3, "3");
        put(4, "4");
        put(5, "5");
        put(6, "6");
        put(7, "7");
        put(8, "8");
        put(9, "9");
        put(10, "10");
        put(11, "Jack");
        put(12, "Queen");
        put(13, "King");
        put(14, "Ace");
    }};

    public Card(int suit, int value) {
        this.suit = suit;
        this.value = value;
    }

    public Card(Card c) {
        this.suit = c.suit;
        this.value = c.value;
    }

    public int getSuit() {
        return suit;
    }

    public int getValue() {
        return value;
    }

    public boolean isDoubles(Card c) {
        return c.value==this.value;
    }

    public boolean equals(Object o) {
        if (o==this) {
            return true;
        }
        if (!(o instanceof Card)) {
            return false;
        }
        Card c = (Card)o;
        return c.suit == this.suit && c.value == this.value;
    }

    public String toString() {
        return values.get(this.value) + " of " + suits.get(this.suit);
    }
}
