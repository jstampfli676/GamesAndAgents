package black_jack;

import cards.Card;
import cards.DeckOfCards;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

import static black_jack.AIController.maxDoubles;
import static black_jack.BlackJackController.calcTotal;

public class RLAI {
    public static Map<PlayerDealerTotals, Double> hitQValues = new HashMap<>();
    public static Map<PlayerDealerTotals, Double> stickQValues = new HashMap<>();
    public static Map<PlayerDealerTotals, Double> doubleQValues = new HashMap<>();
    public Map<PlayerDealerTotals, Integer> hitTimes = new HashMap<>();
    public Map<PlayerDealerTotals, Integer> stickTimes = new HashMap<>();
    public Map<PlayerDealerTotals, Integer> doubleTimes = new HashMap<>();
    public static Map<PlayerDealerTotals, String> actions = new HashMap<>();
    public static Map<PlayerDealerTotals, String> correctActions = new HashMap<>();
    private double learningRate;
    private double noise;
    private long epoch;

    public RLAI(double learningRate, double noise, long epoch, boolean training) {
        this.learningRate = learningRate;
        this.noise = noise;
        this.epoch = epoch;
        if (training) {
            for (int i = 1; i<=10; i++) {
                for (int x = 2; x<=21; x++) {
                    PlayerDealerTotals curTotal = new PlayerDealerTotals(x, i);
                    //hitQValues.put(curTotal, 0.0);
                    //stickQValues.put(curTotal, 0.0);
                    //doubleQValues.put(curTotal, 0.0);
                    hitTimes.put(curTotal, 1000000);
                    stickTimes.put(curTotal, 1000000);
                    doubleTimes.put(curTotal, 1000000);
                }
            }//initialise q values to 0

            try {
                File myObj = new File("qValues.txt");
                Scanner myReader = new Scanner(myObj);
                for (int i = 1; i<=10; i++) {
                    for (int x = 2; x<=21; x++) {
                        PlayerDealerTotals curTotal = new PlayerDealerTotals(x, i);
                        double hitValue = 0.0;
                        double stickValue = 0.0;
                        double doubleValue = 0.0;
                        for (int z = 0; z<3; z++) {
                            if (z==0) {
                                hitValue = myReader.nextDouble();
                            } else if (z==1) {
                                stickValue = myReader.nextDouble();
                            } else {
                                doubleValue = myReader.nextDouble();
                            }
                        }
                        hitQValues.put(curTotal, hitValue);
                        stickQValues.put(curTotal, stickValue);
                        doubleQValues.put(curTotal, doubleValue);
                    }
                }
            } catch (FileNotFoundException e) {
                System.out.println(e);
            }


            trainQValues();
        } else {
            try {
                File myObj = new File("qValues.txt");
                Scanner myReader = new Scanner(myObj);
                for (int i = 1; i<=10; i++) {
                    for (int x = 2; x<=21; x++) {
                        PlayerDealerTotals curTotal = new PlayerDealerTotals(x, i);
                        double hitValue = 0.0;
                        double stickValue = 0.0;
                        double doubleValue = 0.0;
                        for (int z = 0; z<3; z++) {
                            if (z==0) {
                                hitValue = myReader.nextDouble();
                            } else if (z==1) {
                                stickValue = myReader.nextDouble();
                            } else {
                                doubleValue = myReader.nextDouble();
                            }
                        }
                        hitQValues.put(curTotal, hitValue);
                        stickQValues.put(curTotal, stickValue);
                        doubleQValues.put(curTotal, doubleValue);
                    }
                }
            } catch (FileNotFoundException e) {
                System.out.println(e);
            }
        }
        //initializing correct actions
        correctActions.put(new PlayerDealerTotals(8, 2), "hit");
        correctActions.put(new PlayerDealerTotals(8, 3), "hit");
        correctActions.put(new PlayerDealerTotals(8, 4), "hit");
        correctActions.put(new PlayerDealerTotals(8, 5), "hit");
        correctActions.put(new PlayerDealerTotals(8, 6), "hit");
        correctActions.put(new PlayerDealerTotals(8, 7), "hit");
        correctActions.put(new PlayerDealerTotals(8, 8), "hit");
        correctActions.put(new PlayerDealerTotals(8, 9), "hit");
        correctActions.put(new PlayerDealerTotals(8, 10), "hit");
        correctActions.put(new PlayerDealerTotals(8, 1), "hit");
        correctActions.put(new PlayerDealerTotals(9, 2), "hit");
        correctActions.put(new PlayerDealerTotals(9, 3), "double");
        correctActions.put(new PlayerDealerTotals(9, 4), "double");
        correctActions.put(new PlayerDealerTotals(9, 5), "double");
        correctActions.put(new PlayerDealerTotals(9, 6), "double");
        correctActions.put(new PlayerDealerTotals(9, 7), "hit");
        correctActions.put(new PlayerDealerTotals(9, 8), "hit");
        correctActions.put(new PlayerDealerTotals(9, 9), "hit");
        correctActions.put(new PlayerDealerTotals(9, 10), "hit");
        correctActions.put(new PlayerDealerTotals(9, 1), "hit");
        correctActions.put(new PlayerDealerTotals(10, 2), "double");
        correctActions.put(new PlayerDealerTotals(10, 3), "double");
        correctActions.put(new PlayerDealerTotals(10, 4), "double");
        correctActions.put(new PlayerDealerTotals(10, 5), "double");
        correctActions.put(new PlayerDealerTotals(10, 6), "double");
        correctActions.put(new PlayerDealerTotals(10, 7), "double");
        correctActions.put(new PlayerDealerTotals(10, 8), "double");
        correctActions.put(new PlayerDealerTotals(10, 9), "double");
        correctActions.put(new PlayerDealerTotals(10, 10), "hit");
        correctActions.put(new PlayerDealerTotals(10, 1), "hit");
        correctActions.put(new PlayerDealerTotals(11, 2), "double");
        correctActions.put(new PlayerDealerTotals(11, 3), "double");
        correctActions.put(new PlayerDealerTotals(11, 4), "double");
        correctActions.put(new PlayerDealerTotals(11, 5), "double");
        correctActions.put(new PlayerDealerTotals(11, 6), "double");
        correctActions.put(new PlayerDealerTotals(11, 7), "double");
        correctActions.put(new PlayerDealerTotals(11, 8), "double");
        correctActions.put(new PlayerDealerTotals(11, 9), "double");
        correctActions.put(new PlayerDealerTotals(11, 10), "double");
        correctActions.put(new PlayerDealerTotals(11, 1), "double");
        correctActions.put(new PlayerDealerTotals(12, 2), "hit");
        correctActions.put(new PlayerDealerTotals(12, 3), "hit");
        correctActions.put(new PlayerDealerTotals(12, 4), "stand");
        correctActions.put(new PlayerDealerTotals(12, 5), "stand");
        correctActions.put(new PlayerDealerTotals(12, 6), "stand");
        correctActions.put(new PlayerDealerTotals(12, 7), "hit");
        correctActions.put(new PlayerDealerTotals(12, 8), "hit");
        correctActions.put(new PlayerDealerTotals(12, 9), "hit");
        correctActions.put(new PlayerDealerTotals(12, 10), "hit");
        correctActions.put(new PlayerDealerTotals(12, 1), "hit");
        correctActions.put(new PlayerDealerTotals(13, 2), "stand");
        correctActions.put(new PlayerDealerTotals(13, 3), "stand");
        correctActions.put(new PlayerDealerTotals(13, 4), "stand");
        correctActions.put(new PlayerDealerTotals(13, 5), "stand");
        correctActions.put(new PlayerDealerTotals(13, 6), "stand");
        correctActions.put(new PlayerDealerTotals(13, 7), "hit");
        correctActions.put(new PlayerDealerTotals(13, 8), "hit");
        correctActions.put(new PlayerDealerTotals(13, 9), "hit");
        correctActions.put(new PlayerDealerTotals(13, 10), "hit");
        correctActions.put(new PlayerDealerTotals(13, 1), "hit");
        correctActions.put(new PlayerDealerTotals(14, 2), "stand");
        correctActions.put(new PlayerDealerTotals(14, 3), "stand");
        correctActions.put(new PlayerDealerTotals(14, 4), "stand");
        correctActions.put(new PlayerDealerTotals(14, 5), "stand");
        correctActions.put(new PlayerDealerTotals(14, 6), "stand");
        correctActions.put(new PlayerDealerTotals(14, 7), "hit");
        correctActions.put(new PlayerDealerTotals(14, 8), "hit");
        correctActions.put(new PlayerDealerTotals(14, 9), "hit");
        correctActions.put(new PlayerDealerTotals(14, 10), "hit");
        correctActions.put(new PlayerDealerTotals(14, 1), "hit");
        correctActions.put(new PlayerDealerTotals(15, 2), "stand");
        correctActions.put(new PlayerDealerTotals(15, 3), "stand");
        correctActions.put(new PlayerDealerTotals(15, 4), "stand");
        correctActions.put(new PlayerDealerTotals(15, 5), "stand");
        correctActions.put(new PlayerDealerTotals(15, 6), "stand");
        correctActions.put(new PlayerDealerTotals(15, 7), "hit");
        correctActions.put(new PlayerDealerTotals(15, 8), "hit");
        correctActions.put(new PlayerDealerTotals(15, 9), "hit");
        correctActions.put(new PlayerDealerTotals(15, 10), "hit");
        correctActions.put(new PlayerDealerTotals(15, 1), "hit");
        correctActions.put(new PlayerDealerTotals(16, 2), "stand");
        correctActions.put(new PlayerDealerTotals(16, 3), "stand");
        correctActions.put(new PlayerDealerTotals(16, 4), "stand");
        correctActions.put(new PlayerDealerTotals(16, 5), "stand");
        correctActions.put(new PlayerDealerTotals(16, 6), "stand");
        correctActions.put(new PlayerDealerTotals(16, 7), "hit");
        correctActions.put(new PlayerDealerTotals(16, 8), "hit");
        correctActions.put(new PlayerDealerTotals(16, 9), "hit");
        correctActions.put(new PlayerDealerTotals(16, 10), "hit");
        correctActions.put(new PlayerDealerTotals(16, 1), "hit");
        correctActions.put(new PlayerDealerTotals(17, 2), "stand");
        correctActions.put(new PlayerDealerTotals(17, 3), "stand");
        correctActions.put(new PlayerDealerTotals(17, 4), "stand");
        correctActions.put(new PlayerDealerTotals(17, 5), "stand");
        correctActions.put(new PlayerDealerTotals(17, 6), "stand");
        correctActions.put(new PlayerDealerTotals(17, 7), "stand");
        correctActions.put(new PlayerDealerTotals(17, 8), "stand");
        correctActions.put(new PlayerDealerTotals(17, 9), "stand");
        correctActions.put(new PlayerDealerTotals(17, 10), "stand");
        correctActions.put(new PlayerDealerTotals(17, 1), "stand");
    }

