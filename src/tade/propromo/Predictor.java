package tade.propromo;

import java.util.Arrays;

public class Predictor {

    public static double[] getFirstGuess() {
        double[] result = new double[100];
        Arrays.fill(result, 1d/100);
        return result;
    }

    public static double[] predictRow(int round, int row, int[][] previousValues) {
        double[] result = new double[100];
        Arrays.fill(result, 1d/100);
        return result;
    }
}
