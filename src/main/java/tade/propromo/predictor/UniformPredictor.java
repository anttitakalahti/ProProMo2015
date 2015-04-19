package tade.propromo.predictor;

import java.util.Arrays;

public class UniformPredictor implements Predictor {

    @Override
    public double[] getFirstGuess() {
        return giveUniformPrediction();
    }

    @Override
    public double[] predictRow(int round, int[] previousValues) {
        return giveUniformPrediction();
    }

    private double[] giveUniformPrediction() {
        double[] result = new double[100];
        Arrays.fill(result, 1d / 100);
        return result;
    }
}
