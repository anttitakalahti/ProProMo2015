package tade.propromo.predictor;


import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class TestDuvinsPredictor {

    @Test
    public void testGuessesBasedOnNonZeroPreviousValue() {
        DuvinsPredictor duvinsPredictor = new DuvinsPredictor();

        int[] previousValues = new int[10];
        previousValues[9] = 50;
        double[] prediction = duvinsPredictor.predictRow(10, previousValues);

        assertEquals(1d/4, prediction[49]);
        assertEquals(1d/4, prediction[51]);
        assertEquals((1d/2-97*Predictor.MINIMAL), prediction[50]);

        double sum = 0d;
        for (double d : prediction) {
            sum += d;
        }
        assertEquals(1d, sum);
    }

}
