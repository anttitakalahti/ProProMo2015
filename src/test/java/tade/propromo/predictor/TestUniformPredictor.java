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
        double[] firstGuess = predictor.getFirstGuess();
        double sum = 0d;
        for (double prediction : firstGuess) {
            sum += prediction;
        }
        assertEquals("Sum is equal to one.", 1d, sum);
    }

}

