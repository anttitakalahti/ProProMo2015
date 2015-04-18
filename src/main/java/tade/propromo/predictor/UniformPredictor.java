package tade.propromo.predictor;

import java.math.BigDecimal;
import java.util.Arrays;

public class UniformPredictor implements Predictor {

    public BigDecimal[] getFirstGuess() {
        BigDecimal[] result = new BigDecimal[100];
        Arrays.fill(result, new BigDecimal(1).divide(new BigDecimal(100)));
        return result;
    }

    public BigDecimal[] predictRow(int round, int[] previousValues) {
        BigDecimal[] result = new BigDecimal[100];
        Arrays.fill(result, new BigDecimal(1).divide(new BigDecimal(100)));
        return result;
    }
}
