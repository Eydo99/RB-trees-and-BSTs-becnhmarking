package trees;

import java.util.Arrays;

/**
 * Lightweight smoke-test entry point for the {@link BST} and {@link RBTree}
 * implementations.
 * <p>
 * This class is <strong>not</strong> the benchmarking entry point — that is
 * {@link trees.benchmark.BenchmarkRunner}. {@code Main} is used for quick
 * manual
 * verification of basic operations (insert, delete, contains, size, height,
 * inOrder)
 * by printing expected vs. actual values to stdout.
 * </p>
 */
public class Main {
    public static void main(String[] args) {
        System.out.println("=== BST Tests ===");
        BST bst = new BST();

        // Insert five values to build a small BST
        bst.insert(5);
        bst.insert(3);
        bst.insert(7);
        bst.insert(1);
        bst.insert(4);

        // Verify basic properties after insertion
        System.out.println("Size (expected 5): " + bst.size());
        System.out.println("Height (expected 3): " + bst.height());
        System.out.println("Contains 3 (expected true): " + bst.contains(3));
        System.out.println("Contains 9 (expected false): " + bst.contains(9));
        System.out.println("InOrder (expected [1,3,4,5,7]): " + Arrays.toString(bst.inOrder()));

        // Delete a node with one child and re-verify
        bst.delete(3);
        System.out.println("After deleting 3:");
        System.out.println("Size (expected 4): " + bst.size());
        System.out.println("Contains 3 (expected false): " + bst.contains(3));
        System.out.println("InOrder (expected [1,4,5,7]): " + Arrays.toString(bst.inOrder()));

        System.out.println("\n=== RBTree Tests ===");
        RBTree rbt = new RBTree();

        // Insert the same five values into an RBTree
        rbt.insert(5);
        rbt.insert(3);
        rbt.insert(7);
        rbt.insert(1);
        rbt.insert(4);

        // Verify basic properties — height may differ from BST due to self-balancing
        System.out.println("Size (expected 5): " + rbt.size());
        System.out.println("Height (expected 3): " + rbt.height());
        System.out.println("Contains 3 (expected true): " + rbt.contains(3));
        System.out.println("Contains 9 (expected false): " + rbt.contains(9));
        System.out.println("InOrder (expected [1,3,4,5,7]): " + Arrays.toString(rbt.inOrder()));

        // Delete the same node and re-verify
        rbt.delete(3);
        System.out.println("After deleting 3:");
        System.out.println("Size (expected 4): " + rbt.size());
        System.out.println("Contains 3 (expected false): " + rbt.contains(3));
        System.out.println("InOrder (expected [1,4,5,7]): " + Arrays.toString(rbt.inOrder()));
    }
}