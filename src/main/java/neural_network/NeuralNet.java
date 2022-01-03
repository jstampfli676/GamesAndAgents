package neural_network;

import black_jack.PlayerDealerTotals;

import java.io.*;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class NeuralNet {

    //Matrix weights_ih,weights_ho , bias_h,bias_o;
    Matrix[] layers;
    Matrix[] bias;
    double l_rate=0.01;

    public NeuralNet(int[] dimensions) {
        layers = new Matrix[dimensions.length-1];
        bias = new Matrix[dimensions.length-1];
        for (int i = 0; i<layers.length; i++) {
            layers[i] = new Matrix(dimensions[i+1], dimensions[i]);
            bias[i] = new Matrix(dimensions[i+1], 1);
        }
    }

    public NeuralNet(NeuralNet n) {
        int numLayers = n.layers.length;
        this.layers = new Matrix[numLayers];
        this.bias = new Matrix[numLayers];
        for (int i = 0; i<numLayers; i++) {
            this.layers[i] = new Matrix(n.layers[i]);
            this.bias[i] = new Matrix(n.bias[i]);
        }
    }

    public NeuralNet(NeuralNet mom, NeuralNet dad) {
        int numLayers = mom.layers.length;
        this.layers = new Matrix[numLayers];
        this.bias = new Matrix[numLayers];
        for (int i = 0; i<numLayers; i++) {
            this.layers[i] = new Matrix(mom.layers[i], dad.layers[i]);
            this.bias[i] = new Matrix(mom.bias[i], dad.bias[i]);
        }
    }

    public NeuralNet(String filename, int[] dimensions) {
        this(dimensions);
        try {
            File savedAgent = new File(filename);
            Scanner reader = new Scanner(savedAgent);
            int x = 0;
            int y = 0;
            int curLayer = 0;
            while (curLayer<this.layers.length) {
                try {
                    this.layers[curLayer].data[x][y] = reader.nextDouble();
                } catch (NoSuchElementException e) {
                    System.out.println(filename);
                }
                y+=1;
                if (y==dimensions[curLayer]) {
                    reader.nextLine();
                    y=0;
                    x++;
                    if (x==dimensions[curLayer+1]) {
                        x=0;
                        curLayer++;
                    }
                }
            }
            int curBias = 0;
            while (curBias<this.bias.length) {
                this.bias[curBias].data[x][y] = reader.nextDouble();
                y++;
                if (y==1) {
                    reader.nextLine();
                    y=0;
                    x++;
                    if (x==dimensions[curBias+1]) {
                        x=0;
                        curBias++;
                    }
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    /*public NeuralNet(String filename, int i, int h, int o) {
        this(i, h, o);
        try {
            File savedAgent = new File(filename);
            Scanner reader = new Scanner(savedAgent);
            int x = 0;
            int y = 0;
            int curMatrix = 0;
            while (reader.hasNextDouble()) {
                if (curMatrix == 0) {
                    this.weights_ih.data[x][y] = reader.nextDouble();
                    y+=1;
                    if (y==i) {
                        reader.nextLine();
                        y=0;
                        x++;
                        if (x==h) {
                            x=0;
                            curMatrix++;
                        }
                    }
                } else if (curMatrix == 1) {
                    this.weights_ho.data[x][y] = reader.nextDouble();
                    y+=1;
                    if (y==h) {
                        reader.nextLine();
                        y=0;
                        x++;
                        if (x==o) {
                            x=0;
                            curMatrix++;
                        }
                    }
                } else if (curMatrix == 2) {
                    this.bias_h.data[x][y] = reader.nextDouble();
                    y+=1;
                    if (y==1) {
                        reader.nextLine();
                        y=0;
                        x++;
                        if (x==h) {
                            x=0;
                            curMatrix++;
                        }
                    }
                } else {
                    this.bias_o.data[x][y] = reader.nextDouble();
                    y+=1;
                    if (y==1) {
                        reader.nextLine();
                        y=0;
                        x++;
                        if (x==o) {
                            x=0;
                            curMatrix++;
                        }
                    }
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }*/

    public List<Double> predict(double[] X)
    {
        Matrix temp = Matrix.fromArray(X);
        for (int i = 0; i<layers.length; i++) {
            temp = Matrix.multiply(layers[i], temp);
            temp.add(bias[i]);
            temp.elu(0.01);
        }
        return temp.toArray();


        /*Matrix hidden = Matrix.multiply(weights_ih, temp);
        hidden.add(bias_h);
        hidden.elu(0.01);

        Matrix output = Matrix.multiply(weights_ho,hidden);
        output.add(bias_o);
        output.sigmoid();

        return output.toArray();*/
    }


    /*public void fit(double[][]X,double[][]Y,int epochs)//not being used atm
    {
        for(int i=0;i<epochs;i++) {
            int sampleN =  (int)(Math.random() * X.length );
            this.backProp(X[sampleN], Y[sampleN]);
        }
    }*/

    /*public void backProp(double [] X,double [] Y) {//not being used atm
        Matrix input = Matrix.fromArray(X);
        Matrix hidden = Matrix.multiply(weights_ih, input);
        hidden.add(bias_h);
        hidden.sigmoid();

        Matrix output = Matrix.multiply(weights_ho,hidden);
        output.add(bias_o);
        output.sigmoid();

        Matrix target = Matrix.fromArray(Y);

        Matrix error = Matrix.subtract(target, output);
        Matrix gradient = output.dsigmoid();
        gradient.multiply(error);
        gradient.multiply(l_rate);

        Matrix hidden_T = Matrix.transpose(hidden);
        Matrix who_delta =  Matrix.multiply(gradient, hidden_T);

        weights_ho.add(who_delta);
        bias_o.add(gradient);

        Matrix who_T = Matrix.transpose(weights_ho);
        Matrix hidden_errors = Matrix.multiply(who_T, error);

        Matrix h_gradient = hidden.dsigmoid();
        h_gradient.multiply(hidden_errors);
        h_gradient.multiply(l_rate);

        Matrix i_T = Matrix.transpose(input);
        Matrix wih_delta = Matrix.multiply(h_gradient, i_T);

        weights_ih.add(wih_delta);
        bias_h.add(h_gradient);

    }*/

    public void saveAgent(String filename) {
        try {
            File file = new File(filename);
            clearFile(filename);
            file.getParentFile().mkdirs();
            FileWriter myWriter = new FileWriter(file);
            for (int i = 0; i<layers.length; i++) {
                myWriter.write(String.valueOf(layers[i]));
            }
            for (int i = 0; i<bias.length; i++) {
                myWriter.write(String.valueOf(bias[i]));
            }
            myWriter.close();
        } catch (IOException e) {
            System.out.println(e);
        } catch (NullPointerException e) {
            try {
                File file = new File(filename);
                clearFile(filename);
                FileWriter myWriter = new FileWriter(file);
                for (int i = 0; i<layers.length; i++) {
                    myWriter.write(String.valueOf(layers[i]));
                }
                for (int i = 0; i<bias.length; i++) {
                    myWriter.write(String.valueOf(bias[i]));
                }
                myWriter.close();
            } catch (IOException f) {
                f.printStackTrace();
            }
        }
    }

    private void clearFile(String filename) {
        try {
            FileWriter fwOb = new FileWriter("FileName", false);
            PrintWriter pwOb = new PrintWriter(fwOb, false);
            pwOb.flush();
            pwOb.close();
            fwOb.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof NeuralNet)) {
            return false;
        }
        NeuralNet n = (NeuralNet) o;
        if (this.layers.length != n.layers.length || this.bias.length != n.bias.length) {
            return false;
        }
        for (int i = 0; i<layers.length; i++) {
            if (!(this.layers[i].equals(n.layers[i]) && this.bias[i].equals(n.bias[i]))) {
                return false;
            }
        }
        return true;
    }
}
