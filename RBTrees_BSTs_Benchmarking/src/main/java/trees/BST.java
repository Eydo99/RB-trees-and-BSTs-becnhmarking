package trees;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import trees.Node.BSTNode;

import java.util.ArrayList;

/**
 * Unbalanced Binary Search Tree (BST) implementation of {@link TreeInterface}.
 * <p>
 * Stores unique integers. Supports O(log N) average-case operations on random
 * data,
 * but degrades to O(N) in the worst case (e.g., sorted input), where the tree
 * becomes a linked list with height = N.
 * </p>
 * <p>
 * Debug-level logging via SLF4J traces every traversal decision, insertion,
 * and deletion step. Structural validation via {@link Validator} can be enabled
 * by setting {@code Validator.validate = true} (requires JVM {@code -ea} flag).
 * </p>
 */
public class BST implements TreeInterface {

    /** The root node of the tree. Null when the tree is empty. */
    private BSTNode root;

    /** Tracks the number of elements currently in the tree. */
    private int size;

    /** SLF4J logger for tracing BST operations at DEBUG level. */
    private static final Logger logger = LoggerFactory.getLogger(BST.class);

    /**
     * Returns the root node. Used by {@link Validator} to perform structural
     * checks.
     *
     * @return the root {@link BSTNode}, or {@code null} if the tree is empty
     */
    public BSTNode getRoot() {
        return root;
    }

    /**
     * Constructs an empty BST with no elements.
     */
    public BST() {
        this.root = null;
        this.size = 0;
    }

    /**
     * {@inheritDoc}
     * <p>
     * Performs a standard recursive BST search starting from the root.
     * </p>
     */
    @Override
    public boolean contains(int v) {
        logger.debug("Searching for value:{}", v);
        return containsHelper(root, v);
    }

