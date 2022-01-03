package black_jack;

import cards.Card;

import java.util.ArrayList;

import static black_jack.BlackJackController.calcTotal;

public class AIController {
    public ArrayList<ArrayList<Card>> myHands;
    public ArrayList<Double> myBets;
    private double myMoney;
    private double betUnit;
    private int uniqueId;
    private static int idCount = 0;
    public ArrayList<Integer> finishedHands;
    private RLAI rlai;

    public AIController(double myMoney) {
        myHands = new ArrayList<>();
        myBets = new ArrayList<>();
        finishedHands = new ArrayList<>();
        this.myMoney = myMoney;
        this.betUnit = myMoney/10;
        this.uniqueId = idCount;
        idCount++;
        rlai = new RLAI(0,0,0,false);
    }

    public String makeDecision(ArrayList<String> options, int dealerCard, int handIndex) {
        int fixedDealerCard = dealerCard;
        if (fixedDealerCard == 14) {
            fixedDealerCard = 1;
        } else if (fixedDealerCard > 10) {
            fixedDealerCard = 10;
        }

        PlayerDealerTotals curTotals = new PlayerDealerTotals(calcTotal(myHands.get(handIndex), true), fixedDealerCard);
        //System.out.println(curTotals);
        double hitValue = RLAI.hitQValues.get(curTotals);
        double stickValue = RLAI.stickQValues.get(curTotals);
        double doubleValue = RLAI.doubleQValues.get(curTotals);
        double bestOptionValue = -1000000;
        String bestOption = "";
        for (String s : options) {
            if (s.equals("H")) {
                if (hitValue > bestOptionValue) {
                    bestOption = "H";
                    bestOptionValue = hitValue;
                }
            } else if (s.equals("St")) {
                if (stickValue > bestOptionValue) {
                    bestOption = "St";
                    bestOptionValue = stickValue;
                }
            } else if (s.equals("D")) {
                if (doubleValue > bestOptionValue) {
                    bestOption = "D";
                    bestOptionValue = doubleValue;
                }
            } else {
                ArrayList<Card> postSplitHand = new ArrayList<>();
                postSplitHand.add(myHands.get(handIndex).get(0));
                PlayerDealerTotals postSplit = new PlayerDealerTotals(calcTotal(postSplitHand, true), fixedDealerCard);
                //System.out.println("attempting to split " + postSplit);
                if (RLAI.hitQValues.get(postSplit) != null && 2*RLAI.hitQValues.get(postSplit) > bestOptionValue) {
                    bestOption = "Sp";
                    bestOptionValue = 2*RLAI.hitQValues.get(postSplit);
                }
            }
        }
        return bestOption;
    }

    public static double maxDoubles(double one, double two, double three) {
        if (one >= two && two >= three) {
            return one;
        }
        if (two >= one && two >= three) {
            return two;
        }
        return three;
    }
    public static double maxDoubles(double one, double two) {
        if (one > two) {
            return one;
        }
        return two;
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

    public double getBetUnit() {
        return betUnit;
    }

    public int getUniqueId() {
        return uniqueId;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof AIController)) {
            return false;
        }
        AIController p = (AIController) o;
        return p.uniqueId == this.uniqueId;
    }
}
