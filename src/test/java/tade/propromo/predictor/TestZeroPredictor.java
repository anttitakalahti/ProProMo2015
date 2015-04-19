package tade.propromo.predictor;

import org.junit.Test;

import java.math.BigDecimal;
import java.util.stream.DoubleStream;

import static junit.framework.Assert.assertTrue;
import static org.junit.Assert.assertEquals;

public class TestZeroPredictor {

    private static final double EPSILON = 1e-5;

    @Test
    public void initialPredictionValuesSumToOne() {
        ZeroPredictor predictor = new ZeroPredictor();
        double[] firstGuess = predictor.getFirstGuess();
        double sum = 0d;
        for (double prediction : firstGuess) {
            sum += prediction;
        }
        assertEquals("Sum is equal to one.", 1d, sum, EPSILON);
    }
}
