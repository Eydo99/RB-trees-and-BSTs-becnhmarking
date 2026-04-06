package trees.benchmark;

import java.util.Arrays;

/**
 * Utility class providing basic descriptive statistics over an array of
 * {@code long} timing measurements (typically nanoseconds from benchmark runs).
 * <p>
 * All methods are static — this class is not intended to be instantiated.
 * </p>
 */
public class Stats {

    /**
     * Computes the arithmetic mean of the timing values.
     *
     * @param times array of nanosecond measurements
     * @return the mean as a {@code double}
     */
    public static double mean(long[] times) {
        long sum = 0;
        for (long time : times) {
            sum += time;
        }
        return (double) sum / times.length;
    }

    /**
     * Computes the median of the timing values.
     * <p>
     * Works on a sorted copy so the original array is not modified.
     * For even-length arrays, returns the average of the two middle values.
     * For odd-length arrays, returns the exact middle element.
     * </p>
     *
     * @param times array of nanosecond measurements
     * @return the median as a {@code double}
     */
    public static double median(long[] times) {
        long[] sortedArr = Arrays.copyOf(times, times.length); // sort a copy, don't modify original
        Arrays.sort(sortedArr);
        int length = sortedArr.length;
        if (length % 2 == 1) {
            return sortedArr[length / 2]; // odd: exact middle
        } else {
            return (double) (sortedArr[length / 2] + sortedArr[(length / 2) - 1]) / 2; // even: average of two middles
        }
    }

    /**
     * Computes the population standard deviation of the timing values.
     * <p>
     * Uses the definition {@code σ = sqrt( Σ(x_i - μ)² / N )}.
     * </p>
     *
     * @param times array of nanosecond measurements
     * @return the standard deviation as a {@code double}
     */
    public static double standardDeviation(long[] times) {
        double mean = mean(times);

        double sum = 0;
        for (int i = 0; i < times.length; i++) {
            sum += Math.pow(times[i] - mean, 2); // accumulate squared deviations from mean
        }
        return Math.sqrt(sum / times.length); // population std dev (divide by N, not N-1)
    }
}
