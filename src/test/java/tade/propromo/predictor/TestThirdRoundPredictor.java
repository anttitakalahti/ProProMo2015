package tade.propromo.predictor;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

import org.junit.Before;
import org.junit.Test;
import tade.propromo.Statistics;
import tade.propromo.TestStatistics;

import java.util.Arrays;

public class TestThirdRoundPredictor {

    private Predictor predictor;

    @Before
    public void initialize() {
        Statistics statistics = mock(Statistics.class);
        when(statistics.getAverageZeroProbabilityForPosition(0)).thenReturn(1d);
        when(statistics.getAverageZeroProbabilityForPosition(16)).thenReturn(0.27);


        predictor = new ThirdRoundPredictor(statistics);
    }

    @Test
    public void testInitialPredictionDoesNotGiveHundredPercentToZero() {
        assertEquals(0.99999, predictor.getFirstGuess()[0], TestStatistics.EPSILON);
    }

    @Test
    public void testInitialPredictionSumsToOne() {
        assertEquals(1d, Arrays.stream(predictor.getFirstGuess()).sum(), TestStatistics.EPSILON);
    }


    @Test
    public void testPeakPredictionSplitsProbabilitiesToFiveValuesClosestToLastNonZeroValue() {
        double[] prediction = predictor.predictRow(16, new int[] {0, 10, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0});

        assertEquals(1d, Arrays.stream(prediction).sum(), TestStatistics.EPSILON);
        assertEquals(0.27, prediction[0], TestStatistics.EPSILON);

        double forThePeak = 1d - 0.27 - 0.01;
        assertEquals(5d/39 * forThePeak, prediction[ 8], TestStatistics.EPSILON);
        assertEquals(8d/39 * forThePeak, prediction[ 9], TestStatistics.EPSILON);
        assertEquals(1d/3  * forThePeak, prediction[10], TestStatistics.EPSILON);
        assertEquals(8d/39 * forThePeak, prediction[11], TestStatistics.EPSILON);
        assertEquals(5d/39 * forThePeak, prediction[12], TestStatistics.EPSILON);
    }

}