    private void trainQValues() {
        MiniBlackJack mbj = new MiniBlackJack();
        double discountRate = 1;
        for (int i = 0; i < epoch; i++) {
            mbj.playGame();//need to get the list of actions the player dealer totals for each action, and the reward
            for (int x = 0; x < mbj.botInfo.size(); x++) {
                PlayerDealerTotals pdt = mbj.botInfo.get(x);
                if (pdt.getPlayerTotal() < 0) {
                    continue;
                }
                //System.out.println(pdt);
                if (mbj.botActions.get(x).equals("H")) {
                    hitQValues.put(pdt, (hitTimes.get(pdt) * hitQValues.get(pdt) + mbj.trainingResult)/(hitTimes.get(pdt)+1));
                    hitTimes.put(pdt, hitTimes.get(pdt)+1);
                } else if (mbj.botActions.get(x).equals("S")) {
                    stickQValues.put(pdt, (stickTimes.get(pdt) * stickQValues.get(pdt) + mbj.trainingResult)/(stickTimes.get(pdt)+1));
                    stickTimes.put(pdt, stickTimes.get(pdt)+1);
                } else {
                    doubleQValues.put(pdt, (doubleTimes.get(pdt) * doubleQValues.get(pdt) + mbj.trainingResult)/(doubleTimes.get(pdt)+1));
                    doubleTimes.put(pdt, doubleTimes.get(pdt)+1);
                }//monte carlo every time

                /*if (x+1<mbj.botInfo.size()) {
                    PlayerDealerTotals nextPDT = mbj.botInfo.get(x+1);
                    hitQValues.put(pdt, (1-learningRate)*hitQValues.get(pdt)+(learningRate*(discountRate* maxDoubles(hitQValues.get(nextPDT), stickQValues.get(nextPDT), doubleQValues.get(nextPDT)))));
                } else {
                    if (mbj.botActions.get(x).equals("H")) {
                        hitQValues.put(pdt, (1-learningRate) * hitQValues.get(pdt) + learningRate*discountRate*mbj.trainingResult);
                    } else if (mbj.botActions.get(x).equals("S")) {
                        stickQValues.put(pdt, (1-learningRate) * stickQValues.get(pdt) + learningRate*discountRate*mbj.trainingResult);
                    } else {
                        doubleQValues.put(pdt, (1-learningRate) * doubleQValues.get(pdt) + learningRate*discountRate*mbj.trainingResult);
                    }
                }*/
                //q learning, for some reason awful for this use case
            }
        }
    }

