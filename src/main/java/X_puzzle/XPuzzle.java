package X_puzzle;

import java.util.ArrayList;
import java.util.PriorityQueue;

import static X_puzzle.XPuzzleController.heuristicToSolveRow;
import static X_puzzle.XPuzzleController.manhattanDist;

public class XPuzzle implements Comparable<XPuzzle>{
    public int gridDim;
    public int[][] grid;
    public int moveCount;
    public ArrayList<String> moves = new ArrayList<>();

    public XPuzzle(int gridDim) {
        this.gridDim = gridDim;
        grid = new int[gridDim][gridDim];
        moveCount = 0;
    }

    public XPuzzle(XPuzzle xp, boolean reduce) {
        moveCount = 0;
        moves = xp.moves;
        if (reduce) {
            if (xp.grid[0].length < xp.grid.length) {
                grid = new int[xp.grid.length-1][xp.grid[0].length];
                for (int i = 1; i < xp.grid.length; i++) {
                    for (int x = 0; x<xp.grid[0].length; x++) {
                        grid[i-1][x] = xp.grid[i][x];
                    }
                }
            } else {
                grid = new int[xp.grid.length][xp.grid[0].length-1];
                for (int i = 0; i < xp.grid.length; i++) {
                    for (int x = 1; x<xp.grid[0].length; x++) {
                        grid[i][x-1] = xp.grid[i][x];
                    }
                }
            }
        }
        System.out.println(this);
    }

    public XPuzzle(int[][] grid, int moveCount, ArrayList<String> moves) {
        gridDim = grid.length;
        this.grid = grid;
        this.moveCount = moveCount;
        this.moves = moves;
        //System.out.println(this);
    }

    public int[] convert(int[][] grid) {
        int[] converted = new int[gridDim*gridDim];
        for (int i = 0; i<grid.length; i++) {
            for (int x = 0; x<grid[i].length; x++) {
                converted[gridDim*i + x] = grid[i][x];
            }
        }
        return converted;
    }

    public int[][] convert(int[] grid) {
        int[][] converted = new int[gridDim][gridDim];
        for (int i = 0; i<grid.length; i++) {
            converted[i/gridDim][i%gridDim] = grid[i];
        }
        return converted;
    }

    public String toString() {
        String answer = "";
        for (int i = 0; i<grid.length; i++) {
            for (int x = 0; x<grid[i].length; x++) {
                answer+=grid[i][x]+" ";
            }
            answer+="\n";
        }
        return answer;
    }

    @Override
    public int compareTo(XPuzzle xp) {
        //return this.moveCount + manhattanDist(this) - xp.moveCount - manhattanDist(xp);
        return this.moveCount + heuristicToSolveRow(this) - xp.moveCount - heuristicToSolveRow(xp);
    }

    public int hashCode() {
        int prime = 31;
        int result = 1;
        for (int i = 0; i<grid.length; i++) {
            for (int x = 0; x<grid[i].length; x++) {
                result = result*prime+grid[i][x];
            }
        }
        return result;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof XPuzzle)) {
            return false;
        }
        XPuzzle xp = (XPuzzle)o;
        if (xp.grid.length != grid.length || xp.grid[0].length != grid[0].length) {
            return false;
        }
        for (int i = 0; i<grid.length; i++) {
            for (int x = 0; x<grid[i].length; x++) {
                if (xp.grid[i][x] != grid[i][x]) {
                    return false;
                }
            }
        }
        return true;
    }
}
