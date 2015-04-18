package tade.propromo.predictor;

import tade.propromo.Trainer;

import java.io.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Arrays;

public class PositionPredictor implements Predictor {

    private int[][] trainingData;
    private BigDecimal[] zeroProbabilities;
    private int[] countsForSixteen;
    private int[] countsForFifty;

    public PositionPredictor() throws IOException {
        trainingData = Trainer.initializeTestDataFromFile(Trainer.TRAINING_DATA_FILE_NAME_ROUND_TWO);
        countZeroProbabilities();

        countsForSixteen = getCountsForSixteen();
        countsForFifty = getCountsForFifty();

    }



    private void countZeroProbabilities() {
        int[] counts = new int[COLS];
        Arrays.fill(counts, 0);
        for (int[] row : trainingData) {
            for (int position=0; position < row.length; ++position) {
                if (row[position] == 0) { counts[position] += 1; }
            }
        }

        zeroProbabilities = new BigDecimal[COLS];
        Arrays.fill(zeroProbabilities, BigDecimal.ZERO);

        for (int position=0; position<COLS; ++position) {
            zeroProbabilities[position] = new BigDecimal(counts[position]).divide(new BigDecimal(trainingData.length), SCALE, RoundingMode.HALF_UP);
        }
    }

    public BigDecimal[] getFirstGuess() {
        return predictPosition(0, new int[0]);
    }

    public BigDecimal[] predictRow(int round, int[] previousValues) {
        return predictPosition(round, previousValues);
    }

    protected BigDecimal[] predictPosition(int position, int[] previousValues) {
        BigDecimal[] predictions = new BigDecimal[100];
        Arrays.fill(predictions, MINIMAL);


        if (position == 16) {

            predictions = predictSixteen(previousValues);

        } else if (position == 50) {

            predictions = predictFifty(previousValues);

        } else {
            predictions[0] = BigDecimal.ONE;


            if (zeroProbabilities[position].compareTo(new BigDecimal(50).divide(new BigDecimal(100))) < 0) {

                // System.out.printf("position %d has zero probability of %.6f \n", position, zeroProbabilities[position]);

            }

        }

        return normalize(predictions);
    }

    private BigDecimal[] predictSixteen(int[] previousValues) {
        BigDecimal[] prediction = new BigDecimal[100];
        Arrays.fill(prediction, MINIMAL);

        if (Arrays.stream(previousValues).allMatch(x -> x == 0)) {

            int[] relevantValues = new int[] {0,68,69};
            for (int value : relevantValues) {
                prediction[value] = new BigDecimal(countsForSixteen[value]);

            }

        } else {
            prediction[0] = BigDecimal.ONE;
        }

        return prediction;
    }

    private BigDecimal[] predictFifty(int[] previousValues) {
        BigDecimal[] prediction = new BigDecimal[100];
        Arrays.fill(prediction, MINIMAL);

        return prediction;
    }

    private int[] getValueCountsForPosition(int position) {
        int[] counts = new int[100];
        for (int[] row : trainingData) {
            counts[row[position]]++;
        }
        return counts;
    }

    private int[] getCountsForSixteen() {
        int[] counts = new int[100];
        for(int[] line : trainingData) {
            if (Arrays.stream(Arrays.copyOf(line, 15)).allMatch(x -> x == 0)) {
                counts[line[16]]++;
            }
        }


        for(int value=0; value<counts.length; ++value) {
            // System.out.println("value :" + value + " has count of: " + counts[value]);
        }

        System.out.println(Arrays.stream(counts).sum());
        return counts;
    }

    private int[] getCountsForFifty() {
        int[] counts = new int[100];
        for(int[] line : trainingData) {

            counts[line[50]]++;
        }

        for(int value=0; value<counts.length; ++value) {
            System.out.println("value :" + value + " has count of: " + counts[value]);
        }

        return counts;
    }
}