    private String chooseAction(double h, double s, double d) {
        if (h>=d && h>=s) {
            //System.out.println("h is best");
            return chooseOption("H", "S", "D", noise);
        }
        if (s>=d && s>=h) {
            return chooseOption("S", "D", "H", noise);
        } else {
            return chooseOption("D", "H", "S", noise);
        }
    }

    private String chooseOption(String best, String one, String two, double probability) {
        double choice = Math.random();
        if (choice < probability/2) {
            //System.out.println("one");
            return one;
        }
        if (choice < probability) {
            //System.out.println("two");
            return two;
        }
        return best;
    }

    private class MiniBlackJack {
        private Player bot;
        private Player dealer;
        private DeckOfCards deck;
        public ArrayList<String> botActions = new ArrayList<String>();
        public ArrayList<PlayerDealerTotals> botInfo = new ArrayList<>();
        public double trainingResult;

        public MiniBlackJack() {
            bot = new Player(100);
            dealer = new Player(100);
            deck = new DeckOfCards();
        }

        public void playGame() {
            botActions.clear();
            botInfo.clear();
            bot.myHands.clear();
            dealer.myHands.clear();
            deal(bot);
            deal(dealer);
            int doubled = takeTurn();
            performDealerTurn();
            int endBotTotal = calcTotal(bot.myHands.get(0), true);
            int endDealerTotal = calcTotal(dealer.myHands.get(0), true);
            calcTrainingResult(endBotTotal, endDealerTotal, doubled);
            //System.out.println(botActions+" "+botInfo+" "+trainingResult+" "+endBotTotal+" "+endDealerTotal);
        }

