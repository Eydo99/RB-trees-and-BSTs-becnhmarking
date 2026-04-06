package trees.benchmark;

import trees.BST;
import trees.RBTree;
import trees.TreeInterface;

import java.util.Arrays;
import java.util.Random;
import java.util.function.Supplier;

public class BenchmarkRunner {
    private static final int RUNS=5;

    public static void main(String[] args)
    {
        InputGenerator generator=new InputGenerator();

        int[] random=generator.generateRandom();
        int[] nearlySorted1=generator.generateNearlySorted(1);
        int[] nearlySorted5=generator.generateNearlySorted(5);
        int[] nearlySorted10=generator.generateNearlySorted(10);

        runAllBenchmarks("RANDOM",random);
        runAllBenchmarks("Nearly-Sorted with 1% misplaced elements",nearlySorted1);
        runAllBenchmarks("Nearly-Sorted with 5% misplaced elements",nearlySorted5);
        runAllBenchmarks("Nearly-Sorted with 10% misplaced elements",nearlySorted10);

    }

    private static long[] benchmarkInsert(Supplier<TreeInterface> treeSupplier,int[] input)
    {
        long[] elapsedArray=new long[RUNS];

        for(int i=0;i<RUNS;i++)
        {
            TreeInterface tree = treeSupplier.get();
            long start = System.nanoTime();
            for (int k : input) {
                tree.insert(k);
            }
            long end = System.nanoTime();
            long elapsed = end - start;
            elapsedArray[i] = elapsed;
            if (i == RUNS - 1) System.out.println("Height after insertion: " + tree.height());

        }
        return elapsedArray;
    }

    private static long[] benchmarkContains(Supplier<TreeInterface> treeSupplier,int[] input)
    {
        int n=input.length;
        TreeInterface tree=treeSupplier.get();
        long[] elapsedArray=new long[RUNS];

        for (int k : input) {
            tree.insert(k);
        }

        for(int i=0;i<RUNS;i++)
        {
            Random random=new Random(40);
            long start=System.nanoTime();
            for(int j=0;j<50000;j++)
            {
                int lookup=random.nextInt(n);
                tree.contains(input[lookup]);
            }
            for(int j=0;j<50000;j++)
            {
                int lookup=random.nextInt(10*n,30*n);
                tree.contains(lookup);
            }
            long end=System.nanoTime();
            long elapsed=end-start;
            elapsedArray[i]=elapsed;
        }
        return elapsedArray;
    }

    private static long[] benchmarkDelete(Supplier<TreeInterface> treeSupplier,int[] input)
    {
        int n=input.length;
        long[] elapsedArray=new long[RUNS];
        int deletionNo=(int) (0.2*n);
        for(int i=0;i<RUNS;i++)
        {
            TreeInterface tree = treeSupplier.get();
            for(int k : input) tree.insert(k);
            Random random=new Random(23);

            long start=System.nanoTime();
            for(int j=0;j<deletionNo;j++)
            {
                int deletedIndex=random.nextInt(n);
                tree.delete(input[deletedIndex]);
            }
            long end=System.nanoTime();
            long elapsed=end-start;
            elapsedArray[i]=elapsed;
        }
        return elapsedArray;
    }

    private static long[] benchmarkSort(Supplier <TreeInterface> treeSupplier, int[] input)
    {
        long[] elapsedArray=new long[RUNS];
        for(int i=0;i<RUNS;i++)
        {
            TreeInterface tree=treeSupplier.get();
            long start=System.nanoTime();
            for(int k : input) tree.insert(k);
            tree.inOrder();
            long end=System.nanoTime();
            long elapsed=end-start;
            elapsedArray[i]=elapsed;

        }
        return elapsedArray;
    }

    private static long[] benchmarkMergeSort(int[] input)
    {
        SortComparison mergeSort=new SortComparison();
        long[] elapsedArray=new long[RUNS];
        for(int i=0;i<RUNS;i++)
        {
            int[] copy= Arrays.copyOf(input,input.length);
            long start=System.nanoTime();
            mergeSort.sort(copy);
            long end=System.nanoTime();
            long elapsed=end-start;
            elapsedArray[i]=elapsed;
        }
        return elapsedArray;
    }

    private static void printResults(String operation, long[] times)
    {
        System.out.printf("%s -> Mean: %.2f ms | Median: %.2f ms | StdDev: %.2f ms%n",
                operation, Stats.mean(times) / 1_000_000, Stats.median(times) / 1_000_000,
                Stats.standardDeviation(times) / 1_000_000);
    }

    private static void runAllBenchmarks(String distribution ,int[] input)
    {
        System.out.println("\n=== " + distribution + " ===");

        System.out.println("\n-- BST --");
        long[] bstInsert=benchmarkInsert(BST::new,input);
        long[] bstContains= benchmarkContains(BST::new,input);
        long[] bstDelete=benchmarkDelete(BST::new,input);
        long[] bstSort=benchmarkSort(BST::new,input);

        // print BST results
        printResults("Insert", bstInsert);
        printResults("Contains", bstContains);
        printResults("Delete", bstDelete);
        printResults("Sort", bstSort);

        System.out.println("\n-- RBTree --");
        long[] RBInsert=benchmarkInsert(RBTree::new,input);
        long[] RBContains= benchmarkContains(RBTree::new,input);
        long[] RBDelete=benchmarkDelete(RBTree::new,input);
        long[] RBSort=benchmarkSort(RBTree::new,input);

        // print RB results
        printResults("Insert", RBInsert);
        printResults("Contains", RBContains);
        printResults("Delete", RBDelete);
        printResults("Sort", RBSort);

        System.out.println("\n-- MergeSort --");
        long[] mergeSort = benchmarkMergeSort(input);
        printResults("Sort", mergeSort);


        System.out.printf("%nSpeedup (BST/RBTree):%n");
        System.out.printf("Insert speedup: %.2fx%n", Stats.mean(RBInsert) / Stats.mean(bstInsert));
        System.out.printf("Contains speedup: %.2fx%n", Stats.mean(RBContains) / Stats.mean(bstContains));
        System.out.printf("Delete speedup: %.2fx%n", Stats.mean(RBDelete) / Stats.mean(bstDelete));
        System.out.printf("Sort speedup: %.2fx%n", Stats.mean(RBSort) / Stats.mean(bstSort));
    }

}
