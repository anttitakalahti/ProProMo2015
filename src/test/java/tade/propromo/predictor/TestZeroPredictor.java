package tade.propromo.predictor;

import org.junit.Test;

import java.math.BigDecimal;
import java.util.stream.DoubleStream;

import static junit.framework.Assert.assertTrue;
import static org.junit.Assert.assertEquals;

public class TestZeroPredictor {

    @Test
    public void initialPredictionValuesSumToOne() {
        ZeroPredictor predictor = new ZeroPredictor();
        BigDecimal[] firstGuess = predictor.getFirstGuess();
        BigDecimal sum = BigDecimal.ZERO;
        for (BigDecimal prediction : firstGuess) {
            sum = sum.add(prediction);
        }
        assertEquals("Sum is equal to one.", 0, BigDecimal.ONE.compareTo(sum));
    }
}