        private int takeTurn() {
            while (true) {
                int dealerShowing = dealer.myHands.get(0).get(0).getValue();
                if (dealerShowing == 14) {
                    dealerShowing = 1;
                } else if (dealerShowing > 10) {
                    dealerShowing = 10;
                }
                PlayerDealerTotals pdt = new PlayerDealerTotals(calcTotal(bot.myHands.get(0), true), dealerShowing);
                botInfo.add(pdt);
                double hValue = hitQValues.get(pdt);
                double sValue = stickQValues.get(pdt);
                double dValue = doubleQValues.get(pdt);
                String action = chooseAction(hValue, sValue, dValue);
                botActions.add(action);
                //System.out.println(botInfo+" "+botActions);
                if (action.equals("H")) {
                    ArrayList<Card> newHand = bot.myHands.get(0);
                    newHand.add(deck.getRandomCard());
                    bot.myHands.set(0, newHand);
                    if (checkBust(bot)) {
                        //System.out.println("busted");
                        return 1;
                    }
                } else if (action.equals("S")) {
                    return 1;
                } else {
                    ArrayList<Card> newHand = bot.myHands.get(0);
                    newHand.add(deck.getRandomCard());
                    bot.myHands.set(0, newHand);
                    return 2;
                }
            }
        }

        private boolean checkBust(Player p) {
            if (calcTotal(p.myHands.get(0), true) == -1) {
                return true;
            }
            return false;
        }

