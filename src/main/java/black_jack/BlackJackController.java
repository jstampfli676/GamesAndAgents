package black_jack;

import cards.Card;
import cards.DeckOfCards;

import java.util.ArrayList;
import java.util.Scanner;
//need to make a player class and then change around the implementation and add in splitting hands and insurance bets
public class BlackJackController {
    private DeckOfCards deck;
    /*private ArrayList<Card> dealerHand;
    private ArrayList<ArrayList<Card>> playersHands;
    private ArrayList<Double> playersMoney;
    private ArrayList<Double> curBets;*/
    private Player dealer;
    private ArrayList<Card> dealerHand;
    private ArrayList<Player> players;//dealer is the last player
    private ArrayList<AIController> ai;
    private int numPlayers;

    public BlackJackController(int numPlayers, int numAI, double startingMoney) {
        this.numPlayers = numPlayers;
        players = new ArrayList<>();
        ai = new ArrayList<>();
        deck = new DeckOfCards();
        for (int i = 0; i<numPlayers+1; i++) {
            players.add(new Player(startingMoney));
        }
        for (int i = 0; i<numAI; i++) {
            ai.add(new AIController(startingMoney));
        }
        dealer = players.get(players.size()-1);
        players.remove(dealer);
    }

    public void playRound() {
        Scanner input = new Scanner(System.in);
        boolean keepPlaying = true;
        while (keepPlaying) {
            deck.shuffleDiscardsIntoDeck();
            for (Player p : players) {
                p.myBets.clear();
                p.myHands.clear();
                p.finishedHands.clear();
                double curMoney = p.getMoney();
                double curBet = -1;
                while (curBet <= 0 || curBet > curMoney) {
                    System.out.println("Player " + p.getUniqueId() + ", you have " + curMoney + " money. What is your bet?");
                    curBet = input.nextDouble();
                }
                p.myBets.add(curBet);
                curMoney -= curBet;
                p.setMoney(curMoney);
            }
            dealer.myHands.clear();
            deal();
            dealerHand = dealer.myHands.get(0);
            int doneCount = 0;
            boolean firstDeal = true;
            ArrayList<Integer> donePlayers = new ArrayList<>();
            while (doneCount < numPlayers) {
                for (int i = 0; i < numPlayers; i++) {
                    if (!donePlayers.contains(i) && performTurn(players.get(i), firstDeal)) {
                        doneCount++;
                        donePlayers.add(i);
                    }
                }
                firstDeal = false;
            }
            performAITurns();
            performDealerTurn();
            for (int i = 0; i<players.size(); i++) {
                calcWinnings(players.get(i));
                if (players.get(i).getMoney() == 0) {
                    players.remove(i);
                    i--;
                    numPlayers--;
                }
            }
            for (int i = 0; i<ai.size(); i++) {
                calcWinnings(ai.get(i));
                if (ai.get(i).getMoney() == 0) {
                    ai.remove(i);
                    i--;
                }
            }
            if (players.size()==0 && ai.size()==0) {
                keepPlaying = false;
            }
            /*System.out.println("Do you want to play another round (Y/N)?");
            String answer = input.nextLine();
            if (answer.equals("N")) {
                keepPlaying = false;
            }*/
        }
    }

    private void performAITurns() {
        for (AIController aic : ai) {
            aic.myHands.clear();
            aic.myBets.clear();
            boolean alreadySplit = false;
            boolean done = false;
            double curBet = aic.getBetUnit();
            double curMoney = aic.getMoney();
            ArrayList<String> curOptions = new ArrayList<String>();
            if (curBet > curMoney) {
                curBet = curMoney;
            }

            curMoney -= curBet;
            aic.myBets.add(curBet);
            aic.setMoney(curMoney);

            Card card1 = deck.getRandomCard();
            Card card2 = deck.getRandomCard();

            ArrayList<Card> newHand = new ArrayList<Card>();
            newHand.add(card1);
            newHand.add(card2);
            aic.myHands.add(newHand);

            for (int i = 0; i < aic.myHands.size(); i++) {
                curOptions.clear();
                curOptions.add("H");
                curOptions.add("St");
                done = false;
                while (!done) {
                    if (!alreadySplit && card1.isDoubles(card2) && aic.myHands.get(i).size()==2 && curBet <= curMoney) {
                        curOptions.add("Sp");
                    }
                    if (calcTotal(aic.myHands.get(i), false)<=11 && calcTotal(aic.myHands.get(i), false) >= 9 && curBet <= curMoney) {
                        curOptions.add("D");
                    }
                    System.out.println(aic.myHands + " " + curOptions);
                    String decision = aic.makeDecision(curOptions, dealerHand.get(0).getValue(), i);
                    curOptions.remove("Sp");
                    curOptions.remove("D");
                    if (decision.equals("H")) {
                        ArrayList<Card> tempHand = aic.myHands.get(i);
                        tempHand.add(deck.getRandomCard());
                        aic.myHands.set(i, tempHand);
                        if (calcTotal(aic.myHands.get(i), true) < 0) {
                            done = true;
                        }
                    } else if (decision.equals("St")) {
                        done = true;
                    } else if (decision.equals("Sp")) {
                        alreadySplit = true;
                        ArrayList<Card> secondHand = new ArrayList<Card>();
                        secondHand.add(newHand.get(0));
                        secondHand.add(deck.getRandomCard());
                        newHand.remove(0);
                        newHand.add(deck.getRandomCard());
                        aic.myHands.add(secondHand);
                        aic.myHands.set(0, newHand);
                        curMoney -= curBet;
                        aic.setMoney(curMoney);
                        aic.myBets.add(curBet);
                        //i--;
                    } else {
                        //decision is to double
                        curMoney -= curBet;
                        curBet *= 2;
                        aic.setMoney(curMoney);
                        aic.myBets.set(i, curBet);
                        ArrayList<Card> tempHand = aic.myHands.get(i);
                        tempHand.add(deck.getRandomCard());
                        aic.myHands.set(i, tempHand);
                        done = true;
                    }
                }
            }
            System.out.println(aic.getUniqueId()+", "+aic.myHands + " " + aic.myBets + " " + aic.getMoney());
        }
    }



