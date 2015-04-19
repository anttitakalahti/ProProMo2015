package tade.propromo.predictor;

import org.duvin.propromo2015.Example;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Arrays;
import java.util.Random;

/**
 * This is a wrapper for ExampleWorker
 */
public class DuvinsPredictor implements Predictor {

    @Override
    public double[] getFirstGuess() {
        return Example.getRandomDist();
    }

    @Override
    public double[] predictRow(int round, int[] previousValues) {
        return Example.basedOnLastValue(previousValues[round - 1]);
    }

}
