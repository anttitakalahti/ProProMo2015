package tade.propromo;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.stream.DoubleStream;

public class TrainingDataWorker extends Worker {

    private ArrayList<int[]> testData;
    private ArrayList<double[][]> output;

    public TrainingDataWorker(String trainingDataFileFullPath) throws Exception {

        output = new ArrayList<>();
        testData = new ArrayList<>();
        System.out.println("Reading file: " + trainingDataFileFullPath);
        initializeTestDataFromFile(trainingDataFileFullPath);
        Collections.shuffle(testData);
        rows = testData.size();

        System.out.println("Got " + rows + " rows.");
    }

    private void initializeTestDataFromFile(String trainingDataFileFullPath) throws Exception {
        File file = new File(trainingDataFileFullPath);
        FileReader fr = new FileReader(file);
        BufferedReader br = new BufferedReader(fr);
        String line;
        while((line = br.readLine()) != null){
            testData.add(getValues(line));
            if (testData.size() == 2) { break; } // speed == king
        }
        br.close();
        fr.close();
    }



    @Override
    protected int[] getPreviousRoundValues() {
        int[] previousRoundValues = new int[testData.size()];
        for (int i=0; i<previousRoundValues.length; ++i) {
            previousRoundValues[i] = testData.get(i)[getRound() - 1];
        }
        return previousRoundValues;
    }

    @Override
    protected void writePredictionsToOutputFile(double[][] myGuess) {
        output.add(myGuess);
    }

    /**
     * TODO: compare predictions to training data values.
     * @return
     */
    public double calculateScore() {

        double[] rowScores = new double[previousValues[0].length];
        Arrays.fill(rowScores, 0d);

        for (int column=0; column<previousValues.length; ++column) {
            for (int row=0; row<previousValues[0].length; ++row) {

                // previousValues = new int[303][rows];
                int correctValue = previousValues[column][row];

                // myGuess = new double[rows][100];    // hundred values per line.
                double predictedProbability = output.get(column)[row][correctValue];


                System.out.println("actual: " + correctValue + " with a probability of: " + predictedProbability);

                rowScores[row] += predictedProbability;
            }
        }

        return DoubleStream.of(rowScores).sum();
    }
}
