/*
 * (c) Johannes Verwijnen, duvin@duvin.org
 */
package org.duvin.propromo2015;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

/**
 *
 * @author Johannes Verwijnen <duvin@duvin.org>
 */
public class CalculateScores {
    
    private static final Map<String,Double> scores = new HashMap<>();
    
    public static void main(String[] args) throws Exception{
        for (String filename : args) {
            double score = 0;
            Scanner s = new Scanner(new File(filename));
            while (s.hasNext()) {
                String[] values = s.nextLine().split(",");
                for (String value : values) {
                    score += Double.parseDouble(value);
                }
            }
            scores.put(filename,score);
        }
        System.out.println(scores);
    }
    
}
