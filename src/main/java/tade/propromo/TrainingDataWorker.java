package tade.propromo;

import tade.propromo.predictor.Predictor;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.DoubleStream;

public class TrainingDataWorker extends Worker {

    private int[][] testData;
    private ArrayList<BigDecimal[][]> output;

    public TrainingDataWorker(int[][] testData, Predictor predictor) throws Exception {

        output = new ArrayList<>();
        this.testData = testData;


        rows = testData.length;
        previousValues = new int[303][rows];
        round = 0;
        this.predictor = predictor;

        System.out.println("  Predictor: " + predictor.getClass() + " with " + rows + " rows.");
    }

    @Override
    protected int[] getPreviousRoundValues() {
        int[] previousRoundValues = new int[testData.length];
        for (int row=0; row<previousRoundValues.length; ++row) {
            previousRoundValues[row] = testData[row][getRound() - 1];
        }
        return previousRoundValues;
    }

    @Override
    protected void writePredictionsToOutputFile(BigDecimal[][] myGuess) {
        output.add(myGuess);
    }

    public BigDecimal calculateScore() {
        BigDecimal[] rowScores = new BigDecimal[previousValues[0].length];
        Arrays.fill(rowScores, BigDecimal.ZERO);

        for (int column=0; column<previousValues.length; ++column) {
            for (int row=0; row<previousValues[0].length; ++row) {

                // previousValues = new int[303][rows];
                int correctValue = previousValues[column][row];

                // myGuess = new BigDecimal[rows][100];    // hundred values per line.
                BigDecimal predictedProbability = output.get(column)[row][correctValue];

                rowScores[row] = rowScores[row].add(predictedProbability);

            }
        }

        BigDecimal totalScore = BigDecimal.ZERO;
        for (BigDecimal rowScore : rowScores) {
            totalScore = totalScore.add(rowScore);
        }
        return totalScore.setScale(4, RoundingMode.HALF_UP);
    }
}
