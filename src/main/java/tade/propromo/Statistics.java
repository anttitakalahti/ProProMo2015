package tade.propromo;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;

public class Statistics {

    public static final int ROUNDS = 2;
    public static final int COLS = 303;
    public static final String CHARSET_NAME = "UTF-8";
    public static final String STATISTICS_RESOURCES_FOLDER = "statistics";
    public static final String FILE_NAME_COL_MEANS = "col_means_round_";
    public static final String FILE_NAME_COL_VARIANCES = "col_variances_round_";
    public static final String FILE_NAME_COL_ZERO_PROBABILITIES = "col_zero_probabilities_round_";
    public static final String FILE_NAME_ROW_PEAKS = "row_peaks_";

    private ArrayList<double[]> colVariances;
    private ArrayList<double[]> colZeroProbabilities;

    public Statistics() {
        colVariances = new ArrayList<>();
        colZeroProbabilities = new ArrayList<>();

        IntStream.rangeClosed(0, ROUNDS)
                .forEach(i -> {
                    colVariances.add(null);
                    colZeroProbabilities.add(null);
                });
    }

    public double getAverateVarianceForPosition(int position) {
        return IntStream.rangeClosed(1,ROUNDS).mapToDouble(round -> getColVariances(round)[position]).average().getAsDouble();
    }

    public double[] getColVariances(int round) {
        if (colVariances.get(round) != null) { return colVariances.get(round); }
        colVariances.set(round, readDoublesFromFile(FILE_NAME_COL_VARIANCES, round, COLS));
        return colVariances.get(round);
    }

    public double getAverageZeroProbabilityForPosition(int position) {
        return IntStream.rangeClosed(1,ROUNDS).mapToDouble(round -> getColZeroProbabilities(round)[position]).average().getAsDouble();
    }

    public double[] getColZeroProbabilities(int round) {
        if (colZeroProbabilities.get(round) != null ) { return colZeroProbabilities.get(round); }
        colZeroProbabilities.set(round, readDoublesFromFile(FILE_NAME_COL_ZERO_PROBABILITIES, round, COLS));
        return colZeroProbabilities.get(round);
    }

    private double[] readDoublesFromFile(String filePrefix, int round, int count) {
        try {

            List<String> lines = Files.readAllLines(Paths.get(uriForPrefixAndRound(filePrefix, round)), Charset.forName(CHARSET_NAME));
            return doublesFromStrings(lines);

        } catch (IOException e) {
            e.printStackTrace();
            System.exit(2);
        }

        return null;
    }

    public void writeStats(int round, int[][] data) {
        writeColData(round, data);
        writeRowData(round, data);
    }

    private void writeColData(int round, int[][] data) {
        double[] colMeans = new double[COLS];
        double[] colVariances = new double[COLS];
        double[] colZeroProbabilities = new double[COLS];

        for (int position=0; position<COLS; ++position) {
            int[] nonZeroValues = getNonZeroPositionValues(position, data);

            if (nonZeroValues.length == 0) {
                colMeans[position] = 0d;
                colVariances[position] = 0d;
                colZeroProbabilities[position] = 1d;
                continue;
            }

            colMeans[position] = Arrays.stream(nonZeroValues).average().getAsDouble();
            colVariances[position] = calculateVariance(nonZeroValues, colMeans[position]);
            colZeroProbabilities[position] = 1d - ((double)nonZeroValues.length / data.length);
        }

        writeArrayToFile(FILE_NAME_COL_MEANS, round, colMeans);
        writeArrayToFile(FILE_NAME_COL_VARIANCES, round, colVariances);
        writeArrayToFile(FILE_NAME_COL_ZERO_PROBABILITIES, round, colZeroProbabilities);
    }




