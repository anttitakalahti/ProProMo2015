package tade.propromo.predictor;

import org.junit.Test;

import java.io.IOException;
import java.math.BigDecimal;

import static junit.framework.Assert.assertEquals;

public class TestPositionPredictor {

    @Test
    public void testInitialPositionSumsToOne() throws IOException {
        PositionPredictor positionPredictor = new PositionPredictor();
        double sum = 0d;
        for (double prediction : positionPredictor.getFirstGuess()) {
            sum += prediction;
        }
        assertEquals(1d, sum);
    }

    @Test
    public void testAllPositionsSumToOne() throws IOException {
        PositionPredictor positionPredictor = new PositionPredictor();
        for (int position=0; position<303; ++position) {
            double sum = 0d;
            for (double prediction : positionPredictor.predictRow(position, new int[position])) {
                sum += prediction;
            }
            assertEquals(1d, sum);
        }
    }
}
