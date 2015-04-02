package tade.propromo.predictor;

import tade.propromo.predictor.Predictor;

import java.util.Arrays;

public class UniformPredictor implements Predictor {

    public double[] getFirstGuess() {
        double[] result = new double[100];
        Arrays.fill(result, 1d/100);
        return result;
    }

    public double[] predictRow(int round, int row, int[][] previousValues) {
        double[] result = new double[100];
        Arrays.fill(result, 1d/100);
        return result;
    }
}
