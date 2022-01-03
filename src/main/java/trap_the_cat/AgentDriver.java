package trap_the_cat;
import neural_network.NeuralNet;

import java.util.ArrayList;

public class AgentDriver {

    //static int turnCount = 0;

    public static double[] playGame(NeuralNet catAgent, NeuralNet trapperAgent, TrapCatSimple env) {//cat score returned first
        //System.out.println(env);
        double[] scores = new double[2];
        int turnCount = 0;
        int winner = 0;
        while (winner == 0) {
            if (turnCount%2==0) {
                ArrayList<double[]> trapperStates = env.generateTrapMoves();
                double maxScore = -2;
                int maxIndex = -1;
                for (int i = 0; i<trapperStates.size(); i++) {
                    double curScore = trapperAgent.predict(trapperStates.get(i)).get(0);
                    if (curScore > maxScore) {
                        maxScore = curScore;
                        maxIndex = i;
                    }
                }
                env.updateState(trapperStates.get(maxIndex));
            } else {
                ArrayList<double[]> catStates = env.generateCatMoves();
                double maxScore = -2;
                int maxIndex = -1;
                for (int i = 0; i<catStates.size(); i++) {
                    double curScore = catAgent.predict(catStates.get(i)).get(0);
                    if (curScore > maxScore) {
                        maxScore = curScore;
                        maxIndex = i;
                    }
                }
                env.updateState(catStates.get(maxIndex));
            }
            //System.out.println(env);
            int[] results = env.isOver();
            winner = results[0];
            turnCount = results[1];
        }
        //System.out.println(turnCount);
        if (winner == 2) {//the cat won
            scores[0] = calcGameScore(true, turnCount);
            scores[1] = calcGameScore(false, turnCount);
            return scores;
        }
        scores[0] = calcGameScore(false, turnCount);
        scores[1] = calcGameScore(true, turnCount);
        return scores;
    }

    private static double calcGameScore(boolean winner, int gameLength) {
        if (winner) {
            return gameLength;
        } else {
            return 1;//might eventually want to prioritize losers who lasted longer
        }
    }

    private static double max(double d1, double d2) {
        return d1>d2?d1:d2;
    }

    public static void trainAgents(ArrayList<NeuralNet> catAgents, ArrayList<NeuralNet> trapperAgents, TrapCatSimple env, int epochs) {
        //ArrayList<Integer> catWinners = new ArrayList<>();
        //ArrayList<Integer> trapperWinners = new ArrayList<>();
        for (int i = 1; i<=epochs; i++) {
            float totalMoveCount = 0;
            System.out.println(i+" "+catAgents.size());
            //catWinners.clear();
            //trapperWinners.clear();
            ArrayList<Double> catScores = new ArrayList<>();
            ArrayList<Double> trapperScores = new ArrayList<>();
            int gamesPlayed = 0;
            while (gamesPlayed<catAgents.size() /*|| catWinners.size()==0 || trapperWinners.size()==0*/) {
                //gamesPlayed = gamesPlayed%catAgents.size();
                double[] scores = playGame(catAgents.get(gamesPlayed), trapperAgents.get(gamesPlayed), env);
                catScores.add(scores[0]);
                trapperScores.add(scores[1]);
                totalMoveCount+=max(scores[0], scores[1]);
                /*if (catWon) {
                    catWinners.add(gamesPlayed);
                } else {
                    trapperWinners.add(gamesPlayed);
                }*/
                env.resetMap();
                gamesPlayed++;
            }

            int catWins = 0;
            for (int f = 0; f<catScores.size(); f++) {
                if (catScores.get(f)!=1) {
                    catWins++;
                }
            }

            //normalize the range of the scores
            catScores = normalize(catScores);
            trapperScores = normalize(trapperScores);

            //generate the new populations
            /*int idealSize = catAgents.size();
            while (catAgents.size()<idealSize*2) {
                int momNetInd;
                int dadNetInd;

                int catDadInd = (int)(Math.random()*catWinners.size());
                int catMomInd = (int)(Math.random()*catWinners.size());

                momNetInd = catWinners.get(catMomInd);
                dadNetInd = catWinners.get(catDadInd);
                catAgents.add(new NeuralNet(catAgents.get(momNetInd), catAgents.get(dadNetInd)));

                int trapperDadInd = (int)(Math.random()*trapperWinners.size());
                int trapperMomInd = (int)(Math.random()*trapperWinners.size());
                momNetInd = trapperWinners.get(trapperMomInd);
                dadNetInd = trapperWinners.get(trapperDadInd);
                trapperAgents.add(new NeuralNet(trapperAgents.get(momNetInd), trapperAgents.get(dadNetInd)));
            }
            while (catAgents.size()>idealSize) {
                catAgents.remove(0);
                trapperAgents.remove(0);
            }*/
            //generate new populations
            ArrayList<NeuralNet> newCats = genPopulation(catScores, catAgents);
            ArrayList<NeuralNet> newTrappers = genPopulation(trapperScores, trapperAgents);
            catAgents.clear();
            trapperAgents.clear();
            for (int t = 0; t<newCats.size(); t++) {
                catAgents.add(new NeuralNet(newCats.get(t)));
                trapperAgents.add(new NeuralNet(newTrappers.get(t)));
                //catAgents.remove(0);
                //trapperAgents.remove(0);
            }

            //print out some stats
            //int totalGamesPlayed = catWinners.size()+trapperWinners.size();
            float catWinPercent = (float)catWins/gamesPlayed;
            float avgMoveCount = totalMoveCount/(float)gamesPlayed;
            System.out.println(catWinPercent+ " " +avgMoveCount);

            if (i%10==0) {//saving the agents every 10 epochs
                for (int z = 0; z<catAgents.size(); z++) {
                    catAgents.get(z).saveAgent("agents_121_96_64_32_16_1/catAgents/cat_agent"+z+".txt");
                    trapperAgents.get(z).saveAgent("agents_121_96_64_32_16_1/trapperAgents/trapper_agent"+z+".txt");
                }
            }
        }
        for (int z = 0; z<catAgents.size(); z++) {
            catAgents.get(z).saveAgent("agents_121_96_64_32_16_1/catAgents/cat_agent"+z+".txt");
            trapperAgents.get(z).saveAgent("agents_121_96_64_32_16_1/trapperAgents/trapper_agent"+z+".txt");
        }
    }

