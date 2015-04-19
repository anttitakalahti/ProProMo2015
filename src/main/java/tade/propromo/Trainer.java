package tade.propromo;

import tade.propromo.predictor.*;

import java.io.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;

public class Trainer {

    public static final String TRAINING_DATA_FILE_NAME_ROUND_ONE = "data1.csv";
    public static final String TRAINING_DATA_FILE_NAME_ROUND_TWO = "data2.csv";

    public static final int TRAINING_DATA_ROWS_ROUND_ONE = 67785;
    public static final int TRAINING_DATA_ROWS_ROUND_TWO = 68760;

    public static void main(String[] args) throws Exception {


        int[][] testData = initializeTestDataFromFile(TRAINING_DATA_FILE_NAME_ROUND_TWO, 1000);

        double baseline = runWithWorker(new TrainingDataWorker(testData, new DuvinsPredictor()));
        double currentBestPredictor = runWithWorker(new TrainingDataWorker(testData, Worker.MY_BEST_PREDICTOR));
        double myScore = runWithWorker(new TrainingDataWorker(testData, new ExtendedPositionPredictor()));


        System.out.printf("Baseline is: %.4f for each guess. Current best gives %.4f and this one got: %.4f for each guess.\n",
                          baseline, currentBestPredictor, myScore);
    }

    private static double runWithWorker(TrainingDataWorker worker) throws Exception {
        for (int round=0; round<303; ++round) {
            worker.run();
        }
        return worker.calculateScore();
    }

    public static int[][] initializeTestDataFromFile(String fileName) throws IOException {
        int rows = TRAINING_DATA_ROWS_ROUND_TWO;
        if (fileName.equals(TRAINING_DATA_FILE_NAME_ROUND_ONE)) {
            rows = TRAINING_DATA_ROWS_ROUND_ONE;
        }
        return initializeTestDataFromFile(fileName, rows);
    }

    public static int[][] initializeTestDataFromFile(String fileName, int rows) throws IOException {
        int totalRows = TRAINING_DATA_ROWS_ROUND_TWO;
        if (fileName.equals(TRAINING_DATA_FILE_NAME_ROUND_ONE)) {
            totalRows = TRAINING_DATA_ROWS_ROUND_ONE;
        }
        HashSet<Integer> indexes = getRandomIndexes(rows, totalRows);

        int[][] testData = new int[rows][];
        File file = new File(Trainer.class.getClassLoader().getResource(fileName).getFile());
        FileReader fr = new FileReader(file);
        BufferedReader br = new BufferedReader(fr);
        String line;
        int rowIndex = 0;
        int testDataIndex = 0;
        while((line = br.readLine()) != null){
            if (indexes.contains(rowIndex++)) {
                testData[testDataIndex++] = Worker.getValues(line);
            }
        }
        br.close();
        fr.close();

        return testData;
    }

    public static HashSet<Integer> getRandomIndexes(int rows, int totalRows) {
        HashSet<Integer> indexes = new HashSet<>(rows);
        if (rows < totalRows) {
            ArrayList<Integer> validIndexes = new ArrayList<>(totalRows);
            for(int i=0; i<totalRows; ++i) {
                validIndexes.add(i);
            }
            Collections.shuffle(validIndexes);

            for (int i=0; i<rows; ++i) {
                indexes.add(validIndexes.get(i));
            }

        } else {
            for (int i=0; i<rows; ++i) {
                indexes.add(i);
            }
        }
        return indexes;
    }

}
