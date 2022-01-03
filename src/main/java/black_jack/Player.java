package black_jack;

import cards.Card;

import java.util.ArrayList;

public class Player {
    public ArrayList<ArrayList<Card>> myHands;
    public ArrayList<Double> myBets;
    private double myMoney;
    private int uniqueId;
    private static int idCount = 0;
    public ArrayList<Integer> finishedHands;

    public Player(double myMoney) {
        myHands = new ArrayList<>();
        myBets = new ArrayList<>();
        finishedHands = new ArrayList<>();
        this.myMoney = myMoney;
        this.uniqueId = idCount;
        idCount++;
    }

    public boolean finishHand(int handIndex) {
        finishedHands.add(handIndex);
        if (finishedHands.size() >= myHands.size()) {
            return true;
        }
        return false;
    }

    public void setMoney(double money) {
        myMoney = money;
        //System.out.println(myMoney);
    }

    public double getMoney() {
        return myMoney;
    }

    public int getUniqueId() {
        return uniqueId;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof Player)) {
            return false;
        }
        Player p = (Player) o;
        return p.uniqueId == this.uniqueId;
    }
}
