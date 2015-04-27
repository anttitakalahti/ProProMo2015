package tade.propromo.predictor;

import org.junit.Before;
import org.junit.Test;
import tade.propromo.TestStatistics;

import java.util.Arrays;

import static junit.framework.TestCase.assertNotNull;
import static org.junit.Assert.assertEquals;

public class TestThirdRoundPredictor {

    private ThirdRoundPredictor predictor;

    @Before
    public void initialize() {
        predictor = new ThirdRoundPredictor();
    }

    @Test
    public void testInitialPredictionDoesNotGiveHundredPercentToZero() {
        assertEquals(0.9991559554333334, predictor.getFirstGuess()[0], TestStatistics.EPSILON);
    }

    @Test
    public void testInitialPredictionSumsToOne() {
        assertEquals(1d, Arrays.stream(predictor.getFirstGuess()).sum(), TestStatistics.EPSILON);
    }

    @Test
    public void testAllPredictionsSumToOne() {
        for (int position = 1; position < 303; ++position) {
            double[] prediction = predictor.predictRow(position, zeros(position));
            assertNotNull("prediction for position " + position + " is null.", prediction);
            assertEquals(1d, Arrays.stream(prediction).sum(), TestStatistics.EPSILON);
        }
    }

    private int[] zeros(int length) {
        int[] ints = new int[length];
        Arrays.fill(ints, 0);
        return ints;
    }

}
