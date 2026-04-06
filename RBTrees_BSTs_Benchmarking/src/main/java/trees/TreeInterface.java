package trees;

/**
 * Common interface for tree data structures used in this benchmarking project.
 * <p>
 * Both {@link BST} and {@link RBTree} implement this interface, making them
 * interchangeable in the benchmarking harness via
 * {@code Supplier<TreeInterface>}.
 * All implementations store unique integer values only (no duplicates).
 * </p>
 */
public interface TreeInterface {

    /**
     * Inserts a value into the tree.
     *
     * @param v the integer value to insert
     * @return {@code true} if the value was inserted successfully,
     *         {@code false} if the value already exists in the tree (duplicates are
     *         rejected)
     */
    boolean insert(int v);

    /**
     * Deletes a value from the tree.
     *
     * @param v the integer value to delete
     * @return {@code true} if the value was found and removed,
     *         {@code false} if the value does not exist in the tree
     */
    boolean delete(int v);

    /**
     * Checks whether a value exists in the tree.
     *
     * @param v the integer value to search for
     * @return {@code true} if the value is present, {@code false} otherwise
     */
    boolean contains(int v);

    /**
     * Returns all elements stored in the tree in ascending (sorted) order.
     * Achieved via an in-order traversal (left → node → right).
     *
     * @return a sorted {@code int[]} containing all elements in the tree
     */
    int[] inOrder();

    /**
     * Returns the height of the tree.
     * Height is defined as the number of nodes on the longest root-to-leaf path.
     * An empty tree has height 0.
     *
     * @return the height of the tree
     */
    int height();

    /**
     * Returns the number of elements currently stored in the tree.
     *
     * @return the element count
     */
    int size();

}
