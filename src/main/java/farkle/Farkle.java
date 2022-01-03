package farkle;

import java.util.*;

public class Farkle {
    public Map<DiceSet, Integer> SCORE_MAP;
    private ArrayList<Player> playerList;

    public Farkle(int numPlayers) {
        SCORE_MAP = new HashMap<DiceSet, Integer>();
        SCORE_MAP.put(new DiceSet(Arrays.asList(new Die(1), new Die(2), new Die(3), new Die(4), new Die(5), new Die(6))), 2500);
        SCORE_MAP.put(new DiceSet(Arrays.asList(new Die(1))), 100);
        SCORE_MAP.put(new DiceSet(Arrays.asList(new Die(5))), 50);
        mapKind(3);
        mapKind(4);
        mapKind(5);
        mapKind(6);
        mapThreePair();
        playerList = new ArrayList<Player>();
        for (int i = 0; i<numPlayers; i++) {
            playerList.add(new Player());
        }
    }

    private void mapKind(int n) {
        for (int i = 1; i<=6; i++) {
            List<Die> dice = new ArrayList<Die>();
            for (int x = 0; x<n; x++) {
                dice.add(new Die(i));
            }
            if (n==3) {
                if (i==1) {
                    SCORE_MAP.put(new DiceSet(dice), 1000);
                } else {
                    SCORE_MAP.put(new DiceSet(dice), 100*i);
                }
            } else {
                SCORE_MAP.put(new DiceSet(dice), 1000*(n-3));
            }
        }
    }

    private void mapThreePair() {
        int i = 1;
        int x = 2;
        int y = 3;
        for (i = 1; i<x; i++) {
            for (x = i+1; x<y; x++) {
                for (y = x+1; y<=6; y++) {
                    List<Die> dice = new ArrayList<>();
                    Die iDie = new Die(i);
                    Die xDie = new Die(x);
                    Die yDie = new Die(y);
                    for (int j = 0; j<2; j++) {
                        dice.add(iDie);
                        dice.add(xDie);
                        dice.add(yDie);
                    }
                    SCORE_MAP.put(new DiceSet(dice), 1500);
                }
            }
        }
    }

    public void play() {
        Scanner input = new Scanner(System.in);
        boolean win = false;
        while(true) {
            for (Player p : playerList) {//still need to add hot dice and then clean up the text interface a little
                System.out.println(p + " It's their turn.");
                p.rollDice();
                while (true) {
                    p.sortRemainingDice();
                    if (!calcPossibleSaves(p.getRemainingDice())) {
                        System.out.println("Farkle! "+p.getRemainingDice());
                        break;
                    }
                    boolean possibleSave = false;
                    ArrayList<Die> finalSave = new ArrayList<>();
                    while (!possibleSave) {
                        finalSave.clear();
                        System.out.println(p.getRemainingDice()+" what do you want to save?");
                        String toSave = input.nextLine();
                        char[] toSaveInts = toSave.toCharArray();
                        for (int i = 0; i<toSaveInts.length; i++) {
                            finalSave.add(new Die(Character.getNumericValue(toSaveInts[i])));
                        }
                        //System.out.println(finalSave);
                        possibleSave = p.saveDice(finalSave);//also need to figure out how many points they get
                    }
                    int points = calcPoints(finalSave);
                    p.savePoints(points);
                    if (p.getRemainingDice().size()==0) {
                        System.out.println("Hot Dice!");
                        p.fillDice();
                    }
                    System.out.println("You have "+p.getCurPoints()+" points. Do you want to bank? (Y/N)");
                    String rollAgain = input.nextLine();
                    rollAgain = rollAgain.toUpperCase();
                    if (rollAgain.equals("Y")) {
                        win = p.bankPoints();
                        break;
                    }
                    p.rollDice();
                }
                System.out.println(p);
                if (win) {
                    System.out.print(" They win!");
                    break;
                }
                //reset player
                p.reset();
            }
            if (win) {
                break;
            }
        }
    }

    private boolean calcPossibleSaves(ArrayList<Die> input) {
        if (input.contains(new Die(1))) {
            return true;
        }
        if (input.contains(new Die(5))) {
            return true;
        }
        int[] dieCounts = new int[6];
        for (int i = 0; i < dieCounts.length; i++) {
            dieCounts[i] = 0;
        }
        for (Die d : input) {
            dieCounts[d.getCurValue()-1]+=1;
            if (dieCounts[d.getCurValue()-1]>=3) {
                return true;
            }
        }
        boolean[] threePairFullRun = checkThreePairAndFullRun(dieCounts);
        return threePairFullRun[0] || threePairFullRun[1];
    }

    private int calcPoints(ArrayList<Die> input) {//could check to ensure that every die is adding points and if not not accept the save
        int[] dieCounts = new int[6];
        for (int i = 0; i< dieCounts.length; i++) {
            dieCounts[i] = 0;
        }
        for (Die d : input) {
            dieCounts[d.getCurValue()-1]+=1;
        }
        /*for (int i = 0; i<dieCounts.length; i++) {
            System.out.print(dieCounts[i]+", ");
        }*/
        boolean[] threePairFullRun = checkThreePairAndFullRun(dieCounts);
        if (threePairFullRun[0]) {
            return 1500;
        }
        if (threePairFullRun[1]) {
            return 2500;
        }
        int sum = 0;
        for (int i = 0; i<dieCounts.length; i++) {
            if (i==0) {
                if (dieCounts[i]<3) {
                    sum+=100*dieCounts[i];
                } else {
                    sum+=mapPoints(dieCounts[i], i+1);
                }
            } else if (i==4) {
                if (dieCounts[i]<3) {
                    sum+=50*dieCounts[i];
                } else {
                    sum+=mapPoints(dieCounts[i], i+1);
                }
            } else {
                if (dieCounts[i]>=3) {
                    sum+=mapPoints(dieCounts[i], i+1);
                }
            }
        }
        return sum;
    }

    private int mapPoints(int count, int num) {
        if (num==1) {
            if (count==3) {
                return 1000;
            } else if (count == 4) {
                return 1100;
            } else {
                return (count-3)*1000;
            }
        } else {
            if (count>3) {
                return (count-3)*1000;
            } else if (count==3){
                return num*100;
            }
        }
        System.out.println("Error "+count+", "+num);
        return -1;
    }

    //three pair is the first index and full run is the second
    private boolean[] checkThreePairAndFullRun(int[] dieCounts) {
        boolean[] answer = new boolean[2];
        int numPairs = 0;
        boolean fullRun = true;
        for (int i = 0; i<dieCounts.length; i++) {
            if (dieCounts[i]>=2) {
                numPairs++;
            } else if (dieCounts[i]==0) {
                fullRun = false;
            }
        }
        answer[0] = numPairs>=3;
        answer[1] = fullRun;
        return answer;
    }

    public static void main(String[] args) {
        Farkle f = new Farkle(2);
        f.play();
    }

    private class DiceSet {
        List<Die> dice;

        public DiceSet(List<Die> dice) {
            this.dice = dice;
        }

        private void sortDice() {
            Collections.sort(dice);
        }

        public boolean equals(Object o) {
            if (o==this) {
                return true;
            }
            if (!(o instanceof DiceSet)) {
                return false;
            }
            DiceSet ds = (DiceSet) o;
            if (ds.dice.size()!=this.dice.size()) {
                return false;
            }
            ds.sortDice();
            this.sortDice();
            for (int i = 0; i<this.dice.size(); i++) {
                if (!this.dice.get(i).equals(ds.dice.get(i))) {
                    return false;
                }
            }
            return true;
        }
    }
}
