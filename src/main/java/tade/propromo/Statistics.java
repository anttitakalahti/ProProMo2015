package tade.propromo;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Statistics {

    public static final int ROUNDS = 3;
    public static final int COLS = 303;

    public static final int PRE_CALCULATED_PROBABILITIES_PADDING = 10;

    public static final String CHARSET_NAME = "UTF-8";
    public static final String FILE_NAME_COL_MEANS = "col_means_round_";
    public static final String FILE_NAME_COL_VARIANCES = "col_variances_round_";
    public static final String FILE_NAME_COL_ZERO_PROBABILITIES = "col_zero_probabilities_round_";
    public static final String FILE_NAME_PREDICTOR_MATRIX = "predictor_matrix_";
    public static final String FILE_NAME_PRE_CALCULATED_VALUE_PROBABILITIES = "pre_calculated_probabilities_";

    private ArrayList<double[][]> previousProbabilities;
    private ArrayList<double[]> colZeroProbabilities;
    private ArrayList<double[][]> valuePredictors;

    public Statistics() {
        previousProbabilities = new ArrayList<>();
        colZeroProbabilities = new ArrayList<>();
        valuePredictors = new ArrayList<>();

        IntStream.rangeClosed(0, ROUNDS)
                .forEach(i -> {
                    previousProbabilities.add(null);
                    colZeroProbabilities.add(null);
                    valuePredictors.add(null);
                });
    }

    /**
     * Be aware of this returning 1d
     * @param position
     * @return
     */
    public double getAverageZeroProbabilityForPosition(final int position) {
        return IntStream.rangeClosed(1,ROUNDS)
                .mapToDouble(round -> getColZeroProbabilities(round)[position])
                .average()
                .getAsDouble();
    }

    private double[] getColZeroProbabilities(int round) {
        if (colZeroProbabilities.get(round) != null ) { return colZeroProbabilities.get(round); }

        colZeroProbabilities.set(round, readDoublesFromFile(FILE_NAME_COL_ZERO_PROBABILITIES, round));

        return colZeroProbabilities.get(round);
    }

    public double getAveragedPrediction(final int seenPosition, final int unseenPosition) {
        return IntStream.rangeClosed(1, ROUNDS)
                .mapToDouble(round -> getPrediction(round, seenPosition, unseenPosition))
                .filter(d -> d > 0d)
                .average()
                .orElse(0d);
    }

    private double getPrediction(int round, int seenPosition, int unseenPosition) {
        if (valuePredictors.get(round) != null) { return valuePredictors.get(round)[seenPosition][unseenPosition]; }

        valuePredictors.set(round, readMatrixFromFile(FILE_NAME_PREDICTOR_MATRIX, round));

        return valuePredictors.get(round)[seenPosition][unseenPosition];
    }

    public double[] getPreviousProbabilities(int round, int position) {
        if (previousProbabilities.get(round) != null) { return previousProbabilities.get(round)[position]; }

        previousProbabilities.set(round, readMatrixFromFile(FILE_NAME_PRE_CALCULATED_VALUE_PROBABILITIES, round));

        return previousProbabilities.get(round)[position];
    }

    private double[] readDoublesFromFile(String filePrefix, int round) {
        try {

            List<String> lines = Files.readAllLines(Paths.get(uriForPrefixAndRound(filePrefix, round)), Charset.forName(CHARSET_NAME));
            return doublesFromStrings(lines);

        } catch (IOException e) {
            System.out.println("FAILED TO READ FILE: " + filePrefix + round + ".txt");
            e.printStackTrace();
            System.exit(2);
        }

        throw new RuntimeException("System.exit() failed.");
    }

    private double[][] readMatrixFromFile(String filePrefix, int round) {
        try {

            List<String> lines = Files.readAllLines(Paths.get(uriForPrefixAndRound(filePrefix, round)));
            double[][] matrix = new double[lines.size()][];
            int index = 0;
            for (String line : lines) {
                matrix[index++] = doubleArrayFromString(line);
            }
            return matrix;

        } catch (IOException e) {
            System.out.println("FAILED TO READ FILE: " + filePrefix + round + ".txt");
            e.printStackTrace();
            System.exit(2);
        }

        throw new RuntimeException("System.exit() failed.");
    }

    public void writeStats(int round, int[][] data) {
        writeColData(round, data);
        writeRowData(round, data);
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

    private void writeRowData(int round, int[][] data) {

        int[][] valueCountsPerPosition = new int[COLS][];
        for (int position = 0; position < valueCountsPerPosition.length; ++position) {
            valueCountsPerPosition[position] = new int[100];
            Arrays.fill(valueCountsPerPosition[position], PRE_CALCULATED_PROBABILITIES_PADDING);
        }


        for (int[] row : data) {
            for (int position = 0; position < row.length; ++position) {
                int value = row[position];
                valueCountsPerPosition[position][value] += 1;
            }
        }


        double[][] valueProbabilitiesPerPosition = new double[COLS][];
        for (int position = 0; position < valueProbabilitiesPerPosition.length; ++position) {

            double[] valueProbabilities = new double[100];
            for (int value = 0; value < valueCountsPerPosition[position].length; ++value) {
                valueProbabilities[value] = (double)valueCountsPerPosition[position][value] / (100 * PRE_CALCULATED_PROBABILITIES_PADDING + data.length);
            }
            valueProbabilitiesPerPosition[position] = valueProbabilities;

        }

        writeMatrixToFile(FILE_NAME_PRE_CALCULATED_VALUE_PROBABILITIES, round, valueProbabilitiesPerPosition);
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
            System.out.println("uriForPrefixAndRound(" + prefix + ", " + round + ") failed.");
            e.printStackTrace(System.out);
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

    private double[] doubleArrayFromString(String line) {
        String[] s = line.split(",");
        double[] d = new double[s.length];

        for (int i = 0; i < d.length; ++i) {
            d[i] = Double.parseDouble(s[i]);
        }
        return d;
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
