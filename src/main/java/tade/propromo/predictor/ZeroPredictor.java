package tade.propromo.predictor;

import java.math.BigDecimal;
import java.util.Arrays;

public class ZeroPredictor implements Predictor {

    @Override
    public BigDecimal[] getFirstGuess() {
        return alwaysPredictZero();
    }

    @Override
    public BigDecimal[] predictRow(int round, int row, int[] previousValues) {
        return alwaysPredictZero();
    }

    private BigDecimal[] alwaysPredictZero() {
        BigDecimal[] predictions = new BigDecimal[100];
        Arrays.fill(predictions, MINIMAL);
        predictions[0] = BigDecimal.ONE.add(new BigDecimal(99).multiply(MINIMAL).negate());
        return predictions;
    }
}
