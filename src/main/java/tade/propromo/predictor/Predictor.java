package tade.propromo.predictor;

import java.math.BigDecimal;

public interface Predictor {
    public BigDecimal[] getFirstGuess();
    public BigDecimal[] predictRow(int round, int row, int[][] previousValues);
}
