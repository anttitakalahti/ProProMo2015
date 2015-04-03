package tade.propromo.predictor;

import org.junit.Test;

import java.util.stream.DoubleStream;

import static org.junit.Assert.assertEquals;

public class TestZeroPredictor {

    @Test
    public void initialPredictionValuesSumToOne() {
        ZeroPredictor predictor = new ZeroPredictor();
        assertEquals(1d, DoubleStream.of(predictor.getFirstGuess()).sum(), 0d);
    }
}
