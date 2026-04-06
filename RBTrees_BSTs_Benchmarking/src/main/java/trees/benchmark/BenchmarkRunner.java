package trees.benchmark;

import trees.BST;
import trees.RBTree;
import trees.TreeInterface;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Random;
import java.util.function.Supplier;

/**
 * Main entry point for the benchmarking suite (declared as the JAR manifest
 * main class).
 * <p>
 * Benchmarks {@link BST}, {@link RBTree}, and {@link SortComparison}
 * (MergeSort) across
 * four input distributions and four operations: Insert, Contains, Delete, and
 * Sort.
 * Results are printed to stdout and appended to {@code results.csv} in the
 * working directory.
 * </p>
 * <p>
 * Each benchmark is repeated {@value #RUNS} times to obtain stable statistics.
 * The {@link Stats} class is then used to compute mean, median, and standard
 * deviation
 * over those runs. Both tree types are supplied via
 * {@code Supplier<TreeInterface>} method
 * references so the benchmarking logic is shared.
 * </p>
 */
public class BenchmarkRunner {

    /** Number of times each benchmark is repeated for statistical reliability. */
    private static final int RUNS = 6;

    /**
     * Entry point. Generates the four input distributions, writes the CSV header,
     * then calls {@link #runAllBenchmarks(String, int[])} for each distribution.
     *
     * @param args ignored
     * @throws IOException if writing to {@code results.csv} fails
     */
    public static void main(String[] args) throws IOException {
        InputGenerator generator = new InputGenerator();

        // Generate four input distributions (N = 100,000 each, deterministic seed)
        int[] random = generator.generateRandom();
        int[] nearlySorted1 = generator.generateNearlySorted(1);
        int[] nearlySorted5 = generator.generateNearlySorted(5);
        int[] nearlySorted10 = generator.generateNearlySorted(10);

        // Write the CSV header (overwrite mode clears any previous results)
        FileWriter fw = new FileWriter("results.csv", false); // false = overwrite
        PrintWriter pw = new PrintWriter(fw);
        pw.println("Distribution,Structure,Operation,Mean,Median,StdDev");
        pw.close();

        // Run all benchmarks for each distribution
        runAllBenchmarks("RANDOM", random);
        runAllBenchmarks("Nearly-Sorted with 1% misplaced elements", nearlySorted1);
        runAllBenchmarks("Nearly-Sorted with 5% misplaced elements", nearlySorted5);
        runAllBenchmarks("Nearly-Sorted with 10% misplaced elements", nearlySorted10);

    }

    /**
     * Benchmarks the insert operation for a given tree type and input array.
     * Each run creates a fresh tree instance (via {@code treeSupplier}) and inserts
     * all elements, measuring total wall-clock time in nanoseconds.
     * Prints tree height after the final run as a diagnostic.
     *
     * @param treeSupplier factory that creates a fresh empty tree each run
     * @param input        the array of integers to insert
     * @return array of {@value #RUNS} nanosecond timing measurements
     */
    private static long[] benchmarkInsert(Supplier<TreeInterface> treeSupplier, int[] input) {
        long[] elapsedArray = new long[RUNS-1];

        for (int i = 0; i < RUNS; i++) {
            TreeInterface tree = treeSupplier.get();
            long start = System.nanoTime();
            for (int k : input) {
                tree.insert(k);
            }
            long end = System.nanoTime();
            long elapsed = end - start;
            if(i==0) continue;
            elapsedArray[i-1] = elapsed;
            if (i == RUNS - 1)
                System.out.println("Height after insertion: " + tree.height());

        }
        return elapsedArray;
    }

