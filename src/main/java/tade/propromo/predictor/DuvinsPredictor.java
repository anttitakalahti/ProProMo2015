package tade.propromo.predictor;

import java.util.Arrays;
import java.util.Random;

/**
 * These are the relevant bits from Duvins original code
 * https://github.com/verwijnen/ProProMo2015/blob/master/src/org/duvin/propromo2015/Example.java
 */
public class DuvinsPredictor implements Predictor {

    @Override
    public double[] getFirstGuess() {
        Random r = new Random();
        double[] result = new double[100];
        double sum = 0;
        double value;
        for (int i = 0; i < 100; i++) {
            result[i] = value = r.nextDouble();
            sum += value;
        }
        //normalize
        for (int i = 0; i < 100; i++) {
            result[i] /= sum;
        }
        return result;
    }

    @Override
    public double[] predictRow(int round, int row, int[][] previousValues) {
        int last = previousValues[round-1][row];
        double minimal = 0.0000001;
        double[] result = new double[100];
        //make sure we don't have 0 probability
        Arrays.fill(result, minimal);
        if (last == 0) {
            //put all but minimal to bet on zero
            result[0] = 1-99*minimal;
        } else {
            //25% to adjacent values, 49.999% to previous
            result[last]=.5-97*minimal;
            result[last-1]=.25;
            result[last+1]=.25;
        }
        return result;
    }
}
