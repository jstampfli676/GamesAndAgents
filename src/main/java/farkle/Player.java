package farkle;

import java.util.ArrayList;
import java.util.Collections;

public class Player {
    int bankedScore;
    int curScore;
    ArrayList<Die> savedDice;
    ArrayList<Die> remainingDice;
    static int TOTAL_PLAYERS=1;
    int playerNumber;

    public Player() {
        playerNumber = TOTAL_PLAYERS;
        TOTAL_PLAYERS++;
        remainingDice = new ArrayList<Die>();
        fillDice();
        savedDice = new ArrayList<Die>();
    }

    public ArrayList<Die> rollDice() {
        for (Die d : remainingDice) {
            d.roll();
        }
        return remainingDice;
    }

    public boolean saveDice(ArrayList<Die> toSave) {//removing all instances of the die
        ArrayList<Die> temp = new ArrayList<>();
        for (Die d : toSave) {
            if (!remainingDice.contains(d)) {
                //System.out.println(toSave);
                for (Die f : temp) {
                    remainingDice.add(f);
                }
                sortRemainingDice();
                return false;
            }
            temp.add(d);
            remainingDice.remove(d);
        }
        /*for (Die d : toSave) {
            savedDice.add(d);
            remainingDice.remove(d);
        }*/
        return true;
    }

    public boolean bankPoints() {
        bankedScore+=curScore;
        curScore = 0;
        if (bankedScore>=10000) {
            return true;
        }
        return false;
    }

    public void savePoints(int points) {
        curScore+=points;
    }

    public void reset() {
        fillDice();
        savedDice.clear();
        curScore = 0;
    }

    public void fillDice() {
        remainingDice.clear();
        for (int i = 0; i<6; i++) {
            remainingDice.add(new Die(1));
        }
    }

    public void sortRemainingDice() {
        Collections.sort(remainingDice);
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Player)) {
            return false;
        }
        Player p = (Player) o;
        return p.playerNumber == this.playerNumber;
    }

    public String toString() {
        return "Player "+playerNumber+" has "+bankedScore+" points.";
    }

    public ArrayList<Die> getRemainingDice() {
        return remainingDice;
    }

    public ArrayList<Die> getSavedDice() {
        return savedDice;
    }

    public int getCurPoints() {
        return curScore;
    }
}
