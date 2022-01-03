package cards;

import cards.Card;

import java.util.ArrayList;

public class DeckOfCards {
    public static ArrayList<Card> fullDeck;
    public int shuffleLimit;
    public ArrayList<Card> curDeck;

    public DeckOfCards() {
        this(15);
    }

    public DeckOfCards (int shuffleLimit) {
        fullDeck = new ArrayList<>();
        for(int i = 0; i < 4; i++) {
            for (int x = 2; x <= 14; x++) {
                fullDeck.add(new Card(i, x));
            }
        }
        curDeck = deepCopy(fullDeck);
        this.shuffleLimit = shuffleLimit;
    }

    public Card getRandomCard() {
        int randomInd = (int) (Math.random() * curDeck.size());
        Card randomCard = curDeck.get(randomInd);
        curDeck.remove(randomInd);
        checkShuffleLimit();
        return randomCard;
    }

    public void shuffleDiscardsIntoDeck() {
        curDeck.clear();
        curDeck = deepCopy(fullDeck);
    }

    private void checkShuffleLimit() {
        if (curDeck.size() <= shuffleLimit) {
            shuffleDiscardsIntoDeck();
        }
    }

    private ArrayList<Card> deepCopy(ArrayList<Card> oldDeck) {
        ArrayList<Card> newDeck = new ArrayList<Card>();
        for (Card c : oldDeck) {
            newDeck.add(new Card(c));
        }
        return newDeck;
    }
}
