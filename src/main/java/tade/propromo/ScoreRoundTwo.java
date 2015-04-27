package tade.propromo;

import tade.propromo.predictor.Predictor;
import tade.propromo.predictor.RoundTwoPredictor;
import tade.propromo.predictor.ThirdRoundPredictor;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;

public class ScoreRoundTwo {

    public static void main(String[] args) throws IOException {
        double[][] score = new double[1000][303];
        int[][] allRows = Trainer.initializeTestDataFromFile(Trainer.TRAINING_DATA_FILE_NAME_ROUND_THREE);
        int[] selectedRows = readRowIndexes("line_numbers_for_round_2.txt");

        Predictor predictor = new ThirdRoundPredictor(); // new RoundTwoPredictor();

        long started = System.currentTimeMillis();

        int scoreRowIndex = 0;
        for (int selectedRowIndex : selectedRows) {

            int[] actualValues = allRows[selectedRowIndex];

            for (int position=0; position<actualValues.length; ++position) {

                int actualValue = actualValues[position];

                int[] previousValues = Arrays.copyOf(actualValues, position);

                double[] prediction;
                if (position == 0) {
                    prediction = predictor.getFirstGuess();
                } else {
                    prediction = predictor.predictRow(position, previousValues);
                }

                score[scoreRowIndex][position] = Math.log(prediction[actualValue]);

            }

            ++scoreRowIndex;
        }

        double totalScore = 0;
        for (double[] row : score) {
            for (double positionScore : row) {
                totalScore += positionScore;
            }
        }

        System.out.printf("%f \n", totalScore);

        System.out.println((System.currentTimeMillis() - started) + " ms.");
    }

    private static int[] readRowIndexes(String fileName) throws IOException {
        int[] rows = new int[1000];
        File file = new File(Trainer.class.getClassLoader().getResource(fileName).getFile());
        FileReader fr = new FileReader(file);
        BufferedReader br = new BufferedReader(fr);
        String line;
        int index = 0;
        while((line = br.readLine()) != null){
            rows[index++] = new Integer(line);
        }
        br.close();
        fr.close();
        return rows;
    }

}
