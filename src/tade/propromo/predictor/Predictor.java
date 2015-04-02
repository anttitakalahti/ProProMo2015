package tade.propromo.predictor;

public interface Predictor {
    public double[] getFirstGuess();
    public double[] predictRow(int round, int row, int[][] previousValues);
}
