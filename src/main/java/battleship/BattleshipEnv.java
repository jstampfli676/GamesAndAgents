package battleship;

import java.util.ArrayList;

public class BattleshipEnv {
    private static final int[] shipSizes = new int[]{5,4,3,3,2};
    private static final int gridSize = 10;
    private Tile[] map;
    private Ship[] ships;

    public BattleshipEnv() {
        /*map = new Tile[gridSize*gridSize];
        for (int i = 0; i<map.length; i++) {
            map[i] = new Tile(i);
        }*/
        ships = new Ship[shipSizes.length];
        resetMap();
    }

    public void resetMap() {
        Tile[] newMap = new Tile[gridSize*gridSize];
        for (int i = 0; i<newMap.length; i++) {
            newMap[i] = new Tile(i);
        }
        for (int i = 0; i<shipSizes.length; i++) {
            Ship curShip = new Ship(shipSizes[i]);
            int[] tiles = generateLocation(curShip.getLength(), newMap);

            /*for (int x = 0; x<tiles.length; x++) {
                System.out.print(tiles[x]+" ");
            }
            System.out.println();*/

            curShip.setLocation(tiles);
            ships[i] = curShip;
            for (int x = 0; x<tiles.length; x++) {
                if (!newMap[tiles[x]].putShip(curShip)) {//currently doing this for testing purposes
                    for (int j = 0; j<tiles.length; j++) {
                        System.out.print(tiles[j]+" ");
                    }
                    System.out.println();
                }
            }
        }
        map = newMap;//used to deep copy the new map but i believe that didnt work since the ships werent the same in the map and list
    }

    private int[] generateLocation(int length, Tile[] newMap) {
        int[] tiles = new int[length];

        int x = (int)(Math.random()*gridSize);
        int y = (int)(Math.random()*gridSize);
        int dir = (int)(Math.random()*4);//0 is north, 1 is east, 2 is south, 3 is west
        tiles[0] = gridSize*x+y;
        tiles[1] = -1;

        while (true) {
            switch (dir) {
                case 0:
                    if (x-(length-1)>=0) {
                        //can have ship point north
                        tiles = fillOutTiles(true, -1, tiles);
                    }
                case 1:
                    if (y+(length-1)<gridSize) {
                        //can have ship point east
                        tiles = fillOutTiles(false, 1, tiles);
                    }
                case 2:
                    if (x+(length-1)<gridSize) {
                        //can have ship points south
                        tiles = fillOutTiles(true, 1, tiles);
                    }
                case 3:
                    if (y-(length-1)>=0) {
                        //can have ship point west
                        tiles = fillOutTiles(false, -1, tiles);
                    }
            }

            if (tiles[1]!=-1) {
                if (checkTiles(tiles, newMap)) {
                    return tiles;
                }
            }
            dir = (int)(Math.random()*4);
            x = (int)(Math.random()*gridSize);
            y = (int)(Math.random()*gridSize);
            tiles[0] = gridSize*x+y;
            tiles[1] = -1;
            //System.out.println(x*gridSize+y+" "+dir);
        }
    }

    private boolean checkTiles(int[] tiles, Tile[] newMap) {
        //System.out.println("checking tiles");
        for (int i = 0; i<tiles.length; i++) {
            if (newMap[tiles[i]].containsShip()) {
                return false;
            }
        }
        return true;
    }

    private int[] fillOutTiles(boolean vertical, int augment, int[] tiles) {
        int ns = 0;
        int ew = 0;
        if (vertical) {
            ns = 1;
        } else {
            ew = 1;
        }
        for (int i = 0; i<tiles.length-1; i++) {
            tiles[i+1] = tiles[i]+ns*gridSize*augment+ew*augment;
        }
        return tiles;
    }

    public int takeAction(int tileIndex) {
        map[tileIndex].attack();
        return -1;
    }

    public boolean gameOver() {
        //System.out.println(this);
        for (int i = 0; i<ships.length; i++) {
            if (!ships[i].isSunk()) {
                return false;
            }
        }
        resetMap();
        return true;
    }

    private Tile[] deepCopy(Tile[] map) {
        Tile[] newMap = new Tile[map.length];
        for (int i = 0; i<map.length; i++) {
            newMap[i] = new Tile(map[i]);
        }
        return newMap;
    }

    public double[] convertToAgentBasicInput() {
        double[] answer = new double[map.length];
        for (int i = 0; i<answer.length; i++) {
            Tile curTile = map[i];
            if (!curTile.isHit()) {
                answer[i] = 0;
            } else {
                if (curTile.containsShip()) {
                    if (curTile.getShip().isSunk()) {
                        answer[i]=3;
                    } else {
                        answer[i]=2;
                    }
                } else {
                    answer[i]=-1;
                }
            }
        }
        return answer;
    }

    public double[] convertToAgentLikelihoodInput() {
        double[] answer = new double[map.length];
        ArrayList<Ship> remShips = new ArrayList<>();
        for (int i = 0; i< ships.length; i++) {
            if (!ships[i].isSunk()) {
                remShips.add(ships[i]);
            }
        }

        for (int i = 0; i<answer.length; i++) {
            for (int j = 0; j<remShips.size(); j++) {
                int length = remShips.get(j).getLength();
                int x = i/gridSize;
                int y = i%gridSize;
                if (x-(length-1)>=0) {//can be north
                    answer[i]+=1;
                    answer = fillOutProbs(i, length, true, -1, answer);
                }
                if (y+(length-1)<gridSize) {//can be east
                    answer[i]+=1;
                    answer = fillOutProbs(i, length, false, 1, answer);
                }
                if (x+(length-1)<gridSize) {//can be south
                    answer[i]+=1;
                    answer = fillOutProbs(i, length, true, 1, answer);
                }
                if (y-(length-1)>=0) {//can be west
                    answer[i]+=1;
                    answer = fillOutProbs(i, length, false, -1, answer);
                }
            }
        }

        answer = normalize(answer);

        for (int i = 0; i<answer.length; i++) {
            if (map[i].isHit()) {
                if (map[i].containsShip()) {
                    answer[i] = 1;
                    if (i>=gridSize) {
                        answer[i-gridSize]*=100;
                        answer[i-1]*=100;
                    } else if (i>=1) {
                        answer[i-1]*=100;
                    }
                    if (i+gridSize<gridSize*gridSize) {
                        answer[i+gridSize]*=100;
                        answer[i+1]*=100;
                    } else if (i+1<gridSize*gridSize) {
                        answer[i+1]*=100;
                    }
                } else {
                    answer[i] = 0;
                }
            }
        }
        return answer;
    }

    private double[] normalize(double[] map) {
        double sum = 0;
        for (int i = 0; i<map.length; i++) {
            sum+=map[i];
        }
        for (int i = 0; i<map.length; i++) {
            map[i]/=sum;
        }
        return map;
    }

    private double[] fillOutProbs(int start, int length, boolean vert, int augment, double[] map) {
        int ns = 0;
        int ew = 0;
        if (vert) {
            ns = 1;
        } else {
            ew = 1;
        }

        for (int i = 0; i<length-1; i++) {
            map[start+gridSize*ns*augment+ew*augment]+=1;
        }
        return map;
    }

    //public Tile[] convertToMap(int[] agentInput) {}

    public String toString() {
        String answer = "";
        for (int x = 0; x<gridSize; x++) {
            for (int y = 0; y<gridSize; y++) {
                answer+=String.valueOf(map[x*gridSize+y]);
            }
            answer+="\n";
        }
        answer+="\n";
        return answer;
    }
}
