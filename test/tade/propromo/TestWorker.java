package tade.propromo;

import org.junit.Test;


import java.util.ArrayList;

import static junit.framework.Assert.assertEquals;


public class TestWorker {

    private ArrayList<double[][]> output = new ArrayList<>();

    @Test
    public void testPrintOutputIsCalledTwice() {


        DummyWorker worker = new DummyWorker();


        worker.run();
        worker.run();

        assertEquals(2, output.size());

    }

    private class DummyWorker extends Worker {

        @Override
        protected int[] getPreviousRoundValues() {
            int[] previousRoundValues = new int[100];
            for (int i=0; i<previousRoundValues.length; ++i) {
                previousRoundValues[i] = i;
            }
            return previousRoundValues;
        }

        @Override
        protected void writePredictionsToOutputFile(double[][] myGuess) {
            output.add(myGuess);
        }
    }

}
