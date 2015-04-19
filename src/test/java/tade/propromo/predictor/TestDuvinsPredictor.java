package tade.propromo.predictor;


import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class TestDuvinsPredictor {

    private static final double EPSILON = 1e-5;

    @Test
    public void testGuessesBasedOnNonZeroPreviousValue() {
        DuvinsPredictor duvinsPredictor = new DuvinsPredictor();

        int[] previousValues = new int[10];
        previousValues[9] = 50;
        double[] prediction = duvinsPredictor.predictRow(10, previousValues);

        assertEquals(1d/4, prediction[49], EPSILON);
        assertEquals(1d/4, prediction[51], EPSILON);
        assertEquals((1d/2-97*Predictor.MINIMAL), prediction[50], EPSILON);

        double sum = 0d;
        for (double d : prediction) {
            sum += d;
        }
        assertEquals(1d, sum, EPSILON);
    }

}
