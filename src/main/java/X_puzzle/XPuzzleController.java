package X_puzzle;

import java.util.*;

public class XPuzzleController {

    static int gridDim;

    public XPuzzle goalState;
    public XPuzzle curGoalState;

    public XPuzzleController(XPuzzle startNode, boolean solve) {
        gridDim = startNode.gridDim;
        int[][] goalGrid = new int[gridDim][gridDim];
        for (int i = 0; i<gridDim; i++) {
            for (int x = 0; x<gridDim; x++) {
                goalGrid[i][x] = i*gridDim + x + 1;
            }
        }
        goalGrid[gridDim-1][gridDim-1]=0;
        goalState = new XPuzzle(goalGrid, 0, new ArrayList<String>());
        //System.out.println(goalState);
        if (solve) {
            rowSolverAStar(startNode);
            //AStar(startNode);
        }
    }

    public ArrayList<String> AStar (XPuzzle startNode) {
        int nodesExpanded = 0;
        PriorityQueue<XPuzzle> queue = new PriorityQueue<>();
        Set<XPuzzle> visited = new HashSet();
        queue.add(startNode);
        while (!queue.isEmpty()) {
            //System.out.println(visited.size()+" "+queue.size());
            XPuzzle curNode = queue.poll();
            if (!visited.contains(curNode)) {
                nodesExpanded++;
                visited.add(curNode);
                if (curNode.equals(goalState)) {
                    System.out.println(curNode.moves);
                    System.out.println(curNode.moves.size());
                    System.out.println("Expanded "+nodesExpanded+" nodes");
                    return curNode.moves;
                }
                for (XPuzzle nextNode : potentialStates(curNode)) {
                    if (!visited.contains(nextNode)) {
                        queue.add(nextNode);
                    }
                }
            }
        }
        System.out.println("awefowh");
        return null;
    }

    public ArrayList<String> rowSolverAStar (XPuzzle startNode) {
        if (startNode.grid.length == 1 && startNode.grid[0].length == 1) {
            System.out.println(startNode.moves);
            return startNode.moves;
        }
        //System.out.println(goalState);
        int nodesExpanded = 0;
        PriorityQueue<XPuzzle> queue = new PriorityQueue<>();
        Set<XPuzzle> visited = new HashSet();
        queue.add(startNode);
        while (!queue.isEmpty()) {
            //System.out.println(visited.size()+" "+queue.size());
            XPuzzle curNode = queue.poll();
            if (!visited.contains(curNode)) {
                nodesExpanded++;
                visited.add(curNode);
                //System.out.println(visited.size());
                //System.out.println(curNode);
                //System.out.println(queue);
                /*if (curNode.equals(goalState)) {
                    System.out.println(curNode.moves);
                    System.out.println(curNode.moves.size());
                    System.out.println("Exapnded "+nodesExpanded+" nodes");
                    return curNode.moves;
                }*/
                boolean done = true;
                //System.out.println(curNode.grid[0].length);
                if (curNode.grid[0].length < curNode.grid.length) {
                    for (int i = 0; i<curNode.grid[0].length; i++) {
                        //System.out.println(goalState.grid[gridDim-curNode.grid.length][gridDim - curNode.grid[0].length + i]);
                        if (curNode.grid[0][i] != goalState.grid[gridDim-curNode.grid.length][gridDim - curNode.grid[0].length + i]) {
                            done = false;
                            break;
                        }
                    }
                } else {
                    for (int i = 0; i<curNode.grid.length; i++) {
                        if (curNode.grid[i][0] != goalState.grid[gridDim-curNode.grid.length + i][gridDim - curNode.grid[0].length]) {
                            done = false;
                            break;
                        }
                    }
                }
                if (done) {
                    //System.out.println(curNode.moves);
                    System.out.print(curNode);
                    System.out.println("Nodes: " + nodesExpanded+"\n");
                    /*ArrayList<String> temp = new ArrayList<>(rowSolverAStar(new XPuzzle(curNode, true)));
                    for (String s : curNode.moves) {
                        temp.add(s);
                    }
                    return temp;*/
                    return rowSolverAStar(new XPuzzle(curNode, true));
                }
                for (XPuzzle nextNode : potentialStates(curNode)) {
                    if (!visited.contains(nextNode)) {
                        queue.add(nextNode);
                    }
                }
            }
        }
        System.out.println("awefowh");
        //System.out.println(visited);
        //System.out.println(visited.size());
        return null;
    }

    public static int manhattanDist(XPuzzle xp) {//actually nillsons sequence score
        int gridDim = xp.gridDim;
        int answer = 0;
        for (int i = 0; i<gridDim; i++) {
            for (int x = 0; x<gridDim; x++) {
                if (xp.grid[i][x]!=0 && !(i==gridDim-1 && x==gridDim-1)) {
                    if (xp.grid[i+(x+1)/gridDim][(x+1)%gridDim] != xp.grid[i][x]+1) {
                        answer+=6;
                    }
                } else if (i==gridDim-1 && x==gridDim-1) {
                    if (xp.grid[i][x]!=0) {
                        answer+=3;
                    }
                }
                if (xp.grid[i][x]==0) {
                    answer+=Math.abs(gridDim-1-i);
                    answer+=Math.abs(gridDim-1-x);
                } else {
                    int correctI = (xp.grid[i][x]-1)/gridDim;
                    int correctX = (xp.grid[i][x]-1)%gridDim;
                    answer+=Math.abs(correctI-i);
                    answer+=Math.abs(correctX-x);
                }
            }
        }
        return answer;
    }

