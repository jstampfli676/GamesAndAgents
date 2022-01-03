package trap_the_cat;

import java.util.ArrayList;
import java.util.Arrays;

public class TrapCatSimple {
    private SimpleHex[][] map;
    private final int size = 11;
    private static final ArrayList<Integer> allowedMoves = new ArrayList<>(Arrays.asList(10, 11, 1, -1, -11, -12));
    private int turnCount;


    public TrapCatSimple() {
        map = new SimpleHex[size][size];
        resetMap();
    }

    public void resetMap() {
        turnCount = 0;
        for (int i = 0; i<map.length; i++) {
            for (int j = 0; j<map[i].length; j++) {
                double random = Math.random();
                map[i][j] = new SimpleHex(size*i+j);

                if (size*i+j == 60) {
                    map[i][j].setCat(true);
                } else if (random <= 0.2) {
                    map[i][j].block();
                }
            }
        }
    }

    public ArrayList<double[]> generateCatMoves() {
        int x = -1;
        int y = -1;
        int offset = 0;
        for (int i = 0; i<map.length; i++) {
            for (int j = 0; j<map[i].length; j++) {
                if (map[i][j].containsCat()) {
                    x = i;
                    y = j;
                    if (i % 2 == 1) {
                        offset = 2;
                    }
                    break;
                    //System.out.println("found cat "+ x + " "+y+" "+offset);
                }
            }
        }

        ArrayList<double[]> answer = new ArrayList<>();
        for (Integer i : allowedMoves) {
            SimpleHex[][] copy = copyMap(map);
            copy[x][y].setCat(false);
            int deltaX;
            int deltaY;
            if (i==10 || i==-12) {
                deltaX = (i+offset)/size;
                deltaY = (i+offset)%size;
            } else {
                deltaX = i/size;
                deltaY = i%size;
            }
            if (y+deltaY>=size) {
                deltaX+=1;
                deltaY-=size;
            }
            if (y+deltaY<0) {
                deltaX-=1;
                deltaY+=size;
            }
            try {
                if (!copy[x+deltaX][y+deltaY].isBlocked()) {
                    copy[x+deltaX][y+deltaY].setCat(true);
                    answer.add(convertMapToInput(copy));
                }
            } catch (IndexOutOfBoundsException e) {
                System.out.println("move: "+i);
                System.out.println("x values: "+x+" "+deltaX);
                System.out.println("y values: "+y+" "+deltaY);
                System.out.println();
            }
        }
        return answer;
    }

    public ArrayList<double[]> generateTrapMoves() {
        ArrayList<double[]> answer = new ArrayList<>();
        for (int i = 0; i<map.length; i++) {
            for (int j = 0; j<map[i].length; j++) {
                if (!map[i][j].isBlocked() && !map[i][j].containsCat()) {
                    SimpleHex[][] copy = copyMap(map);
                    copy[i][j].block();
                    answer.add(convertMapToInput(copy));
                }
            }
        }
        return answer;
    }

    public void updateState(double[] input) {
        this.map = convertInputToMap(input);
        turnCount++;
    }

    public void updateState(SimpleHex[][] map) {
        this.map = copyMap(map);
        turnCount++;
    }

    public int[] isOver() {//need to return the turn count before resetting so should save turn count in temp variable
        int tempTurnCount=this.turnCount;
        for (int i = 0; i<map.length; i++) {
            for (int j = 0; j<map[i].length; j++) {
                if (map[i][j].containsCat()) {
                    //check if cat won
                    int id = map[i][j].getId();
                    if (i==0 || j==0 || j==10 || i==10) {
                        //System.out.println("The cat has reached the edge of the map and won");
                        resetMap();
                        return new int[]{2, tempTurnCount};
                    }
                    //check if player won
                    if (generateCatMoves().size() == 0) {
                        //System.out.println("The cat has no moves so the trapper wins");
                        resetMap();
                        return new int[]{1,tempTurnCount};
                    }
                    return new int[]{0, tempTurnCount};
                }
            }
        }
        return new int[]{0, tempTurnCount};
    }

    public String toString() {
        String answer = "";
        for (int i = 0; i<map.length; i++) {
            if (i%2==1) {
                answer+=" ";
            }
            for (int j = 0; j<map[i].length; j++) {
                if (map[i][j].isBlocked()) {
                    answer+="[B]";
                } else if (map[i][j].containsCat()) {
                    answer+="[C]";
                } else {
                    answer+="[ ]";
                }
            }
            answer+="\n";
        }
        return answer;
    }

    private SimpleHex[][] copyMap(SimpleHex[][] map) {
        SimpleHex[][] copy = new SimpleHex[size][size];
        for (int i = 0; i<map.length; i++) {
            for (int j = 0; j<map[i].length; j++) {
                copy[i][j] = new SimpleHex(map[i][j]);
            }
        }
        return copy;
    }

    private double[] convertMapToInput(SimpleHex[][] map) {
        double[] answer = new double[size*size];
        for (int i = 0; i<map.length; i++) {
            for (int j = 0; j<map.length; j++) {
                if (map[i][j].isBlocked()) {
                    answer[i*size+j] = -1;
                } else {
                    if (map[i][j].containsCat()) {
                        answer[i*size+j] = 1;
                    } else {
                        answer[i*size+j] = 0;
                    }
                }
            }
        }
        return answer;
    }

    private SimpleHex[][] convertInputToMap(double[] input) {
        SimpleHex[][] answer = new SimpleHex[size][size];
        for (int i = 0; i<input.length; i++) {
            int x = i/size;
            int y = i%size;
            if (input[i]==-1) {
                answer[x][y] = new SimpleHex(true, false, i);
            } else if (input[i]==1) {
                answer[x][y] = new SimpleHex(false, true, i);
            } else {
                answer[x][y] = new SimpleHex(false, false, i);
            }
        }
        return answer;
    }

    public int size() {
        return this.size;
    }
}
