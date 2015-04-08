package tade.propromo.predictor;

import org.junit.Test;

import java.math.BigDecimal;
import java.util.stream.DoubleStream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class TestUniformPredictor {

    @Test
    public void initialPredictionGivesHundredValues() {
        UniformPredictor predictor = new UniformPredictor();
        assertEquals(100, predictor.getFirstGuess().length);
    }

    @Test
    public void initialPredictionValuesSumToOne() {
        UniformPredictor predictor = new UniformPredictor();
        BigDecimal[] firstGuess = predictor.getFirstGuess();
        BigDecimal sum = new BigDecimal(0);
        for (BigDecimal prediction : firstGuess) {
            sum = sum.add(prediction);
        }
        assertEquals("Sum is equal to one.", 0, BigDecimal.ONE.compareTo(sum));
    }

}

