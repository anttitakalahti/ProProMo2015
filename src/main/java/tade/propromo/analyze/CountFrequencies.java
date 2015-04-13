package tade.propromo.analyze;

import tade.propromo.Worker;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;

public class CountFrequencies {

    public static final int ROWS = 67785;
    public static final int COLS = 303;

    public static final String TRAINING_FILE_PATH = "/Users/tade/Code/ProProMo2015/data/data1.txt";

    private static int[][] trainingData;

    public static void main(String[] args) throws IOException {
        loadTrainingData();

        double[] zeroProbabilities = howOftenPositionHasZero();
        for (int position=0; position < zeroProbabilities.length; ++position) {
            System.out.printf("%d: %.4f\n", position, zeroProbabilities[position]);
        }

        double[] valueProbabilities = howOftenValueAppears();
        for (int value=0; value < 100; ++value) {
            System.out.printf("Value %d has probability of %.4f \n", value, valueProbabilities[value]);
        }

    }

    private static void loadTrainingData() throws IOException {
        trainingData = new int[ROWS][];
        File file = new File(TRAINING_FILE_PATH);
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

    public static double[] howOftenPositionHasZero() {
        int[] counts = new int[COLS];
        Arrays.fill(counts, 0);
        for (int[] row : trainingData) {
            for (int position=0; position < row.length; ++position) {
                if (row[position] == 0) { counts[position] += 1; }
            }
        }

        double[] probabilities = new double[COLS];
        for (int position=0; position<COLS; ++position) {
            probabilities[position] = (double)counts[position] / ROWS;
        }
        return probabilities;
    }

    public static double[] howOftenValueAppears() {
        long[] counts = new long[100];
        Arrays.fill(counts, 0L);
        for (int row=1; row<trainingData.length; ++row) {
            for (int position=0; position < trainingData[row].length; ++position) {
                counts[trainingData[row][position]] += 1;
            }
        }

        double[] probabilities = new double[100];
        for (int position=0; position<100; ++position) {
            probabilities[position] = (double)counts[position] / (ROWS * COLS);
        }
        return probabilities;
    }
}
