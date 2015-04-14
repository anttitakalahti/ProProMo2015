package tade.propromo;

import org.junit.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;

import static junit.framework.Assert.assertEquals;


public class TestWorker {

    @Test
    public void testPrintOutputIsCalledTwice() {

        DummyWorker worker = new DummyWorker();


        worker.run();
        worker.run();

        assertEquals(2, worker.getOutput().size());

    }

    private class DummyWorker extends Worker {

        private ArrayList<BigDecimal[][]> output = new ArrayList<>();

        @Override
        protected int[] getPreviousRoundValues() {
            int[] previousRoundValues = new int[Worker.DEFAULT_ROWS];
            Arrays.fill(previousRoundValues, 0);
            return previousRoundValues;
        }

        @Override
        protected void writePredictionsToOutputFile(BigDecimal[][] myGuess) {
            output.add(myGuess);
        }

        public ArrayList<BigDecimal[][]> getOutput() { return output; }
    }

}