    private static ArrayList<Double> normalize(ArrayList<Double> scores) {
        int sum = 0;
        int winCount = 0;
        for (int i = 0; i<scores.size(); i++) {
            if (scores.get(i)!=1) {
                sum+=scores.get(i);
                winCount++;
            }
        }
        float avg = (float)sum/winCount;
        for (int i = 0; i<scores.size(); i++) {
            if (scores.get(i)!=1) {
                scores.set(i, max(avg-scores.get(i), 3));
            }
        }
        return scores;
    }

    public static ArrayList<NeuralNet> genPopulation(ArrayList<Double> scores, ArrayList<NeuralNet> population) {
        ArrayList<NeuralNet> newPopulation = new ArrayList<>();

        while (newPopulation.size()<population.size()) {
            int[] parentIndices = generateParents(scores, 2);
            newPopulation.add(new NeuralNet(population.get(parentIndices[0]), population.get(parentIndices[1])));
        }

        return newPopulation;
    }

    private static int[] generateParents(ArrayList<Double> scores, int numParents) {
        int[] parentIndices = new int[numParents];
        int sumScores = sumList(scores);
        for (int i = 0; i<numParents; i++) {
            double value = Math.random()*sumScores;
            int index = 0;
            while (value>0) {
                value-=scores.get(index);
                index++;
            }
            parentIndices[i]=index-1;
        }
        return parentIndices;
    }

    private static int sumList(ArrayList<Double> input) {
        int sum = 0;
        for (Double d : input) {
            sum+=d;
        }
        return sum;
    }



