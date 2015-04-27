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
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Statistics {

    public static final int ROUNDS = 3;
    public static final int COLS = 303;
    public static final String CHARSET_NAME = "UTF-8";
    public static final String STATISTICS_RESOURCES_FOLDER = "statistics";
    public static final String FILE_NAME_COL_MEANS = "col_means_round_";
    public static final String FILE_NAME_COL_VARIANCES = "col_variances_round_";
    public static final String FILE_NAME_COL_ZERO_PROBABILITIES = "col_zero_probabilities_round_";
    public static final String FILE_NAME_PREDICTOR_MATRIX = "predictor_matrix_";

    private ArrayList<double[]> colVariances;
    private ArrayList<double[]> colZeroProbabilities;
    private ArrayList<double[][]> valuePredictors;

    public Statistics() {
        colVariances = new ArrayList<>();
        colZeroProbabilities = new ArrayList<>();
        valuePredictors = new ArrayList<>();

        IntStream.rangeClosed(0, ROUNDS)
                .forEach(i -> {
                    colVariances.add(null);
                    colZeroProbabilities.add(null);
                    valuePredictors.add(null);
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



    /**
     * Be aware of this returning 1d
     * @param position
     * @return
     */
    public double getAverageZeroProbabilityForPosition(int position) {
        return IntStream.rangeClosed(1,ROUNDS).mapToDouble(round -> getColZeroProbabilities(round)[position]).average().getAsDouble();
    }

    private double[] getColZeroProbabilities(int round) {
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
        writePredictorMatrix(round, data);
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

    private void writePredictorMatrix(int round, int[][] data) {

        double[][] matrix = new double[302][];

        for (int i = 0; i < matrix.length; ++i) {

            final int position = i;

            List<int[]> rowsWithValueInPosition = Arrays.stream(data)
                    .filter(row -> row[position] > 0)
                    .collect(Collectors.toList());

            int[] predictedCounts = new int[303];

            for (int[] row : rowsWithValueInPosition) {
                for (int j = position + 1; j < 303; ++j) {
                    if (row[j] > 0) {
                        predictedCounts[j]++;
                    }
                }
            }

            matrix[position] = new double[303];
            Arrays.fill(matrix[position], 0d);

            for (int j = position + 1; j < 303; ++j) {
                if (rowsWithValueInPosition.isEmpty()) { continue; }
                matrix[position][j] = (double)predictedCounts[j] / rowsWithValueInPosition.size();
            }

        }

        writeMatrixToFile(FILE_NAME_PREDICTOR_MATRIX, round, matrix);
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

    private void writeMatrixToFile(String fileNamePrefix, int round, double[][] matrix) {
        writeStringsToFile(uriForPrefixAndRound(fileNamePrefix, round), matrixToStrings(matrix));
    }

    private void writeStringsToFile(URI uri, Iterable<String> strings) {
        try {

            Files.write(Paths.get(uri), strings, Charset.forName(CHARSET_NAME));

        } catch (IOException e) {
            e.printStackTrace(System.out);
            System.exit(2);
        }
    }

    private URI uriForPrefixAndRound(String prefix, int round) {
        try {

            return Trainer.class.getClassLoader().getResource(prefix + round + ".txt").toURI();

        } catch(NullPointerException e) {
            System.out.println("Failed to write file: " + prefix + round + ".txt");
            System.exit(3);
        } catch(URISyntaxException e) {
            e.printStackTrace(System.out);
            System.exit(1);
        }
        return null;
    }

    private Iterable<String> doublesToStrings(double[] values) {
        ArrayList<String> strings = new ArrayList<>();
        for (double d : values) {
            strings.add(String.format("%.10f", d));
        }
        return strings;
    }

    private Iterable<String> matrixToStrings(double[][] matrix) {
        ArrayList<String> strings = new ArrayList<>();
        for (double[] doubles : matrix) {
            strings.add(Worker.arrayToCSV(doubles));
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

        data = Trainer.initializeTestDataFromFile(Trainer.TRAINING_DATA_FILE_NAME_ROUND_THREE);
        statistics.writeStats(3, data);
    }

}
