package tade.propromo;

import java.util.ArrayList;
import java.util.Arrays;

public class Peak {


    private int maxMinusMin;
    private double meanValue;
    private int[] values;

    public Peak(int[] sortedValues) {
        maxMinusMin = sortedValues[sortedValues.length - 1] - sortedValues[0];
        meanValue = Arrays.stream(sortedValues).average().getAsDouble();
        values = sortedValues;
    }

    public double getMeanValue() {
        return meanValue;
    }

    public int getItemCount() {
        return values.length;
    }

    public int getMaxMinusMin() {
        return maxMinusMin;
    }


    public static ArrayList<Peak> peaksPerRow(int[] row) {
        int[] sortedNonZeroValues = Arrays.stream(row).filter(i -> i > 0).sorted().toArray();
        if (sortedNonZeroValues.length == 0) { return new ArrayList<>(); }

        int prevValue = -10;
        int lastPeakStartIndex = -10;
        ArrayList<Peak> peaks = new ArrayList<>();
        for (int i=0; i<sortedNonZeroValues.length; ++i) {
            int value = sortedNonZeroValues[i];
            if (value > prevValue + 5) {

                if (lastPeakStartIndex >= 0) {
                    peaks.add(new Peak(Arrays.copyOfRange(sortedNonZeroValues, lastPeakStartIndex, i)));
                }

                lastPeakStartIndex = i;
            }
            prevValue = value;
        }

        if (lastPeakStartIndex >= 0) {
            peaks.add(new Peak(Arrays.copyOfRange(sortedNonZeroValues, lastPeakStartIndex, sortedNonZeroValues.length)));
        }

        return peaks;
    }

    public boolean containsValue(int value) {
        return Arrays.stream(values).anyMatch(v -> v == value);
    }
}