    public static int heuristicToSolveRow(XPuzzle xp) {
        ArrayList<Integer> matter = new ArrayList<>();
        Map<Integer, Integer> matterCoords = new HashMap<>();
        int zeroX = 0;
        int zeroY = 0;
        int answer = 0;
        if (xp.grid[0].length < xp.grid.length) {
            for (int i = 0; i<xp.grid[0].length; i++) {
                matter.add(1 + gridDim - xp.grid[0].length + i + gridDim*(gridDim-xp.grid.length));
            }
        } else {
            for (int i = 0; i<xp.grid.length; i++) {
                matter.add(1 + gridDim - xp.grid[0].length + gridDim*(gridDim-xp.grid.length + i));
            }
        }
        //System.out.println(matter);
        for (int i = 0; i<xp.grid.length; i++) {
            for (int x = 0; x<xp.grid[i].length; x++) {
                if (matter.contains(xp.grid[i][x])) {
                    answer += Math.abs(xp.grid[i][x]/gridDim - i);
                    answer += Math.abs(xp.grid[i][x]%gridDim - x);
                    matterCoords.put(xp.grid[i][x], i*gridDim + x);
                } else if (xp.grid[i][x]==0) {
                    //should add the metric to devise the distance from the space to where it should be
                    zeroX = i;
                    zeroY = x;
                }
            }
        }

        int maxDist = -1;
        for (Integer k : matterCoords.keySet()) {
            int curDist = Math.abs(matterCoords.get(k)/gridDim - zeroX) + Math.abs(matterCoords.get(k)%gridDim - zeroY);
            if (curDist > maxDist) {
                maxDist = curDist;
            }
        }
        answer += 2*maxDist;
        return answer;
    }

    private ArrayList<XPuzzle> potentialStates(XPuzzle xp) {
        int xPos = 0;
        int yPos = 0;
        ArrayList<XPuzzle> answer = new ArrayList<>();
        for (int i = 0; i<xp.grid.length; i++) {
            for (int x = 0; x<xp.grid[i].length; x++) {
                if (xp.grid[i][x]==0) {
                    xPos = i;
                    yPos = x;
                }
            }
        }
        //check all directional changes for legality

        if (xPos+1<xp.grid.length) {
            ArrayList<String> tempMoves = deepCopy(xp.moves);
            int[][] tempGrid = deepCopy(xp.grid);
            tempGrid[xPos][yPos] = tempGrid[xPos+1][yPos];
            tempGrid[xPos+1][yPos] = 0;
            tempMoves.add("down");
            answer.add(new XPuzzle(tempGrid, xp.moveCount+1, tempMoves));
        }
        if (xPos - 1>=0) {
            ArrayList<String> tempMoves = deepCopy(xp.moves);
            int[][] tempGrid = deepCopy(xp.grid);
            tempGrid[xPos][yPos] = tempGrid[xPos-1][yPos];
            tempGrid[xPos-1][yPos] = 0;
            tempMoves.add("up");
            answer.add(new XPuzzle(tempGrid, xp.moveCount+1, tempMoves));
        }
        if (yPos + 1<xp.grid[0].length) {
            ArrayList<String> tempMoves = deepCopy(xp.moves);
            int[][] tempGrid = deepCopy(xp.grid);
            tempGrid[xPos][yPos] = tempGrid[xPos][yPos+1];
            tempGrid[xPos][yPos+1] = 0;
            tempMoves.add("right");
            answer.add(new XPuzzle(tempGrid, xp.moveCount+1, tempMoves));
        }
        if (yPos - 1>=0) {
            ArrayList<String> tempMoves = deepCopy(xp.moves);
            int[][] tempGrid = deepCopy(xp.grid);
            tempGrid[xPos][yPos] = tempGrid[xPos][yPos-1];
            tempGrid[xPos][yPos-1] = 0;
            tempMoves.add("left");
            answer.add(new XPuzzle(tempGrid, xp.moveCount+1, tempMoves));
        }
        return answer;
    }

    private int[][] deepCopy(int[][] oldGrid) {
        int[][] newGrid = new int[oldGrid.length][oldGrid[0].length];
        for (int i = 0; i< oldGrid.length; i++) {
            for (int x = 0; x<oldGrid[i].length; x++) {
                newGrid[i][x] = oldGrid[i][x];
            }
        }
        return newGrid;
    }

    private ArrayList<String> deepCopy(ArrayList<String> oldMoves) {
        ArrayList<String> newMoves = new ArrayList<>();
        for (String s : oldMoves) {
            newMoves.add(new String(s));
        }
        return newMoves;
    }
}
