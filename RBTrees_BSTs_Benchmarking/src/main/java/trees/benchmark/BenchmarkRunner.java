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

public class BenchmarkRunner {
    private static final int RUNS=6;

    public static void main(String[] args) throws IOException {
        InputGenerator generator=new InputGenerator();

        int[] random=generator.generateRandom();
        int[] nearlySorted1=generator.generateNearlySorted(1);
        int[] nearlySorted5=generator.generateNearlySorted(5);
        int[] nearlySorted10=generator.generateNearlySorted(10);

        FileWriter fw = new FileWriter("results.csv", false); // false = overwrite
        PrintWriter pw = new PrintWriter(fw);
        pw.println("Distribution,Structure,Operation,Mean,Median,StdDev");
        pw.close();

        runAllBenchmarks("RANDOM",random);
        runAllBenchmarks("Nearly-Sorted with 1% misplaced elements",nearlySorted1);
        runAllBenchmarks("Nearly-Sorted with 5% misplaced elements",nearlySorted5);
        runAllBenchmarks("Nearly-Sorted with 10% misplaced elements",nearlySorted10);

    }

    private static long[] benchmarkInsert(Supplier<TreeInterface> treeSupplier,int[] input,String distribution,String structure) throws IOException {
        long[] elapsedArray=new long[RUNS-1];

        for(int i=0;i<RUNS;i++)
        {
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
            {
                int height = tree.height();
                System.out.println("Height after insertion: " + height);
                // write to CSV
                FileWriter fw = new FileWriter("results.csv", true);
                PrintWriter pw = new PrintWriter(fw);
                pw.printf("%s,%s,Height,%d,%d,0%n", distribution, structure, height, height);
                pw.close();
            }

        }
        return elapsedArray;
    }

    private static long[] benchmarkContains(Supplier<TreeInterface> treeSupplier,int[] input)
    {
        int n=input.length;
        TreeInterface tree=treeSupplier.get();
        long[] elapsedArray=new long[RUNS-1];

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
            if(i==0) continue;
            elapsedArray[i-1]=elapsed;
        }
        return elapsedArray;
    }

    private static long[] benchmarkDelete(Supplier<TreeInterface> treeSupplier,int[] input)
    {
        int n=input.length;
        long[] elapsedArray=new long[RUNS-1];
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
            if(i==0) continue;
            elapsedArray[i-1]=elapsed;
        }
        return elapsedArray;
    }

    private static long[] benchmarkSort(Supplier <TreeInterface> treeSupplier, int[] input)
    {
        long[] elapsedArray=new long[RUNS-1];
        for(int i=0;i<RUNS;i++)
        {
            TreeInterface tree=treeSupplier.get();
            long start=System.nanoTime();
            for(int k : input) tree.insert(k);
            tree.inOrder();
            long end=System.nanoTime();
            long elapsed=end-start;
            if(i==0) continue;
            elapsedArray[i-1]=elapsed;

        }
        return elapsedArray;
    }

    private static long[] benchmarkMergeSort(int[] input)
    {
        SortComparison mergeSort=new SortComparison();
        long[] elapsedArray=new long[RUNS-1];
        for(int i=0;i<RUNS;i++)
        {
            int[] copy= Arrays.copyOf(input,input.length);
            long start=System.nanoTime();
            mergeSort.sort(copy);
            long end=System.nanoTime();
            long elapsed=end-start;
            if(i==0) continue;
            elapsedArray[i-1]=elapsed;
        }
        return elapsedArray;
    }

    private static void printResults(String operation, long[] times)
    {
        System.out.printf("%s -> Mean: %.2f ms | Median: %.2f ms | StdDev: %.2f ms%n",
                operation, Stats.mean(times) / 1_000_000, Stats.median(times) / 1_000_000,
                Stats.standardDeviation(times) / 1_000_000);
    }

    private static void runAllBenchmarks(String distribution ,int[] input) throws IOException {
        System.out.println("\n=== " + distribution + " ===");

        System.out.println("\n-- BST --");
        long[] bstInsert=benchmarkInsert(BST::new,input,distribution,"BST");
        long[] bstContains= benchmarkContains(BST::new,input);
        long[] bstDelete=benchmarkDelete(BST::new,input);
        long[] bstSort=benchmarkSort(BST::new,input);

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
        long[] RBInsert=benchmarkInsert(RBTree::new,input,distribution,"RBTree");
        long[] RBContains= benchmarkContains(RBTree::new,input);
        long[] RBDelete=benchmarkDelete(RBTree::new,input);
        long[] RBSort=benchmarkSort(RBTree::new,input);

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


        System.out.printf("%nSpeedup (BST/RBTree):%n");
        System.out.printf("Insert speedup: %.2fx%n", Stats.mean(bstInsert) / Stats.mean(RBInsert));
        System.out.printf("Contains speedup: %.2fx%n", Stats.mean(bstContains) / Stats.mean(RBContains));
        System.out.printf("Delete speedup: %.2fx%n", Stats.mean(bstDelete) / Stats.mean(RBDelete));
        System.out.printf("Sort speedup: %.2fx%n", Stats.mean(bstSort) / Stats.mean(RBSort));
    }

    private static void writeToCSV(String distribution, String structure, String operation, long[] times) throws IOException {
        FileWriter fw = new FileWriter("results.csv", true); // true = append mode
        PrintWriter pw = new PrintWriter(fw);
        pw.printf("%s,%s,%s,%.2f,%.2f,%.2f%n",
                distribution, structure, operation,
                Stats.mean(times)/1_000_000,
                Stats.median(times)/1_000_000,
                Stats.standardDeviation(times)/1_000_000);
        pw.close();
    }
}
