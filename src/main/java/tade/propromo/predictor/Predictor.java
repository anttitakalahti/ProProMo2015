package tade.propromo.predictor;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Arrays;

public interface Predictor {

    public static final int COLS = 303;
    public static final int SCALE = 10;
    public static final double MINIMAL = 0.0000001;

    public double[] getFirstGuess();
    public double[] predictRow(int round, int[] previousValues);

    default public double[] normalize(double[] values) {
        double[] normalized = new double[values.length];

        double sum = Arrays.stream(values).sum();
        for (int position=0; position < values.length; ++position) {
            normalized[position] = values[position] / sum;
        }

        return normalized;
    }

    default public double[] normalize(int[] values) {
        double[] d = new double[values.length];

        int sum = Arrays.stream(values).sum();

        for (int i=0; i<values.length; ++i) {
            d[i] = (double)values[i] / sum;
        }

        return d;
    }
}
