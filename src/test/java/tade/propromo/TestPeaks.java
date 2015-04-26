package tade.propromo;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class TestPeaks {

    @Test
    public void testPeaksPerRow() {
        assertEquals(0, Peak.peaksPerRow(new int[] {0,   0, 0,  0}).size());
        assertEquals(1, Peak.peaksPerRow(new int[] {0,  10, 0, 10}));
        assertEquals(2, Peak.peaksPerRow(new int[] {0,  10, 0, 50}));
        assertEquals(1, Peak.peaksPerRow(new int[] {10, 11, 9, 12}));
    }
}
