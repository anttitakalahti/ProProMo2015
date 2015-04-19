/*
 * (c) Johannes Verwijnen, duvin@duvin.org
 */
package org.duvin.propromo2015;

import java.io.File;
import java.io.PrintWriter;
import java.util.Map;
import java.util.Scanner;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 *
 * @author Johannes Verwijnen <duvin@duvin.org>
 */
public class CreateTestVectors {

    public static void main(String[] args) throws Exception {
        SortedMap<Integer, Integer> rows = new TreeMap<>();
        Scanner s = new Scanner(new File(args[0]));
        int i = 1;
        while (s.hasNextLine()) {
            int vector = Integer.parseInt(s.nextLine());
            rows.put(vector, i++);
        }
        s.close();
        if (rows.size() != 1000) {
            System.out.println(rows.size());
            System.exit(1);
        }
        int[][] data = new int[303][1000];
        s = new Scanner(new File(args[1]));
        i = 1;
        for (Map.Entry<Integer, Integer> row : rows.entrySet()) {
            //System.out.println(row.getKey() + "\t" + i);
            while (i < row.getKey()) {
                s.nextLine();
                i++;
            }
            String[] values = s.nextLine().split(",");
            for (int j = 0; j < values.length; j++) {
                data[j][row.getValue()-1] = Integer.parseInt(values[j]);
            }
            i++;
        }
        s.close();
        for (i = 0; i < data.length; i++) {
            PrintWriter pw = new PrintWriter(args[2]+(i+1)+"/in.txt");
            StringBuilder sb = new StringBuilder();
            for (int j = 0; j < data[i].length; j++) {
                sb.append(data[i][j]).append(",");
            }
            pw.println(sb.substring(0, sb.length() - 1));
            pw.close();
        }
    }

}