        private void calcTrainingResult(int bot, int dealer, int doubled) {
            if (bot < 0) {
                trainingResult = -1 * doubled;
            } else if (bot == dealer) {
                trainingResult = 0;
            } else if (bot == 21) {
                trainingResult = 1.5 * doubled;
            } else if (bot > dealer) {
                trainingResult = 1 * doubled;
            } else if (bot < dealer) {
                trainingResult = -1 * doubled;
            }
        }

        private void performDealerTurn() {
            int curTotal = calcTotal(dealer.myHands.get(0), true);
            while (curTotal < 17 && curTotal > 0) {
                dealer.myHands.get(0).add(deck.getRandomCard());
                curTotal = calcTotal(dealer.myHands.get(0), true);
            }
        }

        private void deal(Player p) {
            ArrayList<Card> tempHand = new ArrayList<Card>();
            tempHand.add(deck.getRandomCard());
            tempHand.add(deck.getRandomCard());
            p.myHands.add(tempHand);
        }
    }

    public static void main(String[] args) {
        RLAI ai = new RLAI(.001, 0.067/*.067*/, 10000000, true);
        //System.out.println("hits" + ai.hitQValues);
        //System.out.println(ai.hitTimes);
        //System.out.println("sticks" + ai.stickQValues);
        //System.out.println(ai.stickTimes);
        //System.out.println("double" + ai.doubleQValues);
        //System.out.println(ai.doubleTimes);
        double numRight = 0;
        double totalNum = 0;
        ArrayList<PlayerDealerTotals> wrongAnswers = new ArrayList<>();
        for (int i = 1; i<=10; i++) {
            for (int x = 8; x<=17; x++) {
                PlayerDealerTotals curTotal = new PlayerDealerTotals(x, i);
                double hValue = hitQValues.get(curTotal);
                double sValue = stickQValues.get(curTotal);
                double dValue = doubleQValues.get(curTotal);
                if (hValue > sValue && hValue > dValue) {
                    actions.put(curTotal, "hit");
                } else if (hValue < sValue && sValue > dValue) {
                    actions.put(curTotal, "stand");
                } else {
                    actions.put(curTotal, "double");
                }
                if (actions.get(curTotal).equals(correctActions.get(curTotal))) {
                    numRight++;
                } else {
                    wrongAnswers.add(curTotal);
                }
                totalNum++;
            }
        }
        System.out.println(actions);
        System.out.println(numRight/totalNum);
        for (PlayerDealerTotals pdt : wrongAnswers) {
            System.out.println(pdt+" "+hitQValues.get(pdt)+" "+doubleQValues.get(pdt)+" "+stickQValues.get(pdt)+actions.get(pdt)+" "+correctActions.get(pdt));
        }

        try {
            File delete = new File("qValues.txt");
            delete.delete();
            FileWriter myWriter = new FileWriter("qValues.txt");
            for (int i = 1; i<=10; i++) {
                for (int x = 2; x<=21; x++) {
                    PlayerDealerTotals curTotal = new PlayerDealerTotals(x, i);
                    myWriter.write(String.valueOf(hitQValues.get(curTotal))+"\n");
                    myWriter.write(String.valueOf(stickQValues.get(curTotal))+"\n");
                    myWriter.write(String.valueOf(doubleQValues.get(curTotal))+"\n");
                }
            }
            myWriter.close();
        } catch (IOException e) {
            System.out.println(e);
        }//write new data values to the file necessary to save training data
    }
}
