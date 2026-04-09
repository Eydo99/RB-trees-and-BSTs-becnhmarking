package trees.benchmark;

import java.util.Random;


/**
 * Generates deterministic input arrays for the benchmarking suite.
 * <p>
 * All methods use a fixed {@link #seed} so that results are reproducible across
 * multiple runs. Two types of inputs are supported:
 * <ul>
 * <li><b>Random</b> — uniformly random integers in {@code [0, 10*N]}.</li>
 * <li><b>Nearly-Sorted</b> — the sorted array {@code [0..N-1]} with a given
 * percentage of random pair-swaps applied to introduce disorder.</li>
 * </ul>
 * </p>
 */
public class InputGenerator {



    /** Number of elements in every generated array. */
    private static  int n;

    /** Fixed random seed for reproducibility across runs. */
    private static final int seed = 40;

    /**
     * Generates an array of {@value } random integers uniformly distributed
     * in the range {@code [0, 10*N]}, using the fixed {@link #seed}.
     *
     * @return a new {@code int[]} of length {@value } with random values
     */
    public static int[] generateRandom() {
        int[] randomArray = new int[n];
        Random random = new Random(seed);
        for (int i = 0; i < n; i++) {
            randomArray[i] = random.nextInt(0, 10 * n + 1);
        }
        return randomArray;
    }

    /**
     * Generates a nearly-sorted array of {@value } elements.
     * <p>
     * Starts with the perfectly sorted array {@code [0, 1, 2, ..., N-1]}, then
     * randomly swaps {@code (swapPercent / 100.0) * N} pairs of elements to
     * introduce
     * a controlled degree of disorder. Uses the fixed {@link #seed}.
     * </p>
     *
     * @param swapPercent the percentage of elements to displace via random swaps
     *                    (e.g., 1 means 1% of N swaps, 10 means 10% of N swaps)
     * @return a new {@code int[]} of length {@value } that is nearly sorted
     */
    public static int[] generateNearlySorted(int swapPercent) {
        int[] nearlySortedArr = new int[n];
        Random random = new Random(seed);
        // Start with the perfectly sorted sequence [0, 1, 2, ..., N-1]
        for (int i = 0; i < n; i++) {
            nearlySortedArr[i] = i;
        }
        double swapsNo = (swapPercent / 100.0) * n; // number of pair-swaps to perform

        // Apply the random swaps to introduce disorder
        for (int i = 0; i < swapsNo; i++) {
            int firstIndex = random.nextInt(0, n);
            int secondIndex = random.nextInt(0, n);
            swap(nearlySortedArr, firstIndex, secondIndex);
        }
        return nearlySortedArr;
    }

    /**
     * Swaps two elements in an {@code int[]} array by index.
     * Used internally by {@link #generateNearlySorted(int)}.
     *
     * @param arr the array to modify in place
     * @param i   index of the first element
     * @param j   index of the second element
     */
    private static void swap(int[] arr, int i, int j) {
        int temp = arr[i];
        arr[i] = arr[j];
        arr[j] = temp;
    }

    public static void setN(int n) {
        InputGenerator.n = n;
    }

}
