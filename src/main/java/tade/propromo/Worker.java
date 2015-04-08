package tade.propromo;


import tade.propromo.predictor.Predictor;
import tade.propromo.predictor.UniformPredictor;
import tade.propromo.predictor.ZeroPredictor;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.Scanner;

/**
 *  Evaluation of the predictions
 *    Your results for the network structure learning will be evaluated using two criteria:
 *      link prediction accuracy and
 *      predictive distribution modelling accuracy.
 *
 *    For the first evaluation, you need to produce a ranked list of directed
 *    arcs in the network. These will be evaluated based on the area under the ROC curve.
 *
 *    For the second evaluation, you need to produce a predictive probability distribution for a
 *    given set of test data vectors. The produced distribution on the test data vectors is compared to
 *    the distribution produced by the gold standard model using a suitable distance metric.
 */
public class Worker extends Thread {

    public static final Predictor MY_BEST_PREDICTOR = new ZeroPredictor();

    protected Predictor predictor;
    protected int rows;
    protected int[][] previousValues;
    protected int round;

    private String currentRequest;
    private String currentContext;
    private String currentWork;
    private String currentInput;



    public Worker() {
        round = 0;
        rows = 1000;
        previousValues = new int[303][rows];
        predictor = MY_BEST_PREDICTOR;
    }

    public int getRound() { return round; }

    public void updateData(String currentRequest, String currentContext, String currentWork, String currentInput) {
        this.currentRequest = currentRequest;
        this.currentContext = currentContext;
        this.currentWork    = currentWork;
        this.currentInput   = currentInput;
    }

    @Override
    public void run() {

        BigDecimal[][] myGuess = new BigDecimal[rows][100];    // hundred values per line.

        if (round == 0) {

            for (int row=0; row<myGuess.length; ++row) {
                myGuess[row] = predictor.getFirstGuess();
            }

        } else {

            previousValues[round - 1] = getPreviousRoundValues();
            for (int row=0; row<myGuess.length; ++row) {
                myGuess[row] = predictor.predictRow(round, row, previousValues);
            }

        }

        writePredictionsToOutputFile(myGuess);

        round++;
    }

    protected void writePredictionsToOutputFile(BigDecimal[][] myGuess) {
        PrintWriter pw = null;

        try {
            pw = getPrintWriter();
            for (BigDecimal[] vector : myGuess) {
                if (Thread.currentThread().isInterrupted()) {
                    throw new InterruptedException();
                }
                pw.println(arrayToCSV(vector));
            }

        } catch (Exception e) {
            System.err.println("Oops, " + e.getMessage());
            e.printStackTrace();
            System.out.println("FAILED\t" + currentRequest + "\t" + currentWork + "/output/output.csv");
        } finally {
            pw.close();
        }
    }

    private PrintWriter getPrintWriter() throws FileNotFoundException {
        File outputDir = new File(currentWork + "/output");
        if (!outputDir.exists()) {
            outputDir.mkdir();
        }
        File outputFile = new File(outputDir, "output.csv");
        return new PrintWriter(outputFile);
    }

    public void cancel() {
        interrupt();
    }

    public static String arrayToCSV(BigDecimal[] a) {
        StringBuilder sb = new StringBuilder();
        for (BigDecimal d : a) {
            sb.append(d.toPlainString() + ",");
        }
        sb.deleteCharAt(sb.length() - 1);
        return sb.toString();
    }

    protected static int[] getValues(String line) {
        String[] parts = line.split(",");
        int[] result = new int[parts.length];
        for (int i = 0; i<parts.length; i++) {
            result[i] = Integer.parseInt(parts[i]);
        }
        return result;
    }

    protected int[] getPreviousRoundValues() {
        int[] previousRoundValues = new int[0];

        File inputFile = new File(currentInput).listFiles()[0];
        Scanner s = null;
        try {
            s = new Scanner(inputFile);
            previousRoundValues = getValues(s.nextLine());
        } catch (FileNotFoundException e) {
            System.err.println("Oops, " + e.getMessage());
            e.printStackTrace();
            System.out.println("FAILED\t" + currentRequest + "\t" + currentWork + "/output/output.csv");
        } finally {
            s.close();
        }

        return previousRoundValues;
    }

}