    private void performDealerTurn() {
        int curTotal = calcTotal(dealerHand, true);
        while (curTotal < 17 && curTotal > 0) {
            dealerHand.add(deck.getRandomCard());
            curTotal = calcTotal(dealerHand, true);
        }
        System.out.println(dealerHand + " " + curTotal);
    }

    private void calcWinnings(AIController player) {
        //System.out.println(player.getUniqueId() + " " +player.myHands);
        for (int i = 0; i < player.myHands.size(); i++) {
            int playerTotal = calcTotal(player.myHands.get(i), true);
            int dealerTotal = calcTotal(dealerHand, true);
            double curMoney = player.getMoney();
            double curBet = player.myBets.get(i);
            //System.out.println(player.getUniqueId() + " " + curBet + " " + curMoney + " "+playerTotal+" "+dealerTotal);
            if (playerTotal < 0) {
                continue;
            }
            if (playerTotal == dealerTotal) {
                //System.out.println("a");
                player.setMoney(curMoney + curBet);
            } else if (playerTotal == 21) {
                //System.out.println("b");
                player.setMoney(curMoney + 2.5 * curBet);
            } else if (playerTotal > dealerTotal) {
                //System.out.println("c");
                player.setMoney(curMoney + 2 * curBet);
            }
        }
        System.out.println("AI " + player.getUniqueId() + ", you now have " + player.getMoney() + " money.");
    }

    private void calcWinnings(Player player) {
        //System.out.println(player.getUniqueId() + " " +player.myHands);
        for (int i = 0; i < player.myHands.size(); i++) {
            int playerTotal = calcTotal(player.myHands.get(i), true);
            int dealerTotal = calcTotal(dealerHand, true);
            double curMoney = player.getMoney();
            double curBet = player.myBets.get(i);
            //System.out.println(player.getUniqueId() + " " + curBet + " " + curMoney + " "+playerTotal+" "+dealerTotal);
            if (playerTotal < 0) {
                continue;
            }
            if (playerTotal == dealerTotal) {
                //System.out.println("a");
                player.setMoney(curMoney + curBet);
            } else if (playerTotal == 21) {
                //System.out.println("b");
                player.setMoney(curMoney + 2.5 * curBet);
            } else if (playerTotal > dealerTotal) {
                //System.out.println("c");
                player.setMoney(curMoney + 2 * curBet);
            }
        }
        System.out.println("Player " + player.getUniqueId() + ", you now have " + player.getMoney() + " money.");
    }

