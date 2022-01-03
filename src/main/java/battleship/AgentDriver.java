package battleship;

import neural_network.NeuralNet;

import java.util.ArrayList;
import java.util.List;

public class AgentDriver {

    public static double playGame(BattleshipEnv env, NeuralNet agent) {
        double reward = 0;
        while (!env.gameOver()) {
            double[] curState = env.convertToAgentLikelihoodInput();
            printState(curState, 10);
            /*ArrayList<double[]> potentialStates = new ArrayList<>();
            for (int i = 0; i<curState.length; i++) {
                if (curState[i]==0) {
                    //System.out.println("adding potential state");
                    double[] tempState = deepCopy(curState);
                    tempState[i] = -1;
                    //printState(tempState);
                    potentialStates.add(tempState);
                }
            }*/
            //System.out.println("number of potential states "+potentialStates.size());

            /*double max = -Integer.MAX_VALUE;
            int maxIndex = -1;
            for (int i = 0; i<potentialStates.size(); i++) {
                //System.out.println("calculating future state values");
                Double curVal = agent.predict(potentialStates.get(i)).get(0);
                if (curVal>max) {
                    max = curVal;
                    maxIndex = i;
                    //System.out.println("new best future state "+maxIndex);
                }
            }*/
            ArrayList<Integer> legalActions = new ArrayList<>();
            for (int i = 0; i<curState.length; i++) {
                if (curState[i]!=0 && curState[i]!=1) {
                    legalActions.add(i);
                }
            }
            List<Double> scores = agent.predict(curState);
            reward+=env.takeAction(findBestAction(scores, legalActions));

            /*try {
                double[] futureState = potentialStates.get(maxIndex);
                for (int i = 0; i<futureState.length; i++) {
                    if (futureState[i]==-1) {
                        reward+=env.takeAction(i);
                    }
                }
            } catch (IndexOutOfBoundsException e) {
                System.out.println(env);
                e.printStackTrace();
                System.exit(0);
            }*/
        }
        return reward;
    }

    private static int findBestAction(List<Double> scores, ArrayList<Integer> legalActions) {
        double max = -Integer.MAX_VALUE;
        int maxIndex = -1;
        for (Integer i : legalActions) {
            if (scores.get(i)>max) {
                maxIndex = i;
                max = scores.get(i);
            }
        }
        return maxIndex;
    }

    public static void train(BattleshipEnv env, ArrayList<NeuralNet> agents, int epochs) {
        for (int a = 0; a<epochs; a++) {
            System.out.println(a+" "+agents.size());

            ArrayList<Double> agentScores = new ArrayList<>();
            for (int b = 0; b<agents.size(); b++) {
                agentScores.add(playGame(env, agents.get(b)));
            }

            double avgScore = avg(agentScores);
            double stdScore = std(agentScores, avgScore);
            double maxScore = max(agentScores);
            double minScore = min(agentScores);
            System.out.println(agentScores);
            agentScores = normalize(agentScores);
            System.out.println(agentScores);

            agents = trap_the_cat.AgentDriver.genPopulation(agentScores, agents);

            System.out.println("the average score is "+avgScore);
            System.out.println("the standard deviation is "+stdScore);
            System.out.println("the best score is "+maxScore);
            System.out.println("the worst score is "+minScore);

            if (a%10==0) {//saving the agents every 10 epochs
                for (int c = 0; c<agents.size(); c++) {
                    agents.get(c).saveAgent("agents_64_128_64_32_16_1/battleshipAgents/battleship_agent"+c+".txt");
                }
            }
        }
        for (int c = 0; c<agents.size(); c++) {
            agents.get(c).saveAgent("agents_64_128_64_32_16_1/battleshipAgents/battleship_agent"+c+".txt");
        }
    }

    private static double min(ArrayList<Double> agentScores) {
        double min = Double.MAX_VALUE;
        for (Double d : agentScores) {
            if (d<min) {
                min = d;
            }
        }
        return min;
    }

    private static double max(ArrayList<Double> agentScores) {
        double max = -Double.MAX_VALUE;
        for (Double d : agentScores) {
            if (d>max) {
                max = d;
            }
        }
        return max;
    }

    private static double std(ArrayList<Double> scores, double avg) {
        double var = 0;
        for (Double d : scores) {
            var+=Math.pow(d-avg, 2);
        }
        var = var/scores.size();
        return Math.sqrt(var);
    }

    private static double avg(ArrayList<Double> scores) {
        double sum = 0;
        for (int i = 0; i<scores.size(); i++) {
            sum+=scores.get(i);
        }
        return sum/scores.size();
    }

    private static ArrayList<Double> normalize(ArrayList<Double> agentScores) {
        double toAdd = 0;
        for (int i = 0; i<agentScores.size(); i++) {
            if (agentScores.get(i)<toAdd) {
                toAdd = agentScores.get(i);
            }
        }
        toAdd-=1;
        toAdd*=-1;
        for (int i = 0; i<agentScores.size(); i++) {
            agentScores.set(i, agentScores.get(i)+toAdd);
            if (agentScores.get(i)!=1) {
                agentScores.set(i, Math.pow(agentScores.get(i), 2));
            }
        }
        return agentScores;
    }

    private static ArrayList<NeuralNet> deepCopy(ArrayList<NeuralNet> agents) {
        ArrayList<NeuralNet> newAgents = new ArrayList<>();
        for (int i = 0; i<agents.size(); i++) {
            newAgents.add(new NeuralNet(agents.get(i)));
        }
        return newAgents;
    }

    private static double[] deepCopy(double[] state) {
        double[] newState = new double[state.length];
        for (int i = 0; i<state.length; i++) {
            newState[i] = state[i];
        }
        return newState;
    }

    private static void printState(double[] state, int sideLength) {
        for (int i = 0; i<sideLength; i++) {
            for (int x = 0; x<sideLength; x++) {
                System.out.print(state[i*sideLength+x]+" ");
            }
            System.out.println();
        }
        System.out.println();
    }

    public static void main(String[] args) {
        BattleshipEnv env = new BattleshipEnv();
        NeuralNet agent = new NeuralNet(new int[]{100,128,256,128,100});
        /*ArrayList<NeuralNet> agents = new ArrayList<>();
        for (int i = 0; i<1000; i++) {
            agents.add(new NeuralNet("agents_64_128_64_32_16_1/battleshipAgents/battleship_agent"+i+".txt", new int[]{64,128,64,32,16,1}));
            //need to scrap this those agents are all the same and useless at this point
        }
        train(env, agents, 1000);*/
        playGame(env, agent);
        /*int i = 0;
        while (i<10000) {
            playGame(env, agent);
            if (i%100==0) {
                System.out.println(i);
            }
            i++;
        }*/

    }
}
