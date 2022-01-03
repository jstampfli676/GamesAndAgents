import java.util.ArrayList;
import java.util.Scanner;

public class PicoFirmiController {
    private int numberLength;
    private int maxNumber;
    private boolean doubles;
    private ArrayList<ArrayList<Integer>> possibleCodes = new ArrayList<>();
    private ArrayList<ArrayList<Integer>> aiGuesses = new ArrayList<>();

    public PicoFirmiController(int numberLength, int maxNumber, boolean doubles) {
        if (!doubles) {
            if (maxNumber +1 < numberLength) {
                maxNumber = numberLength-1;
                System.out.println("max number too small changed to the minimum");
            }
        }
        this.numberLength = numberLength;
        this.maxNumber = maxNumber;
        this.doubles = doubles;
        generateCodes(new ArrayList<Integer>());
        //System.out.println(possibleCodes.size());
    }

    public int playGame(int player, ArrayList<Integer> answer) {
        if (answer == null) {
            answer = generateNumber();
        }
        //System.out.println(answer);
        int numGuesses = 0;
        if (player == 0) {
            System.out.println("The number is "+numberLength+" digits long and consists of numbers between 0 and" +
                    " " + maxNumber+". Contains duplicate numbers: "+doubles);
            Scanner input = new Scanner(System.in);
            while (true) {
                numGuesses++;
                System.out.println("Next Guess: ");
                ArrayList<Integer> guess = new ArrayList<>();
                char[] digits = input.nextLine().toCharArray();
                for (Character digit : digits) {
                    guess.add(Character.getNumericValue(digit));
                }
                String evaluation = evaluateGuess(guess, answer, true);
                System.out.println(evaluation);
                if (evaluation.equals("correct!")) {
                    System.out.println(answer);
                    break;
                }
            }
        } else if (player == 1) {
            numGuesses = worseCaseAlgorithm(answer);
            System.out.println(aiGuesses);
        } else if (player == 2) {
            int numAiGuesses = worseCaseAlgorithm(answer);
            int numPlayerGuesses = playGame(0, answer);
            if (numAiGuesses == numPlayerGuesses) {
                System.out.println("Tie");
            } else if (numAiGuesses<numPlayerGuesses) {
                System.out.println("Ai Wins!");
            } else {
                System.out.println("You Win!");
            }
            System.out.println(aiGuesses);
        }
        return numGuesses;
    }

    private int worseCaseAlgorithm(ArrayList<Integer> answer) {
        aiGuesses.clear();
        int numGuesses = 1;
        ArrayList<ArrayList<Integer>> tempPossibleCodes = deepCopyDouble(possibleCodes);
        //System.out.println(tempPossibleCodes.size());
        ArrayList<Integer> guess = tempPossibleCodes.get((int) (Math.random() * tempPossibleCodes.size()));
        while (true) {
            String evaluation = evaluateGuess(guess, answer, false);
            //System.out.println(evaluation);
            if (evaluation.equals("correct!")) {
                //System.out.println(guess + "was the answer " + answer);
                aiGuesses.add(guess);
                return numGuesses;
            }
            //remove false codes
            for (int i = 0; i < tempPossibleCodes.size(); i++) {
                if (!evaluation.equals(evaluateGuess(guess, tempPossibleCodes.get(i), false))) {
                    tempPossibleCodes.remove(i);
                    i--;
                }
            }
            //System.out.println(tempPossibleCodes.size());

            //make new guess will be random for now
            /*System.out.println(guess);
            System.out.println(evaluation);*/
            aiGuesses.add(guess);
            guess = tempPossibleCodes.get((int) (Math.random() * tempPossibleCodes.size()));
            numGuesses++;
        }
    }

    private void generateCodes(ArrayList<Integer> cur) {
        if (cur.size() == numberLength) {
            possibleCodes.add(cur);
            return;
        }
        for (int i = 0; i <= maxNumber; i++) {
            if (!doubles) {
                if (!cur.contains(i)) {
                    ArrayList<Integer> newList = deepCopy(cur);
                    newList.add(i);
                    generateCodes(newList);
                }
            } else {
                ArrayList<Integer> newList = deepCopy(cur);
                newList.add(i);
                generateCodes(newList);
            }
        }
    }

