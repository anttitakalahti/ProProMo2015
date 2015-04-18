package tade.propromo.predictor;

import org.junit.Test;

import java.io.IOException;
import java.math.BigDecimal;

import static junit.framework.Assert.assertEquals;

public class TestPositionPredictor {

    @Test
    public void testInitialPositionSumsToOne() throws IOException {
        PositionPredictor positionPredictor = new PositionPredictor();
        BigDecimal sum = BigDecimal.ZERO;
        for (BigDecimal prediction : positionPredictor.getFirstGuess()) {
            sum = sum.add(prediction);
        }
        assertEquals(0, sum.compareTo(BigDecimal.ONE));
    }

    @Test
    public void testAllPositionsSumToOne() throws IOException {
        PositionPredictor positionPredictor = new PositionPredictor();
        for (int position=0; position<303; ++position) {
            BigDecimal sum = BigDecimal.ZERO;
            for (BigDecimal prediction : positionPredictor.predictRow(position, new int[position])) {
                sum = sum.add(prediction);
            }
            assertEquals(0, sum.compareTo(BigDecimal.ONE));
        }
    }
}
