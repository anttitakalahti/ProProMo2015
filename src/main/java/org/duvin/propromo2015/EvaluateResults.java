/*
 * (c) Johannes Verwijnen, duvin@duvin.org
 */
package org.duvin.propromo2015;

import java.io.File;
import java.io.PrintWriter;
import java.util.Scanner;
import static org.duvin.propromo2015.Example.arrayToCSV;

/**
 *
 * @author Johannes Verwijnen <duvin@duvin.org>
 */
public class EvaluateResults {

    public static double epsilon = 1e-5;

    public static void main(String[] args) throws Exception {
        double[][] score = new double[1000][303];
        PrintWriter pw = new PrintWriter(args[0]);
        for (int round = 1; round < 304; round++) {
            double[][] predictions = readPredictions(args[1] + round + "/output");
            int[] values = readValues(args[2] + round);
            for (int vector = 0; vector < 1000; vector++) {
                score[vector][round-1] = Math.log(predictions[vector][values[vector]]);
            }
        }
        for (double[] vector : score) {
            pw.println(Example.arrayToCSV(vector));
        }
        pw.close();
    }

    public static double[][] readPredictions(String filename) throws Exception {
        //System.out.println("reading " + filename);
        double[][] predictions = new double[1000][100];
        File dir = new File(filename);
        File f = dir.listFiles()[0];
        Scanner s = new Scanner(f);
        int row = 0;
        while (s.hasNextLine()) {
            String[] dist = s.nextLine().split(",");
            for (int i = 0; i < 100; i++) {
                predictions[row][i] = Double.parseDouble(dist[i]);
            }
            double sum = rowsum(predictions[row]);
            if (sum!=0 && Math.abs(sum - 1) > epsilon) {
                System.out.println("row:"+ row + " sum:" + sum + "\tnormalizing...");
                normalize(predictions[row], sum);
            }
            row++;
        }
        s.close();
        return predictions;
    }

    public static void normalize(double[] row, double factor) {
        for (int i = 0; i < row.length; i++) {
            row[i] /= factor;
        }
    }

    public static double rowsum(double[] row) {
        double sum = 0;
        for (double d : row) {
            sum += d;
        }
        return sum;
    }

    public static int[] readValues(String filename) throws Exception {
        int[] values = new int[1000];
        Scanner s = new Scanner(new File(filename + "/in.txt"));
        String[] row = s.nextLine().split(",");
        for (int i = 0; i < 1000; i++) {
            values[i] = Integer.parseInt(row[i]);
        }
        s.close();
        return values;
    }

}
