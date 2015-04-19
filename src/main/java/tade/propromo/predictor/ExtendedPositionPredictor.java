package tade.propromo.predictor;

import tade.propromo.Trainer;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;

public class ExtendedPositionPredictor implements Predictor {

    private static final double MY_MINIMAL = 0.0001;

    private int[][] trainingData;

    private double[][] preCalculatedProbabilities;


    public ExtendedPositionPredictor() {
        try {

            trainingData = Trainer.initializeTestDataFromFile(Trainer.TRAINING_DATA_FILE_NAME_ROUND_TWO);


            preCalculatedProbabilities = new double[303][];
            for (Integer position=0; position<COLS; ++position) {
                preCalculatedProbabilities[position] = getValueProbabilitiesForPosition(position);
            }

        } catch (IOException e) {
            e.printStackTrace(System.out);
            System.exit(-1);
        }
    }

    @Override
    public double[] getFirstGuess() {
        return predictPosition(0, new int[0]);
    }

    @Override
    public double[] predictRow(int round, int[] previousValues) {
        return predictPosition(round, previousValues);
    }

    protected double[] predictPosition(int position, int[] previousValues) {
        return preCalculatedProbabilities[position];
    }



    private double[] getValueProbabilitiesForPosition(Integer position) {
        int[] counts = new int[100];
        Arrays.fill(counts, 10);

        for(int[] line : trainingData) {
            counts[line[position]]++;
        }

        return normalize(counts);
    }

}
