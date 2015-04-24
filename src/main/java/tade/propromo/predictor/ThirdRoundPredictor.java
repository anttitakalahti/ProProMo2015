package tade.propromo.predictor;

import tade.propromo.Statistics;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Arrays;

public class ThirdRoundPredictor implements Predictor {

    private Statistics statistics;

    public ThirdRoundPredictor() {
        statistics = new Statistics();
    }

    @Override
    public double[] getFirstGuess() {


        Double[] prediction = new Double[100];
        prediction[0] = statistics.getAverageZeroProbabilityForPosition(0);
        return splitForNulls(prediction, 1d - prediction[0]);


    }

    @Override
    public double[] predictRow(int round, int[] previousValues) {

        double zeroProbability = statistics.getAverageZeroProbabilityForPosition(round);

        if (zeroProbability > 0.95) {
            Double[] prediction = new Double[100];
            prediction[0] = zeroProbability;
            double saveForTheRest = 0.01;
            double remainingProbability = 1d - prediction[0] - saveForTheRest;

            int peaksSoFar = Statistics.peaks(previousValues);

            /*
                    Round 1:
                    1 peaks seen  4949 times.
                    2 peaks seen 41841 times.
                    3 peaks seen 19390 times.
                    4 peaks seen 1567 times.
                    5 peaks seen   36 times.
                    6 peaks seen    2 times.

                    Round 2:
                    1 peaks seen  5366 times.
                    2 peaks seen 42297 times.
                    3 peaks seen 19628 times.
                    4 peaks seen  1443 times.
                    5 peaks seen    26 times.
             */

            if (peaksSoFar == 0) {

            }

            double variance = statistics.getAverateVarianceForPosition(round);


            return splitForNulls(prediction, 1d - prediction[0]);
        }

        Double[] prediction = new Double[100];
        prediction[0] = zeroProbability;
        return splitForNulls(prediction, 1d - prediction[0]);
    }

    private double[] splitForNulls(Double[] prediction, double valueToBeShared) {
        long nulls = Arrays.stream(prediction).filter(d -> d == null).count();
        double[] doubles = new double[prediction.length];
        for (int i=0; i<doubles.length; ++i) {
            if (prediction[i] == null) {
                doubles[i] = valueToBeShared / nulls;
            } else {
                doubles[i] = prediction[i];
            }
        }
        return doubles;
    }
}
