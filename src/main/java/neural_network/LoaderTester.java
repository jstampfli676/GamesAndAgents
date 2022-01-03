package neural_network;

public class LoaderTester {

    public static void main(String[] args) {
        NeuralNet n = new NeuralNet(new int[]{16,8,4,2});
        n.saveAgent("testAgent.txt");
        NeuralNet loaded = new NeuralNet("testAgent.txt", new int[]{16,8,4,2});
        System.out.println(n.equals(loaded));
    }
}
