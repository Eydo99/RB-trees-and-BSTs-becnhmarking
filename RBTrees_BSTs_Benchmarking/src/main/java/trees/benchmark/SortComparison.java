package trees.benchmark;

import java.util.ArrayList;
import java.util.List;

/**
 * MergeSort implementation used as a baseline comparison for tree-sort
 * operations.
 * <p>
 * This class records comparison and interchange counts alongside each sort
 * step,
 * making it suitable for both benchmarking and step-by-step analysis. The
 * {@link #steps}
 * list captures array snapshots at each active index, enabling visualisation of
 * the
 * sorting process.
 * </p>
 * <p>
 * To run a timed sort, call {@link #sort(int[])} which first calls
 * {@link #reset()} to
 * clear any state from a previous run, then performs the sort.
 * </p>
 */
public class SortComparison {

    /**
     * Immutable snapshot of the array state at a particular merge step.
     * Used for visualisation and step-by-step analysis.
     */
    static class SortStep {
        /** A clone of the array at this point in the sort. */
        public int[] array;

        /** The index in the array that was most recently written. */
        public int activeIndex;

        /**
         * @param array       the current state of the array (cloned to avoid aliasing)
         * @param activeIndex the index most recently modified
         */
        public SortStep(int[] array, int activeIndex) {
            this.array = array.clone();
            this.activeIndex = activeIndex;
        }
    }

    /** Running count of element comparisons performed. */
    private long comparisons = 0;

    /** Running count of element placements (writes into the output array). */
    private long interchanges = 0;

    /** Log of all intermediate array states captured during sorting. */
    private List<SortStep> steps = new ArrayList<>();

    /** Human-readable label for this sorter (used in reporting). */
    String name;

    /**
     * Constructs a new MergeSort instance with name {@code "MergeSort"}.
     */
    public SortComparison() {
        this.name = "MergeSort";
    }

    /**
     * Resets statistics and step log for a fresh run.
     * Called automatically at the start of {@link #sort(int[])}.
     */
    public void reset() {
        steps.clear();
        comparisons = 0;
        interchanges = 0;
    }

    /**
     * Sorts the given array in ascending order using MergeSort.
     * Resets stats and the step log before sorting.
     *
     * @param array the array to sort in place
     */
    public void sort(int[] array) {
        reset();
        mergeSort(array);
    }

    /**
     * Recursively splits the array in half and merges the two sorted halves.
     * Base case: arrays of length {@code < 2} are already sorted.
     *
     * @param array the (sub)array to sort in place
     */
    private void mergeSort(int[] array) {
        int n = array.length;
        if (n < 2)
            return;

        int mid = n / 2;
        int[] left = new int[mid];
        int[] right = new int[n - mid];

        // Split array into left and right halves
        int l = 0, r = 0;
        for (; l < n; l++) {
            if (l < mid)
                left[l] = array[l];
            else {
                right[r] = array[l];
                r++;
            }
        }

        mergeSort(left); // recursively sort left half
        mergeSort(right); // recursively sort right half
        merge(array, left, right); // merge the two sorted halves back into array
    }

    /**
     * Merges two sorted halves ({@code left} and {@code right}) into {@code array}.
     * Counts comparisons and interchanges for benchmarking analysis.
     *
     * @param array the destination array receiving the merged result
     * @param left  sorted left half
     * @param right sorted right half
     */
    private void merge(int[] array, int[] left, int[] right) {
        int leftSize = left.length;
        int rightSize = right.length;
        int l = 0, r = 0, i = 0;

        // Merge by always picking the smaller of the two current front elements
        while (l < leftSize && r < rightSize) {
            if (left[l] < right[r]) {
                array[i] = left[l]; // left element is smaller — place it
                l++;
                i++;
                interchanges++;
            } else {
                array[i] = right[r]; // right element is smaller or equal — place it
                r++;
                i++;
                interchanges++;
            }

            comparisons++; // one comparison consumed
        }

        // Drain remaining elements from left half (already in order)
        while (l < leftSize) {
            array[i] = left[l];
            l++;
            i++;
            interchanges++;

        }

        // Drain remaining elements from right half (already in order)
        while (rightSize > r) {
            array[i] = right[r];
            r++;
            i++;
            interchanges++;
        }
    }
}
