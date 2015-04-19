/*
 * (c) Johannes Verwijnen, duvin@duvin.org
 */
package org.duvin.propromo2015;

import java.io.File;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Arrays;
import java.util.Locale;
import java.util.Random;
import java.util.Scanner;

/**
 *
 * @author Johannes Verwijnen <duvin@duvin.org>
 */
public class Example {

    private static String refineryId = "";
    private static String currentContext;
    private static String currentRequest;
    private static String currentInput;
    private static String currentWork;
    private static ExampleWorker worker;
    private static int[][] context;
    private static int round = 0;
    private static DecimalFormat df = new DecimalFormat();

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        

        Scanner s = new Scanner(System.in);
        //let repository know we're alive
        System.out.println("HELLO\t1.0");

        while (true) {
            String command = s.nextLine();
            String[] parts = command.split("\\t");
            if (command.startsWith("SETUP\t")) {
                refineryId = parts[1];
                System.out.println("ALIVE\t" + refineryId + "\tinfo=Test program\tversion=0.666");
            } else if (command.startsWith("PERFORM\t") && !refineryId.isEmpty()) {
                currentRequest = parts[1];
                currentContext = parts[2];
                currentWork = parts[3];
                currentInput = parts[4];
                worker = new ExampleWorker();
                worker.start();
            } else if (command.startsWith("ABORT\t") && !refineryId.isEmpty()) {
                worker.cancel();
            } else if (command.startsWith("PING\t")) {
                System.out.println("PONG\t" + parts[1]);
            } else {
                System.err.println("Uhhuh!\nReceived: " + command);
                System.exit(0);
            }
        }
    }

    public static String arrayToCSV(double[] a) {
        df.setMaximumFractionDigits(340);
        df.setMinimumFractionDigits(1);
        df.setDecimalFormatSymbols(new DecimalFormatSymbols(Locale.US));
        StringBuilder sb = new StringBuilder();
        for (double d : a) {
            sb.append(df.format(d)).append(",");
        }
        sb.deleteCharAt(sb.length() - 1);
        return sb.toString();
    }

    public static double[] getRandomDist() {
        Random r = new Random();
        double[] result = new double[100];
        double sum = 0;
        double value;
        for (int i = 0; i < 100; i++) {
            result[i] = value = r.nextDouble();
            sum += value;
        }
        //normalize
        for (int i = 0; i < 100; i++) {
            result[i] /= sum;
        }
        return result;
    }

    private static int[] getValues(String line) {
        String[] parts = line.split(",");
        int[] result = new int[parts.length];
        for (int i = 0; i < parts.length; i++) {
            result[i] = Integer.parseInt(parts[i]);
        }
        return result;
    }

    public static double[] basedOnLastValue(int last) {
        double minimal = 0.0000001;
        double[] result = new double[100];
        //make sure we don't have 0 probability
        Arrays.fill(result, minimal);
        if (last == 0) {
            //put all but minimal to bet on zero
            result[0] = 1 - 99 * minimal;
        } else {
            //25% to adjacent values, 49.999% to previous
            result[last] = .5 - 97 * minimal;
            result[last - 1] = .25;
            result[last + 1] = .25;
        }
        return result;
    }

    public static class ExampleWorker extends Thread {

        @Override
        public void run() {
            double[][] myGuess = new double[1000][100];
            File inputDir = new File(currentInput);
            //just take first file
            File inputFile = inputDir.listFiles()[0];
            try {
                Scanner s = new Scanner(inputFile);
                File outputDir = new File(currentWork + "/output");
                if (!outputDir.exists()) {
                    outputDir.mkdir();
                }
                File outputFile = new File(outputDir, "output.csv");
                PrintWriter pw = new PrintWriter(outputFile);
                if (round++ == 0) {
                    //don't have context yet
                    context = new int[303][1000];

                    //return dist over first column for each vector
                    double[] randomVector = getRandomDist();
                    for (double[] vector : myGuess) {
                        if (Thread.currentThread().isInterrupted()) {
                            throw new InterruptedException();
                        }
                        vector = randomVector;
                    }
                } else {
                    //read context
                    context[round - 2] = getValues(s.nextLine());
                    //now calculate based on context
                    for (int i = 0; i < 1000; i++) {
                        myGuess[i] = basedOnLastValue(context[round - 2][i]);
                    }
                }
                for (double[] vector : myGuess) {
                    if (Thread.currentThread().isInterrupted()) {
                        throw new InterruptedException();
                    }
                    pw.println(arrayToCSV(vector));
                }
                pw.close();
                s.close();
                System.out.println("READY\t" + currentRequest + "\t" + currentWork + "/output/output.csv");
            } catch (InterruptedException e) {
                System.out.println("ABORTED\t" + currentRequest);
            } catch (Exception e) {
                System.err.println("Oops, " + e.getMessage());
                e.printStackTrace();
                System.out.println("FAILED\t" + currentRequest + "\t" + currentWork + "/output/output.csv");
            }
        }

        public void cancel() {
            interrupt();
        }

    }

}
