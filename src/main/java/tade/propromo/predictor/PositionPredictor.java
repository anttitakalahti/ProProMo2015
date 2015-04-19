package tade.propromo.predictor;

import tade.propromo.Trainer;

import java.io.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

public class PositionPredictor implements Predictor {

    private static final double MY_MINIMAL = 0.0001;
    private static final double ALL_BUT_MINIMAL = 1 - (99 * MY_MINIMAL);
    private HashSet<Integer> interesting_positions;

    private int[][] trainingData;
    private double[] zeroProbabilities;


    private double[][] preCalculatedProbabilities;


    public PositionPredictor() {
        try {

            trainingData = Trainer.initializeTestDataFromFile(Trainer.TRAINING_DATA_FILE_NAME_ROUND_TWO);
            countZeroProbabilities();


            interesting_positions = new HashSet<>();
            for (int i : new int[]{16, 50, 126, 184, 237, 244, 248, 270, 279, 291}) {
                interesting_positions.add(i);
            }

            preCalculatedProbabilities = new double[303][];
            for (Integer position : interesting_positions) {
                preCalculatedProbabilities[position] = getValueProbabilitiesForPosition(position);
            }

        } catch (IOException e) {
            e.printStackTrace(System.out);
            System.exit(-1);
        }
    }



    private void countZeroProbabilities() {
        int[] counts = new int[COLS];
        Arrays.fill(counts, 0);
        for (int[] row : trainingData) {
            for (int position=0; position < row.length; ++position) {
                if (row[position] == 0) { counts[position] += 1; }
            }
        }

        zeroProbabilities = new double[COLS];
        for (int position=0; position<COLS; ++position) {
            zeroProbabilities[position] = (double)counts[position] / trainingData.length;
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
        if (interesting_positions.contains(position)) {
            return preCalculatedProbabilities[position];
        }

        double[] predictions = new double[100];
        Arrays.fill(predictions, MY_MINIMAL);
        predictions[0] = ALL_BUT_MINIMAL;
        return predictions;
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
