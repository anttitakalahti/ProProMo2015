package tade.propromo;

public class Trainer {


    public static void main(String[] args) throws Exception {
        TrainingDataWorker worker = new TrainingDataWorker("/Users/tade/Code/ProProMo2015/data/data1.txt");
        for (int round=0; round<303; ++round) {
            worker.run();
        }
        System.out.println("I got a combined probability of: " + worker.calculateScore());
    }



}
