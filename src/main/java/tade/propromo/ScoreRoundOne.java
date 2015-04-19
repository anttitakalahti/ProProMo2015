package tade.propromo;

import org.duvin.propromo2015.Example;

import java.io.*;
import java.util.Arrays;

public class ScoreRoundOne {

    public static void main(String[] args) throws IOException {
        double[][] score = new double[1000][303];
        int[][] allRows = Trainer.initializeTestDataFromFile(Trainer.TRAINING_DATA_FILE_NAME_ROUND_TWO);
        int[] selectedRows = readRowIndexes();

        int scoreRowIndex = 0;
        for (int selectedRowIndex : selectedRows) {

            int[] actualValues = allRows[selectedRowIndex];

            for (int position=0; position<actualValues.length; ++position) {

                int actualValue = actualValues[position];

                int[] previousValues = Arrays.copyOf(actualValues, position);
                double[] prediction = getExamplePrediction(position, previousValues);

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

        System.out.printf("%f", totalScore);
    }

    private static double[] getExamplePrediction(int position, int[] previousValues) {
        if (position == 0) { return Example.getRandomDist(); }
        return Example.basedOnLastValue(previousValues[position-1]);
    }

    private static int[] readRowIndexes() throws IOException {
        int[] rows = new int[1000];
        File file = new File(Trainer.class.getClassLoader().getResource("line_numbers_for_round_1.txt").getFile());
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