    /**
     * Benchmarks the contains (search) operation.
     * A single tree is pre-filled with all elements once. Then, each run performs
     * 100,000 lookups: 50,000 hits (existing keys, random seed 40) and
     * 50,000 misses (out-of-range keys in {@code [10N, 30N]}).
     *
     * @param treeSupplier factory that creates a fresh empty tree (used once to
     *                     pre-fill)
     * @param input        the array of integers used to populate the tree
     * @return array of {@value #RUNS} nanosecond timing measurements
     */
    private static long[] benchmarkContains(Supplier<TreeInterface> treeSupplier, int[] input) {
        int n = input.length;
        TreeInterface tree = treeSupplier.get();
        long[] elapsedArray = new long[RUNS-1];

        // Pre-fill the tree once — it is reused across all RUNS
        for (int k : input) {
            tree.insert(k);
        }

        for (int i = 0; i < RUNS; i++) {
            Random random = new Random(40); // fixed seed for reproducibility
            long start = System.nanoTime();
            // 50,000 successful lookups (keys that exist in the tree)
            for (int j = 0; j < 50000; j++) {
                int lookup = random.nextInt(n);
                tree.contains(input[lookup]);
            }
            // 50,000 unsuccessful lookups (keys beyond the input range)
            for (int j = 0; j < 50000; j++) {
                int lookup = random.nextInt(10 * n, 30 * n);
                tree.contains(lookup);
            }
            long end = System.nanoTime();
            long elapsed = end - start;
            if(i==0) continue;
            elapsedArray[i-1] = elapsed;
        }
        return elapsedArray;
    }

    /**
     * Benchmarks the delete operation.
     * Each run creates a fresh pre-filled tree and deletes 20% of the elements
     * (chosen randomly with fixed seed 23).
     *
     * @param treeSupplier factory that creates a fresh empty tree each run
     * @param input        the array of integers to insert and partially delete
     * @return array of {@value #RUNS} nanosecond timing measurements
     */
    private static long[] benchmarkDelete(Supplier<TreeInterface> treeSupplier, int[] input) {
        int n = input.length;
        long[] elapsedArray = new long[RUNS-1];
        int deletionNo = (int) (0.2 * n); // delete 20% of elements
        for (int i = 0; i < RUNS; i++) {
            // Fresh tree each run so deletions don't accumulate
            TreeInterface tree = treeSupplier.get();
            for (int k : input)
                tree.insert(k);
            Random random = new Random(23); // fixed seed for reproducibility

            long start = System.nanoTime();
            for (int j = 0; j < deletionNo; j++) {
                int deletedIndex = random.nextInt(n);
                tree.delete(input[deletedIndex]);
            }
            long end = System.nanoTime();
            long elapsed = end - start;
            if(i==0) continue;
            elapsedArray[i-1] = elapsed;
        }
        return elapsedArray;
    }

    /**
     * Benchmarks tree-sort: insert all elements then call
     * {@link TreeInterface#inOrder()}.
     * This measures the cost of using a tree as a sorting data structure
     * (insert N elements + traverse in sorted order).
     *
     * @param treeSupplier factory that creates a fresh empty tree each run
     * @param input        the array of integers to sort via the tree
     * @return array of {@value #RUNS} nanosecond timing measurements
     */
    private static long[] benchmarkSort(Supplier<TreeInterface> treeSupplier, int[] input) {
        long[] elapsedArray = new long[RUNS];
        for (int i = 0; i < RUNS; i++) {
            TreeInterface tree = treeSupplier.get();
            long start = System.nanoTime();
            for (int k : input)
                tree.insert(k);
            tree.inOrder(); // in-order traversal = sorted output
            long end = System.nanoTime();
            long elapsed = end - start;
            if(i==0) continue;
            elapsedArray[i-1] = elapsed;

        }
        return elapsedArray;
    }

    /**
     * Benchmarks MergeSort using {@link SortComparison}.
     * Each run sorts a fresh copy of the input array so the previous run's sorted
     * state
     * does not affect the next run's timing.
     *
     * @param input the array to sort
     * @return array of {@value #RUNS} nanosecond timing measurements
     */
    private static long[] benchmarkMergeSort(int[] input) {
        SortComparison mergeSort = new SortComparison();
        long[] elapsedArray = new long[RUNS];
        for (int i = 0; i < RUNS; i++) {
            int[] copy = Arrays.copyOf(input, input.length); // fresh copy each run
            long start = System.nanoTime();
            mergeSort.sort(copy);
            long end = System.nanoTime();
            long elapsed = end - start;
            if(i==0) continue;
            elapsedArray[i-1] = elapsed;
        }
        return elapsedArray;
    }

    /**
     * Prints a single benchmark result line to stdout.
     * Times are converted from nanoseconds to milliseconds for readability.
     *
     * @param operation label for the operation (e.g., "Insert", "Delete")
     * @param times     raw nanosecond timing array from a benchmark run
     */
    private static void printResults(String operation, long[] times) {
        System.out.printf("%s -> Mean: %.2f ms | Median: %.2f ms | StdDev: %.2f ms%n",
                operation, Stats.mean(times) / 1_000_000, Stats.median(times) / 1_000_000,
                Stats.standardDeviation(times) / 1_000_000);
    }