    /**
     * Recursive helper for {@link #contains(int)}.
     * Traverses left if {@code v < node.data}, right if {@code v > node.data},
     * and returns {@code true} on an exact match.
     *
     * @param node the current node being examined
     * @param v    the value to search for
     * @return {@code true} if {@code v} is found in the subtree rooted at
     *         {@code node}
     */
    private boolean containsHelper(BSTNode node, int v) {
        if (node == null) {
            logger.debug("value {} not found in tree", v);
            return false;
        }

        if (v < node.data) {
            logger.debug("value {} is less than current node value {}.Traversing Left", v, node.data);
            return containsHelper(node.left, v);
        } else if (v > node.data) {
            logger.debug("value {} is greater than current node value {}.Traversing Right", v, node.data);
            return containsHelper(node.right, v);
        } else {
            logger.debug("value {} found in tree", v);
            return true;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int size() {
        return size;
    }

    /**
     * {@inheritDoc}
     * <p>
     * Recursively computes height as {@code 1 + max(leftHeight, rightHeight)}.
     * Returns 0 for an empty tree.
     * </p>
     */
    @Override
    public int height() {
        return heightHelper(root);

    }

    /**
     * Recursive helper for {@link #height()}.
     *
     * @param node the current subtree root
     * @return the height of the subtree rooted at {@code node}
     */
    private int heightHelper(BSTNode node) {
        if (node == null)
            return 0; // base case: empty subtree has height 0
        int leftHeight = heightHelper(node.left);
        int rightHeight = heightHelper(node.right);
        return 1 + Math.max(leftHeight, rightHeight);
    }

    /**
     * {@inheritDoc}
     * <p>
     * Performs an in-order traversal (left → node → right), collecting results
     * into an {@code ArrayList} which is then copied to a plain {@code int[]}.
     * </p>
     */
    public int[] inOrder() {
        ArrayList<Integer> inOrderArr = new ArrayList<>();
        inOrderHelper(root, inOrderArr);
        int[] inOrder = new int[size];
        for (int i = 0; i < size; i++) {
            inOrder[i] = inOrderArr.get(i);
        }
        return inOrder;
    }

    /**
     * Recursive in-order traversal helper.
     * Visits left subtree, appends current node's value, then visits right subtree.
     *
     * @param node the current node
     * @param arr  the list accumulating values in sorted order
     */
    private void inOrderHelper(BSTNode node, ArrayList<Integer> arr) {
        if (node == null)
            return;

        inOrderHelper(node.left, arr);
        arr.add(node.data);
        inOrderHelper(node.right, arr);
    }

    /**
     * {@inheritDoc}
     * <p>
     * Rejects duplicate values. After a successful insertion, optionally
     * validates the BST invariants via {@link Validator#checkBST(BST)}.
     * </p>
     */
    @Override
    public boolean insert(int v) {
        logger.debug("Inserting value {} in the tree", v);
        if (contains(v)) {
            logger.debug("Value {} already existing", v);
            return false; // duplicates are not allowed
        }
        root = insertHelper(root, v);
        logger.debug("value {} has been successfully inserted to the tree", v);
        size++;
        if (Validator.validate)
            Validator.checkBST(this); // structural check (disabled by default)
        return true;
    }

    /**
     * Recursive helper for {@link #insert(int)}.
     * Navigates to the correct position and attaches a new {@link BSTNode}.
     *
     * @param node the current subtree root (may be {@code null} at a leaf boundary)
     * @param v    the value to insert
     * @return the (possibly new) subtree root after insertion
     */
    private BSTNode insertHelper(BSTNode node, int v) {
        if (node == null) {
            // Found the correct null position — create and return the new node
            logger.debug("Correct place found.Inserting value {} in the tree", v);
            node = new BSTNode(v);
            return node;
        }
        if (v < node.data) {
            logger.debug("Inserted value {} is less than current node value {}.Traversing Left", v, node.data);
            node.left = insertHelper(node.left, v);
        } else if (v > node.data) {
            logger.debug("Inserted value {} is greater than current node value {}.Traversing Right", v, node.data);
            node.right = insertHelper(node.right, v);
        }
        return node;
    }

    /**
     * {@inheritDoc}
     * <p>
     * Handles three deletion cases:
     * <ol>
     * <li><b>Leaf node</b> — simply removed (set to {@code null}).</li>
     * <li><b>Has right child</b> — replaced by its in-order successor
     * (leftmost node of right subtree), then the successor is deleted
     * recursively.</li>
     * <li><b>Has only left child</b> — replaced by its in-order predecessor
     * (rightmost node of left subtree), then the predecessor is deleted
     * recursively.</li>
     * </ol>
     * After deletion, optionally validates BST invariants via
     * {@link Validator#checkBST(BST)}.
     * </p>
     */
    @Override
    public boolean delete(int v) {
        logger.debug("deleting value {} from the tree", v);
        if (contains(v)) {
            root = deleteHelper(root, v);
            logger.debug("value {} has been successfully deleted from tree", v);
            size--;
            if (Validator.validate)
                Validator.checkBST(this); // structural check (disabled by default)
            return true;
        }
        logger.debug("Value {} doesn't exist in the tree", v);
        return false;
    }

    /**
     * Recursive helper for {@link #delete(int)}.
     * Navigates to the target node and removes it using one of the three deletion
     * strategies.
     *
     * @param node the current subtree root
     * @param v    the value to delete
     * @return the (possibly updated) subtree root after deletion
     */
    private BSTNode deleteHelper(BSTNode node, int v) {
        if (node == null)
            return node;
        if (v < node.data) {
            logger.debug("deleted value {} is less than current node value {}.Traversing Left", v, node.data);
            node.left = deleteHelper(node.left, v);
        } else if (v > node.data) {
            logger.debug("deleted value {} is greater than current node value {}.Traversing Right", v, node.data);
            node.right = deleteHelper(node.right, v);
        } else {
            logger.debug("value {} to be deleted is found,deleting it", v);
            if (node.left == null && node.right == null)
                node = null; // Case 1: leaf node
            else if (node.right != null) {
                // Case 2: has right subtree — replace with in-order successor
                node.data = successor(node);
                node.right = deleteHelper(node.right, node.data);
            } else {
                // Case 3: only left subtree — replace with in-order predecessor
                node.data = predecessor(node);
                node.left = deleteHelper(node.left, node.data);
            }
        }
        return node;
    }

    /**
     * Finds the in-order successor of {@code node}: the smallest value in its right
     * subtree.
     * Navigates right once, then as far left as possible.
     *
     * @param node the node whose successor is needed (must have a right child)
     * @return the successor's integer value
     */
    private int successor(BSTNode node) {
        node = node.right;
        while (node.left != null) {
            node = node.left;
        }
        return node.data;
    }

    /**
     * Finds the in-order predecessor of {@code node}: the largest value in its left
     * subtree.
     * Navigates left once, then as far right as possible.
     *
     * @param node the node whose predecessor is needed (must have a left child)
     * @return the predecessor's integer value
     */
    private int predecessor(BSTNode node) {
        node = node.left;
        while (node.right != null) {
            node = node.right;
        }
        return node.data;
    }
}
