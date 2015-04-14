package tade.propromo.predictor;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Arrays;
import java.util.Random;

/**
 * These are the relevant bits from Duvins original code
 * https://github.com/verwijnen/ProProMo2015/blob/master/src/org/duvin/propromo2015/Example.java
 */
public class DuvinsPredictor implements Predictor {

    public static final BigDecimal MINIMAL = BigDecimal.ONE.divide(new BigDecimal(10000000));

    @Override
    public BigDecimal[] getFirstGuess() {
        Random r = new Random();
        BigDecimal[] result = new BigDecimal[100];
        BigDecimal sum = new BigDecimal(0).setScale(10, RoundingMode.HALF_UP);
        for (int i = 0; i < 100; i++) {
            result[i] = new BigDecimal(r.nextDouble()).setScale(10, RoundingMode.HALF_UP);
            sum = sum.add(result[i]);
        }
        //normalize
        for (int i = 0; i < 100; i++) {
            result[i] = result[i].divide(sum, RoundingMode.HALF_UP);
        }
        return result;
    }

    @Override
    public BigDecimal[] predictRow(int round, int row, int[] previousValues) {
        int last = previousValues[round-1];
        BigDecimal[] result = new BigDecimal[100];
        //make sure we don't have 0 probability
        Arrays.fill(result, MINIMAL);
        if (last == 0) {
            //put all but minimal to bet on zero
            result[0] = BigDecimal.ONE.add(new BigDecimal(99).multiply(MINIMAL).negate());
        } else {
            //25% to adjacent values, 49.999% to previous
            result[last]   = BigDecimal.ONE.divide(new BigDecimal(2)).add(new BigDecimal(97).multiply(MINIMAL).negate());
            result[last-1] = BigDecimal.ONE.divide(new BigDecimal(4));
            result[last+1] = BigDecimal.ONE.divide(new BigDecimal(4));
        }
        return result;
    }
}
