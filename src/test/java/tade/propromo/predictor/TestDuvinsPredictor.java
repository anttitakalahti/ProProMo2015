package tade.propromo.predictor;


import org.junit.Test;

import java.math.BigDecimal;

import static org.junit.Assert.assertEquals;

public class TestDuvinsPredictor {

    @Test
    public void testGuessesBasedOnNonZeroPreviousValue() {
        DuvinsPredictor duvinsPredictor = new DuvinsPredictor();

        int[] previousValues = new int[10];
        previousValues[9] = 50;
        BigDecimal[] prediction = duvinsPredictor.predictRow(10, 1, previousValues);

        assertEquals(0, prediction[49].compareTo(BigDecimal.ONE.divide(new BigDecimal(4))));
        assertEquals(0, prediction[51].compareTo(BigDecimal.ONE.divide(new BigDecimal(4))));

        BigDecimal target = BigDecimal.ONE.divide(new BigDecimal(2));
        target = target.add(Predictor.MINIMAL.multiply(new BigDecimal(97)).negate());
        assertEquals(0, prediction[50].compareTo(target));

        BigDecimal sum = BigDecimal.ZERO;
        for (BigDecimal p : prediction) {
            sum = sum.add(p);
        }
        assertEquals(0, sum.compareTo(BigDecimal.ONE));
    }

}
