package tade.propromo;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
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

    private String currentRequest;
    private String currentContext;
    private String currentWork;
    private String currentInput;
    private static int[][] context;
    private int round;

    public Worker() {
        context = new int[303][1000];
        round = 0;
    }

    public void updateData(String command) {
        String[] parts = command.split("\\t");

        currentRequest = parts[1];
        currentContext = parts[2];
        currentWork    = parts[3];
        currentInput   = parts[4];
    }

    @Override
    public void run() {

        double[][] myGuess = new double[1000][100];    // thousand rows, hundred values per row.

        if (round == 0) {

            for (double[] row : myGuess) {
                row = Predictor.getFirstGuess();
            }

        } else {

            context[round - 1] = getPreviousRoundValues();
            for (int i=0; i<myGuess.length; ++i) {
                myGuess[i] = Predictor.predictRow(round, i, context);
            }

        }

        writePredictionsToOutputFile(myGuess);

        round++;
    }

    protected void writePredictionsToOutputFile(double[][] myGuess) {
        PrintWriter pw = null;

        try {
            pw = getPrintWriter();
            for (double[] vector : myGuess) {
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

    public static String arrayToCSV(double[] a) {
        StringBuilder sb = new StringBuilder();
        for (double d : a) {
            sb.append("" + d + ",");
        }
        sb.deleteCharAt(sb.length() - 1);
        return sb.toString();
    }

    private static int[] getValues(String line) {
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
