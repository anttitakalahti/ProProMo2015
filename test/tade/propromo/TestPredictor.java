package tade.propromo;

import org.junit.Test;

import java.util.stream.DoubleStream;

import static org.junit.Assert.assertEquals;

public class TestPredictor {

    @Test
    public void initialPredictionGivesHundredValues() {
         assertEquals(100, Predictor.getFirstGuess().length);
    }

    @Test
    public void initialPredictionValuesSumToOne() {
        assertEquals(1d, DoubleStream.of(Predictor.getFirstGuess()).sum(), 0d);
    }

}

