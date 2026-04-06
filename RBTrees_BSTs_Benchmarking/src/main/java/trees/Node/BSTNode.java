package trees.Node;

/**
 * Node class for the Binary Search Tree ({@link trees.BST}).
 * <p>
 * Each node stores an integer value and references to its left and right
 * children.
 * There is intentionally no parent pointer — the BST manages parent context
 * via the recursive call stack during traversal.
 * </p>
 */
public class BSTNode {

    /**
     * The left child node (contains values less than {@code data}). Null if absent.
     */
    public BSTNode left;

    /**
     * The right child node (contains values greater than {@code data}). Null if
     * absent.
     */
    public BSTNode right;

    /** The integer value stored in this node. */
    public int data;

    /**
     * Constructs a new BSTNode with the given value.
     * Both {@code left} and {@code right} default to {@code null}.
     *
     * @param data the integer value to store in this node
     */
    public BSTNode(int data) {
        this.data = data;
    }
}
