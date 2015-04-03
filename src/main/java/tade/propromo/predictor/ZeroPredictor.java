package tade.propromo.predictor;

import java.util.Arrays;

public class ZeroPredictor implements Predictor {
    @Override
    public double[] getFirstGuess() {
        return alwaysPredictZero();
    }

    @Override
    public double[] predictRow(int round, int row, int[][] previousValues) {
        return alwaysPredictZero();
    }

    private double[] alwaysPredictZero() {
        double[] predictions = new double[100];
        Arrays.fill(predictions, 0.0000001);
        predictions[0] = 1d - (99 * 0.0000001);
        return predictions;
    }
}