    private ArrayList<Integer> deepCopy(ArrayList<Integer> oldList) {
        ArrayList<Integer> newList = new ArrayList<>();
        for (Integer i : oldList) {
            newList.add(i);
        }
        return newList;
    }

    private ArrayList<ArrayList<Integer>> deepCopyDouble (ArrayList<ArrayList<Integer>> oldList) {
        ArrayList<ArrayList<Integer>> newList = new ArrayList<>();
        for (ArrayList<Integer> innerList : oldList) {
            newList.add(deepCopy(innerList));
        }
        return newList;
    }

    private String evaluateGuess(ArrayList<Integer> guess, ArrayList<Integer> answer, boolean human) {
        String evaluation = "";
        ArrayList<String> evaluationList = new ArrayList<>();
        ArrayList<Integer> guessCopy = deepCopy(guess);
        boolean win = true;

        ArrayList<Integer> alreadyCheckedInGuess = new ArrayList<>();
        ArrayList<Integer> alreadyCheckedInAnswer = new ArrayList<>();
        //check firmi
        for (int i = 0; i<answer.size(); i++) {
            //System.out.println(i+" "+answer+" "+guessCopy);
            if (answer.get(i) == guessCopy.get(i) && !alreadyCheckedInGuess.contains(i)) {
                evaluationList.add("firmi");
                alreadyCheckedInGuess.add(i);
                alreadyCheckedInAnswer.add(i);
            }
        }
        //System.out.println(alreadyCheckedInAnswer + " " + alreadyCheckedInGuess);
        //check pico
        for (int i = 0; i<guessCopy.size(); i++) {
            if (!alreadyCheckedInGuess.contains(i)) {
                ArrayList<Integer> temp = indexOfAll(guessCopy.get(i), answer);
                if (answer.contains(guessCopy.get(i)) &&
                        !alreadyCheckedInAnswer.containsAll(temp)) {
                    evaluationList.add("pico");
                    alreadyCheckedInGuess.add(i);
                    alreadyCheckedInAnswer = addNew(temp, alreadyCheckedInAnswer);
                    win = false;
                }
            }
        }
        if (win && alreadyCheckedInAnswer.size() == numberLength) {
            return "correct!";
        }
        if (human) {
            while (evaluationList.size()>0) {
                int index = (int) (Math.random() * evaluationList.size());
                evaluation += evaluationList.get(index) + " ";
                evaluationList.remove(index);
            }
        } else {
            for (String s : evaluationList) {
                evaluation += s;
            }
        }
        return evaluation;
    }

    private ArrayList<Integer> addNew(ArrayList<Integer> addableElements, ArrayList<Integer> oldList) {
        ArrayList<Integer> newList = deepCopy(oldList);
        for (Integer i : addableElements) {
            if (!newList.contains(i)) {
                newList.add(i);
                return newList;
            }
        }
        return null;
    }

    private ArrayList<Integer> indexOfAll(Integer element, ArrayList<Integer> list) {
        final ArrayList<Integer> indexList = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            if (element.equals(list.get(i))) {
                indexList.add(i);
            }
        }
        return indexList;
    }

    private ArrayList<Integer> generateNumber() {
        ArrayList<Integer> answer = new ArrayList<>();
        for (int i = 0; i < numberLength; i++) {
            int digit = (int) (Math.random() * (maxNumber+1));
            if (!doubles) {
                while (answer.contains(digit)) {
                    digit = (int) (Math.random() * (maxNumber+1));
                }
            }
            answer.add(digit);
        }
        return answer;
    }

    public static void main(String[] args) {
        PicoFirmiController pf = new PicoFirmiController(4, 5, true);
        /*int trials = 10000;
        double sum = 0.0;
        double min = 100;
        double max = 0;
        for (int i = 0; i < trials; i++) {
            double roundScore = pf.playGame(1, null);
            sum += roundScore;
            if (roundScore > max) {
                max = roundScore;
            }
            if (roundScore < min) {
                min = roundScore;
            }
        }
        System.out.println("avg: "+sum/trials + ", max: "+max + ", min: "+min);*/
        pf.playGame(2, null);
    }
}
