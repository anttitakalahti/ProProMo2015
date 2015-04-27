package tade.propromo.predictor;

import tade.propromo.Peak;
import tade.propromo.Statistics;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class ThirdRoundPredictor implements Predictor {

    private Statistics statistics;

    public ThirdRoundPredictor() {
        statistics = new Statistics();
    }

    @Override
    public double[] getFirstGuess() {

        Double[] prediction = new Double[100];
        prediction[0] = Math.min(0.99999, statistics.getAverageZeroProbabilityForPosition(0));
        return splitRemainingProbabilityForNulls(prediction);

    }

    @Override
    public double[] predictRow(int round, int[] previousValues) {

        return statistics.getPreviousProbabilities(3, round);

    }

    private double calculateConfidence(int[] previousValues, int unseenPosition) {
        double confidence = 0d;
        for (int i = 0; i < previousValues.length; ++i) {
            if (previousValues[i] == 0) { continue; }
            confidence = Math.max(confidence, statistics.getAveragedPrediction(i, unseenPosition));
        }
        return confidence;
    }

    private Double[] predictPeak(Double[] prediction, Peak peak, double probabilityForPeak, int[] previousValues) {

        int lastSeenValueInThisPeak = getLastSeenValueInThisPeak(peak, previousValues);

        if (lastSeenValueInThisPeak > 97) { lastSeenValueInThisPeak = 97; } // -- || --

        IntStream.rangeClosed(lastSeenValueInThisPeak, lastSeenValueInThisPeak + 2).forEach(index -> {
            if (prediction[index] == null) {
                prediction[index] = 0d;
            }
        });

        prediction[lastSeenValueInThisPeak]     += 2d/3  * probabilityForPeak;
        prediction[lastSeenValueInThisPeak + 1] += 8d/39 * probabilityForPeak;
        prediction[lastSeenValueInThisPeak + 2] += 5d/39 * probabilityForPeak;

        return prediction;
    }

    private int getLastSeenValueInThisPeak(Peak peak, int[] previousValues) {
        for (int i = previousValues.length - 1; i >= 0; --i) {
            if (previousValues[i] == 0) { continue; }
            if (peak.containsValue(previousValues[i])) { return previousValues[i]; }
        }

        return 66; // I love 66
    }

    private double[] splitRemainingProbabilityForNulls(Double[] prediction) {
        long nulls = 0;
        double valueToBeShared = 1d;
        for (Double d : prediction) {
            if (d == null) {
                ++nulls;
            } else {
                valueToBeShared -= d;
            }
        }

        if (valueToBeShared <= 0d) {
            throw new RuntimeException("Value is too small to be shared: " + valueToBeShared);
        }

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
