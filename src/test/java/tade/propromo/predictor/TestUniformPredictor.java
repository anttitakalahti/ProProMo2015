package tade.propromo.predictor;

import org.junit.Test;

import java.util.stream.DoubleStream;

import static org.junit.Assert.assertEquals;

public class TestUniformPredictor {

    @Test
    public void initialPredictionGivesHundredValues() {
        UniformPredictor predictor = new UniformPredictor();
        assertEquals(100, predictor.getFirstGuess().length);
    }

    @Test
    public void initialPredictionValuesSumToOne() {
        UniformPredictor predictor = new UniformPredictor();
        assertEquals(1d, DoubleStream.of(predictor.getFirstGuess()).sum(), 0d);
    }

}

