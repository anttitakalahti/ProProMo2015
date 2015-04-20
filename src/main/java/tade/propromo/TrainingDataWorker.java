package tade.propromo;

import tade.propromo.predictor.Predictor;

import java.util.ArrayList;
import java.util.Arrays;

public class TrainingDataWorker extends Worker {

    private int[][] testData;
    private ArrayList<double[][]> output;

    public TrainingDataWorker(int[][] testData, Predictor predictor) throws Exception {

        output = new ArrayList<>();
        this.testData = testData;


        rows = testData.length;
        previousValues = new int[303][rows];
        round = 0;
        this.predictor = predictor;

        System.out.println("  Predictor: " + predictor.getClass() + " with " + rows + " rows.");
    }

    private int[] getActualValues(int round) {
        int[] values = new int[testData.length];
        for (int row=0; row<values.length; ++row) {
            values[row] = testData[row][round];
        }
        return values;
    }

    @Override
    protected int[] getPreviousRoundValues() {
        return getActualValues(getRound() - 1);
    }

    @Override
    protected void writePredictionsToOutputFile(double[][] myGuess) {
        output.add(myGuess);
    }

    public double calculateScore() {
        double[][] score = new double[rows][303];

        for (int round = 0; round < 303; round++) {

            double[][] predictions = output.get(round);
            int[] values = getActualValues(round);

            for (int vector = 0; vector < rows; vector++) {
                score[vector][round] = Math.log(predictions[vector][values[vector]]);
            }

        }

        double[] rowScores = new double[rows];

        for (int i=0; i<rows; ++i) {
            rowScores[i] = Arrays.stream(score[i]).sum();
        }

        return Arrays.stream(rowScores).sum();
    }
}