    /**
     * Runs the full benchmark suite for a given input distribution.
     * Benchmarks BST, RBTree, and MergeSort, prints results, writes to CSV,
     * and prints BST/RBTree speedup ratios.
     *
     * @param distribution human-readable label for the distribution (used in CSV
     *                     and stdout)
     * @param input        the input array for this distribution
     * @throws IOException if writing to {@code results.csv} fails
     */
    private static void runAllBenchmarks(String distribution, int[] input) throws IOException {
        System.out.println("\n=== " + distribution + " ===");

        System.out.println("\n-- BST --");
        long[] bstInsert = benchmarkInsert(BST::new, input);
        long[] bstContains = benchmarkContains(BST::new, input);
        long[] bstDelete = benchmarkDelete(BST::new, input);
        long[] bstSort = benchmarkSort(BST::new, input);

        // print BST results
        printResults("Insert", bstInsert);
        writeToCSV(distribution, "BST", "Insert", bstInsert);
        printResults("Contains", bstContains);
        writeToCSV(distribution, "BST", "Contains", bstContains);
        printResults("Delete", bstDelete);
        writeToCSV(distribution, "BST", "Delete", bstDelete);
        printResults("Sort", bstSort);
        writeToCSV(distribution, "BST", "Sort", bstSort);

        System.out.println("\n-- RBTree --");
        long[] RBInsert = benchmarkInsert(RBTree::new, input);
        long[] RBContains = benchmarkContains(RBTree::new, input);
        long[] RBDelete = benchmarkDelete(RBTree::new, input);
        long[] RBSort = benchmarkSort(RBTree::new, input);

        // print RB results
        printResults("Insert", RBInsert);
        writeToCSV(distribution, "RBTree", "Insert", RBInsert);
        printResults("Contains", RBContains);
        writeToCSV(distribution, "RBTree", "Contains", RBContains);
        printResults("Delete", RBDelete);
        writeToCSV(distribution, "RBTree", "Delete", RBDelete);
        printResults("Sort", RBSort);
        writeToCSV(distribution, "RBTree", "Sort", RBSort);

        System.out.println("\n-- MergeSort --");
        long[] mergeSort = benchmarkMergeSort(input);
        printResults("Sort", mergeSort);
        writeToCSV(distribution, "MergeSort", "Sort", mergeSort);

        // Print speedup ratios: BST mean / RBTree mean (>1 means BST is faster, <1
        // means RBTree is faster)
        System.out.printf("%nSpeedup (BST/RBTree):%n");
        System.out.printf("Insert speedup: %.2fx%n", Stats.mean(bstInsert) / Stats.mean(RBInsert));
        System.out.printf("Contains speedup: %.2fx%n", Stats.mean(bstContains) / Stats.mean(RBContains));
        System.out.printf("Delete speedup: %.2fx%n", Stats.mean(bstDelete) / Stats.mean(RBDelete));
        System.out.printf("Sort speedup: %.2fx%n", Stats.mean(bstSort) / Stats.mean(RBSort));
    }

    /**
     * Appends a single benchmark result row to {@code results.csv} in append mode.
     * Times are converted from nanoseconds to milliseconds before writing.
     *
     * @param distribution the input distribution label
     * @param structure    the data structure name ("BST", "RBTree", or "MergeSort")
     * @param operation    the operation name ("Insert", "Contains", "Delete",
     *                     "Sort")
     * @param times        raw nanosecond timing array from a benchmark run
     * @throws IOException if writing to the file fails
     */
    private static void writeToCSV(String distribution, String structure, String operation, long[] times)
            throws IOException {
        FileWriter fw = new FileWriter("results.csv", true); // true = append mode
        PrintWriter pw = new PrintWriter(fw);
        pw.printf("%s,%s,%s,%.2f,%.2f,%.2f%n",
                distribution, structure, operation,
                Stats.mean(times) / 1_000_000,
                Stats.median(times) / 1_000_000,
                Stats.standardDeviation(times) / 1_000_000);
        pw.close();
    }

}
