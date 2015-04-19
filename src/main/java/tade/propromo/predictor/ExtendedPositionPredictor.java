package tade.propromo.predictor;

import tade.propromo.Trainer;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;

public class ExtendedPositionPredictor implements Predictor {

    private double[][] preCalculatedProbabilities;

    public ExtendedPositionPredictor() {
        try {
            int[][] trainingData = Trainer.initializeTestDataFromFile(Trainer.TRAINING_DATA_FILE_NAME_ROUND_TWO);

            preCalculatedProbabilities = new double[303][];
            for (Integer position=0; position<COLS; ++position) {
                int[] counts = new int[100];
                Arrays.fill(counts, 10);

                for(int[] line : trainingData) {
                    counts[line[position]]++;
                }

                preCalculatedProbabilities[position] = normalize(counts);
            }
        } catch (IOException e) {
            e.printStackTrace(System.out);
            System.exit(-1);
        }
    }

    @Override
    public double[] getFirstGuess() {
        return predictPosition(0);
    }

    @Override
    public double[] predictRow(int round, int[] previousValues) {
        return predictPosition(round);
    }

    protected double[] predictPosition(int position) {
        return preCalculatedProbabilities[position];
    }

}