    private void writeRowData(int round, int[][] data) {
        double[] roundMeans = new double[data.length];
        double[] roundVariances = new double[data.length];
        int[] peaksPerRow = new int[100];

        int index = 0;
        for (int[] row : data) {
            int[] nonZeroValues = Arrays.stream(row)
                    .filter(x -> x > 0)
                    .toArray();

            roundMeans[index] = Arrays.stream(nonZeroValues).average().getAsDouble();
            roundVariances[index] = calculateVariance(nonZeroValues, roundMeans[index]);
            peaksPerRow[peaks(row)]++;

            index++;
        }

        writeArrayToFile(FILE_NAME_ROW_PEAKS, round, peaksPerRow);
    }

    // http://www.wolframalpha.com/input/?i=variance+of+1%2C2%2C3&lk=4&num=1
    protected static double calculateVariance(int[] values, double mean) {
        return Arrays.stream(values)
                .asDoubleStream()
                .map(x -> (x - mean) * (x - mean))
                .sum() / (values.length - 1);
    }

    protected static int[] getNonZeroPositionValues(int position, int[][] data) {
        return Arrays.stream(data)
                .flatMapToInt(x -> IntStream.of(x[position]))
                .filter(x -> x > 0)
                .toArray();
    }

    public static int peaks(int[] row) {
        int[] nonZeroRowValues = Arrays.stream(row).filter(i -> i > 0).sorted().toArray();
        if (nonZeroRowValues.length == 0) { return 0; }

        int peaks = 0;
        int prevValue = -10;
        for (int value : nonZeroRowValues) {
            if (value > prevValue + 5) { ++peaks; }
            prevValue = value;
        }
        return peaks;
    }

    /**
     * Surprise! target is file:/Users/tade/Code/ProProMo2015/target/classes/col_means_round_1.txt
     *
     * @param fileNamePrefix like "col_means_round_"
     * @param round round.txt is added to the end of the prefix
     * @param values
     * @throws URISyntaxException
     * @throws IOException
     */
    private void writeArrayToFile(String fileNamePrefix, int round, double[] values) {
        writeStringsToFile(uriForPrefixAndRound(fileNamePrefix, round), doublesToStrings(values));
    }

    private void writeArrayToFile(String fileNamePrefix, int round, int[] values) {
        writeStringsToFile(uriForPrefixAndRound(fileNamePrefix, round), intsToStrings(values));
    }

    private void writeStringsToFile(URI uri, Iterable<String> strings) {
        try {

            Files.write(Paths.get(uri), strings, Charset.forName(CHARSET_NAME), StandardOpenOption.CREATE);

        } catch (IOException e) {
            e.printStackTrace(System.out);
            System.exit(2);
        }
    }

    private URI uriForPrefixAndRound(String prefix, int round) {
        try {

            return Trainer.class.getClassLoader().getResource(prefix + round + ".txt").toURI();

        } catch(URISyntaxException e) {
            e.printStackTrace(System.out);
            System.exit(1);
        }
        return null;
    }

    private Iterable<String> intsToStrings(int[] values) {
        ArrayList<String> strings = new ArrayList<>();
        for (int i : values) {
            strings.add("" + i);
        }
        return strings;
    }

    private Iterable<String> doublesToStrings(double[] values) {
        ArrayList<String> strings = new ArrayList<>();
        for (double d : values) {
            strings.add(String.format("%.10f", d));
        }
        return strings;
    }

    private double[] doublesFromStrings(List<String> lines) {
        double[] values = new double[lines.size()];
        int index = 0;
        for (String line : lines) {
            values[index++] = Double.parseDouble(line);
        }
        return values;
    }

    public static void main(String[] args) throws IOException {
        Statistics statistics = new Statistics();
        int[][] data;

        data = Trainer.initializeTestDataFromFile(Trainer.TRAINING_DATA_FILE_NAME_ROUND_ONE);
        statistics.writeStats(1, data);

        data = Trainer.initializeTestDataFromFile(Trainer.TRAINING_DATA_FILE_NAME_ROUND_TWO);
        statistics.writeStats(2, data);
    }

}