    public static void main(String[] args) {
        TrapCatSimple env = new TrapCatSimple();
        /*ArrayList<NeuralNet> catAgents = new ArrayList<>();
        ArrayList<NeuralNet> trapperAgents = new ArrayList<>();
        int numAgents = 1000;
        int[] dims = new int[]{121,96,64,32,16,1};

        for (int i = 0; i<numAgents; i++) {
            String catString = "agents_121_96_64_32_16_1/catAgents/cat_agent"+i+".txt";
            String trapperString = "agents_121_96_64_32_16_1/trapperAgents/trapper_agent"+i+".txt";
            catAgents.add(new NeuralNet(catString, dims));//not currently loading because no networks of this architecture have been saved
            trapperAgents.add(new NeuralNet(trapperString, dims));
        }
        trainAgents(catAgents, trapperAgents, env, 600);*/

        NeuralNet catAgentHuge = new NeuralNet("agents_121_96_64_32_16_1/catAgents/cat_agent20.txt", new int[]{121,96,64,32,16,1});
        NeuralNet trapperAgentHuge = new NeuralNet("agents_121_96_64_32_16_1/trapperAgents/trapper_agent10.txt", new int[]{121,96,64,32,16,1});
        NeuralNet catAgentLarge = new NeuralNet("agents_121_64_28_1/catAgents/cat_agent5.txt", new int[]{121,64,28,1});
        NeuralNet catAgentSmall = new NeuralNet("agents_121_64_1/catAgents/cat_agent5.txt", new int[]{121,64,1});
        NeuralNet trapperAgentLarge = new NeuralNet("agents_121_64_28_1/trapperAgents/trapper_agent5.txt", new int[]{121,64,28,1});
        NeuralNet trapperAgentSmall = new NeuralNet("agents_121_64_1/trapperAgents/trapper_agent5.txt", new int[]{121,64,1});
        /*double[] calWins = new double[3];
        double[] casWins = new double[3];
        double[] talWins = new double[3];
        double[] tasWins = new double[3];
        int iterations = 10000;
        for (int i = 0; i<iterations; i++) {
            if (i%1000==0) {
                System.out.println(i);
            }
            if (i<iterations/2) {
                if (i<iterations/4) {
                    double[] scores = playGame(catAgentLarge, trapperAgentLarge, env);
                    if (scores[0]==1) {
                        talWins[0]+=1;
                        calWins[2]+=scores[1];
                        talWins[2]+=scores[1];
                    } else {
                        calWins[0]+=1;
                        calWins[2]+=scores[0];
                        talWins[2]+=scores[0];
                    }
                } else {
                    double[] scores = playGame(catAgentLarge, trapperAgentSmall, env);
                    if (scores[0]==1) {
                        tasWins[0]+=1;
                        tasWins[2]+=scores[1];
                        calWins[2]+=scores[1];
                    } else {
                        calWins[1]+=1;
                        calWins[2]+=scores[0];
                        tasWins[2]+=scores[0];
                    }
                }
            } else {
                if (i<(3*iterations)/4) {
                    double[] scores = playGame(catAgentSmall, trapperAgentLarge, env);
                    if (scores[0]==1) {
                        talWins[1]+=1;
                        casWins[2]+=scores[1];
                        talWins[2]+=scores[1];
                    } else {
                        casWins[0]+=1;
                        casWins[2]+=scores[0];
                        talWins[2]+=scores[0];
                    }
                } else {
                    double[] scores = playGame(catAgentSmall, trapperAgentSmall, env);
                    if (scores[0]==1) {
                        tasWins[1]+=1;
                        casWins[2]+=scores[1];
                        tasWins[2]+=scores[1];
                    } else {
                        casWins[1]+=1;
                        casWins[2]+=scores[0];
                        tasWins[2]+=scores[0];
                    }
                }
            }
            env.resetMap();
        }

        System.out.println("big cat win percentage vs big trapper= "+4*calWins[0]/iterations);
        System.out.println("big cat win percentage vs small trapper= "+4*calWins[1]/iterations);
        System.out.println("small cat win percentage vs big trapper= "+4*casWins[0]/iterations);
        System.out.println("small cat win percentage vs small trapper= "+4*casWins[1]/iterations);
        System.out.println("big trapper win percentage= "+2*(talWins[0]+talWins[1])/iterations);
        System.out.println("small trapper win percentage= "+2*(tasWins[0]+tasWins[1])/iterations);
        System.out.println("big cat average game moves= "+2*calWins[2]/iterations);
        System.out.println("small cat average game moves= "+2*casWins[2]/iterations);
        System.out.println("big trapper average game moves= "+2*talWins[2]/iterations);
        System.out.println("small trapper average game moves= "+2*tasWins[2]/iterations);*/
        double totalMoves = 0;
        ArrayList<Double> games = new ArrayList<>();
        for (int i = 0; i<500; i++) {
            double[] scores = playGame(catAgentHuge, trapperAgentHuge, env);
            totalMoves+=max(scores[0], scores[1]);
            games.add(max(scores[0], scores[1]));
        }
        System.out.println(totalMoves/500);
        System.out.println(games);
    }
}
