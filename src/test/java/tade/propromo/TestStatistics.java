package tade.propromo;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class TestStatistics {

    public static final double EPSILON = 1e-5;

    int[][] data;

    @Before
    public void initialize() {
        data = new int[3][];
        data[0] = new int[]{0, 2, 1, 0};
        data[1] = new int[]{3, 2, 0, 1};
        data[2] = new int[]{1, 2, 3, 4};
    }

    @Test
    public void testCalculateVariance() {
        int[] twos = new int[] {2, 2, 2};
        assertEquals(0d, Statistics.calculateVariance(twos, 2), EPSILON);

        int[] oneTwoThree = new int[] {1, 2, 3};
        assertEquals(1d, Statistics.calculateVariance(oneTwoThree, 2), EPSILON);

        int[] tenTwentyThirty = new int[] {10, 20, 30};
        assertEquals(100d, Statistics.calculateVariance(tenTwentyThirty, 20), EPSILON);
    }

    @Test
    public void testGetNonZeroPositionValues() {
        int[] nonZeroValues = Statistics.getNonZeroPositionValues(2, data);
        assertEquals(2, nonZeroValues.length);
        assertEquals(1, nonZeroValues[0]);
        assertEquals(3, nonZeroValues[1]);

        nonZeroValues = Statistics.getNonZeroPositionValues(1, data);
        assertEquals(3, nonZeroValues.length);
        assertEquals(2, nonZeroValues[0]);
        assertEquals(2, nonZeroValues[1]);
        assertEquals(2, nonZeroValues[2]);
    }

}