    private boolean performTurn(Player player, boolean firstDeal) {
        Scanner input = new Scanner(System.in);
        boolean alreadySplit = false;
        for (int i = 0; i < player.myHands.size(); i++) {
            if (!player.finishedHands.contains(i)) {
                double curMoney = player.getMoney();
                double curBet = player.myBets.get(i);
                ArrayList<Card> curHand = player.myHands.get(i);
                String initialPrint = "Player " + player.getUniqueId() + ", your hand is the ";
                int cardsSum = calcTotal(curHand, false);
                for (Card c : curHand) {
                    initialPrint += c + " ";
                }
                initialPrint += ". The dealer is showing " + String.valueOf(dealerHand.get(0));
                System.out.println(initialPrint);

                //insurance bet should be here but skipping it for now

                if (!alreadySplit && firstDeal && curBet <= curMoney && curHand.get(0).isDoubles(curHand.get(1))) {//can only double on first two cards need to check that
                    System.out.println("Do you want to split (Y/N)?");
                    if (input.nextLine().equals("Y")) {
                        i=-1;
                        ArrayList<Card> newHand = new ArrayList<>();
                        newHand.add(curHand.get(1));
                        curHand.remove(1);
                        newHand.add(deck.getRandomCard());
                        curHand.add(deck.getRandomCard());
                        player.myHands.set(0, curHand);
                        player.myHands.add(newHand);
                        player.myBets.add(curBet);
                        player.setMoney(curMoney-curBet);
                        alreadySplit = true;
                        System.out.println("You now have " + player.getMoney() + " money. " +
                                "Your new hands are " + curHand + ", " + newHand);
                        continue;
                    }
                }

                if (cardsSum <= 11 && cardsSum >= 9 && curBet <= curMoney) {
                    System.out.println("You have " + curMoney +" money and your bet is " +
                            curBet + ". Do you want to double down (Y/N)?");
                    if (input.nextLine().equals("Y") ) {
                        curMoney -= curBet;
                        curBet *= 2;
                        player.myBets.set(i, curBet);
                        player.setMoney(curMoney);
                        player.myHands.get(i).add(deck.getRandomCard());
                        if (player.finishHand(i)) {
                            System.out.println("You now have " + curMoney +" money and your bet is " +
                                    curBet + ". Your new hand is " + player.myHands.get(i));
                            return true;
                        }
                        System.out.println("You now have " + curMoney +" money and your bet is " +
                                curBet + ". Your new hand is " + player.myHands.get(i));
                    }
                }

                System.out.println("Do you want to stick or hit (S/H)?");
                String stickOrHit = input.nextLine();
                if (stickOrHit.equals("S")) {
                    System.out.println("Your total is " + calcTotal(curHand, true));
                    if (player.finishHand(i)) {
                        return true;
                    }
                } else if (stickOrHit.equals("H")) {
                    //System.out.println("in hit");
                    Card newCard = deck.getRandomCard();
                    //System.out.println("got random card");
                    curHand.add(newCard);
                    player.myHands.set(i, curHand);
                    if (calcTotal(curHand, true) == -1) {
                        System.out.println("You drew the " + String.valueOf(newCard) + ". You busted!");
                        if (player.finishHand(i)) {
                            return true;
                        }
                    } else {
                        System.out.println("You drew the " + String.valueOf(newCard) + ".");
                    }
                }
            }
        }
        return false;
    }

    public static int calcTotal(ArrayList<Card> playerHand, boolean end) {
        ArrayList<Card> curHand = playerHand;
        ArrayList<Integer> possibleSums = new ArrayList<>();
        //System.out.println(curHand);
        for (Card c : curHand) {
            //System.out.println("calc total");
            if (c.getValue() == 14) {
                if (possibleSums.size() == 0) {
                    possibleSums.add(1);
                    possibleSums.add(11);
                } else {
                    int limit = possibleSums.size();
                    for (int i = 0; i < limit; i++) {
                        possibleSums.set(i, possibleSums.get(i) + 1);
                        possibleSums.add(possibleSums.get(i) + 10);//only adding 10 because i already added 1
                    }
                }
            } else {
                int newValue;
                if (c.getValue() == 11 || c.getValue() == 12 || c.getValue() == 13) {
                    newValue = 10;
                } else {
                    newValue = c.getValue();
                }
                if (possibleSums.size() == 0) {
                    possibleSums.add(newValue);
                } else {
                    for (int i = 0; i < possibleSums.size(); i++) {
                        possibleSums.set(i, possibleSums.get(i) + newValue);
                    }
                }
            }
            //System.out.println(possibleSums);
        }

        //find max valid score
        if (end) {
            int maxScore = -1;
            for (Integer score : possibleSums) {
                if (score > maxScore && score <= 21) {
                    maxScore = score;
                }
            }
            return maxScore;
        } else {
            for (Integer score : possibleSums) {
                if (score >= 9 && score <= 11) {
                    return score;
                }
            }
        }
        return -1;
    }

    private void deal() {
        for (Player p : players) {
            ArrayList<Card> curHand = new ArrayList<>();
            //for testing
            /*if (p.getUniqueId()==0) {
                Card test1 = new Card(0, 2);
                Card test2 = new Card(1, 2);
                curHand.add(test1);
                curHand.add(test2);
                p.myHands.add(curHand);
                continue;
            }*/

            Card card1 = deck.getRandomCard();
            Card card2 = deck.getRandomCard();
            curHand.add(card1);
            curHand.add(card2);
            p.myHands.add(curHand);
        }
        ArrayList<Card> curHand = new ArrayList<>();
        Card card1 = deck.getRandomCard();
        Card card2 = deck.getRandomCard();
        curHand.add(card1);
        curHand.add(card2);
        dealer.myHands.add(curHand);
    }

    public static void main(String[] args) {
        BlackJackController bjc = new BlackJackController(1, 1, 10);
        bjc.playRound();
    }
}
