package tade.propromo.predictor;

import java.math.BigDecimal;
import java.math.RoundingMode;

public interface Predictor {

    public static final int SCALE = 10;
    public static final BigDecimal MINIMAL = BigDecimal.ONE.divide(new BigDecimal(10000000));

    public BigDecimal[] getFirstGuess();
    public BigDecimal[] predictRow(int round, int row, int[][] previousValues);

    default public BigDecimal[] normalize(BigDecimal[] values) {
        BigDecimal sum = BigDecimal.ZERO;
        for (BigDecimal value : values) {
            sum = sum.add(value);
        }

        BigDecimal[] normalized = new BigDecimal[values.length];
        BigDecimal normalizedSum = BigDecimal.ZERO;
        for (int position=0; position < values.length; ++position) {
            // FLOOR to avoid 1.0000000001 - SORRY
            normalized[position] = values[position].divide(sum, SCALE, RoundingMode.FLOOR);
            normalizedSum = normalizedSum.add(normalized[position]);
        }

        // Add the rest to zero (after flooring the division)
        if (normalizedSum.compareTo(BigDecimal.ONE) < 0) {
            normalized[0] = normalized[0].add(BigDecimal.ONE.add(normalizedSum.negate()));
        }

        return normalized;
    }
}
