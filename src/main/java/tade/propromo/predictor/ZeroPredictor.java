package tade.propromo.predictor;

import java.math.BigDecimal;
import java.util.Arrays;

public class ZeroPredictor implements Predictor {

    public static final BigDecimal OTHER_THAN_ZERO = BigDecimal.ONE.divide(new BigDecimal(10000000));

    @Override
    public BigDecimal[] getFirstGuess() {
        return alwaysPredictZero();
    }

    @Override
    public BigDecimal[] predictRow(int round, int row, int[][] previousValues) {
        return alwaysPredictZero();
    }

    private BigDecimal[] alwaysPredictZero() {
        BigDecimal[] predictions = new BigDecimal[100];
        Arrays.fill(predictions, OTHER_THAN_ZERO);
        predictions[0] = BigDecimal.ONE.add(new BigDecimal(99).multiply(OTHER_THAN_ZERO).negate());
        return predictions;
    }
}
