package tade.propromo;

import tade.propromo.predictor.DuvinsPredictor;
import tade.propromo.predictor.UniformPredictor;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Collections;

public class Trainer {


    public static void main(String[] args) throws Exception {
        ArrayList<int[]> testData = initializeTestDataFromFile("/Users/tade/Code/ProProMo2015/data/data1.txt");
        Collections.shuffle(testData);

        double baseline = runWithWorker(new TrainingDataWorker(testData, new DuvinsPredictor()));
        double myScore = runWithWorker(new TrainingDataWorker(testData, new UniformPredictor()));

        System.out.println("Baseline is: " + baseline + " and I got: " + myScore);
    }

    private static double runWithWorker(TrainingDataWorker worker) throws Exception {
        for (int round=0; round<303; ++round) {
            worker.run();
        }
        return worker.calculateScore();
    }

    private static ArrayList<int[]> initializeTestDataFromFile(String trainingDataFileFullPath) throws Exception {
        ArrayList<int[]> testData = new ArrayList<>();

        System.out.println("Reading file: " + trainingDataFileFullPath);
        File file = new File(trainingDataFileFullPath);
        FileReader fr = new FileReader(file);
        BufferedReader br = new BufferedReader(fr);
        String line;
        while((line = br.readLine()) != null){
            testData.add(Worker.getValues(line));
            if (testData.size() == 2000) { break; } // It can't handle 60k+ rows.
        }
        br.close();
        fr.close();

        return testData;
    }

}
