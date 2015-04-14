package tade.propromo.predictor;

import tade.propromo.Worker;

import java.io.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Arrays;

public class PositionPredictor implements Predictor {

    public static final int ROWS = 67785;
    public static final int COLS = 303;

    public static final String TRAINING_FILE_PATH = "data1.csv";

    private int[][] trainingData;
    private BigDecimal[] zeroProbabilities;

    public PositionPredictor() throws IOException {
        initializeTrainingData();
        countZeroProbabilities();
    }

    private void initializeTrainingData() throws IOException {
        trainingData = new int[ROWS][];
        ClassLoader classLoader = getClass().getClassLoader();
        File file = new File(classLoader.getResource(TRAINING_FILE_PATH).getFile());
        FileReader fr = new FileReader(file);
        BufferedReader br = new BufferedReader(fr);
        String line;
        int index = 0;
        while((line = br.readLine()) != null){
            trainingData[index++] = Worker.getValues(line);
        }
        br.close();
        fr.close();
    }

    private void countZeroProbabilities() {
        int[] counts = new int[COLS];
        Arrays.fill(counts, 0);
        for (int[] row : trainingData) {
            for (int position=0; position < row.length; ++position) {
                if (row[position] == 0) { counts[position] += 1; }
            }
        }

        zeroProbabilities = new BigDecimal[COLS];
        Arrays.fill(zeroProbabilities, BigDecimal.ZERO);

        for (int position=0; position<COLS; ++position) {
            zeroProbabilities[position] = new BigDecimal(counts[position]).divide(new BigDecimal(trainingData.length), SCALE, RoundingMode.HALF_UP);
        }
    }

    @Override
    public BigDecimal[] getFirstGuess() {
        return predictPosition(0);
    }

    @Override
    public BigDecimal[] predictRow(int round, int row, int[][] previousValues) {
        return predictPosition(round);
    }

    protected BigDecimal[] predictPosition(int position) {
        BigDecimal[] predictions = new BigDecimal[100];
        Arrays.fill(predictions, MINIMAL);


        if (zeroProbabilities[position].compareTo(new BigDecimal(99).divide(new BigDecimal(100))) > 0) {
            predictions[0] = BigDecimal.ONE;
        } else {
            System.out.println(position);
        }

        return normalize(predictions);
    }
}
