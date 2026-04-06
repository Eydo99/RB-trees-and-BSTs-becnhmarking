package trees;

import trees.Node.BSTNode;
import trees.Node.RBNode;

import java.util.HashSet;

/**
 * Assertion-based structural validator for {@link BST} and {@link RBTree}.
 * <p>
 * This class provides a suite of checks that verify the data-structure
 * invariants
 * of both tree types after every mutation (insert or delete). It is
 * <strong>disabled by default</strong> ({@code validate = false}) because it
 * adds
 * significant overhead — each call triggers a full tree traversal.
 * </p>
 * <p>
 * To enable validation:
 * <ol>
 * <li>Set {@code public static final boolean validate = true;}</li>
 * <li>Run the JVM with {@code -ea} (enable assertions) so that {@code assert}
 * statements are active.</li>
 * </ol>
 * Validation is intended for development and debugging, not production use.
 * </p>
 */
public class Validator {

    /**
     * Master switch for all structural validation.
     * {@code false} — validation is off (no overhead on mutations).
     * {@code true} — validation runs after every insert/delete (requires JVM
     * {@code -ea}).
     */
    public static final boolean validate = false;

    /**
     * Validates the full BST invariant suite after a mutation.
     * Asserts:
     * <ul>
     * <li>BST ordering property (all values within their valid range).</li>
     * <li>Size consistency (node count matches {@code tree.size()}).</li>
     * <li>No pointer cycles in the tree structure.</li>
     * </ul>
     *
     * @param tree the BST to validate
     */
    public static void checkBST(BST tree) {
        assert isOrdered(tree.getRoot(), Integer.MIN_VALUE, Integer.MAX_VALUE);
        assert sizeConsistency(tree);
        assert noCycles(tree.getRoot(), new HashSet<>());

    }

    /**
     * Validates the full Red-Black Tree invariant suite after a mutation.
     * Asserts:
     * <ul>
     * <li>BST ordering property (all values within their valid range).</li>
     * <li>Root is Black.</li>
     * <li>Size consistency (node count matches {@code tree.size()}).</li>
     * <li>No consecutive Red nodes on any path.</li>
     * <li>Uniform black-height on all root-to-NIL paths.</li>
     * </ul>
     *
     * @param tree the RBTree to validate
     */
    public static void checkRBTree(RBTree tree) {
        assert isOrdered(tree.getRoot(), Integer.MIN_VALUE, Integer.MAX_VALUE, tree.getNIL());
        assert rootIsBlack(tree.getRoot());
        assert sizeConsistency(tree);
        assert noConsecutiveReds(tree.getRoot(), tree.getNIL());
        assert blackHeight(tree.getRoot(), tree.getNIL()) != -1;
    }

    /**
     * Checks the BST ordering property recursively.
     * Each node's value must be strictly within the range {@code (min, max)}.
     *
     * @param node the current node
     * @param min  the minimum allowed value (exclusive) for this subtree
     * @param max  the maximum allowed value (exclusive) for this subtree
     * @return {@code true} if the subtree is correctly ordered
     */
    private static boolean isOrdered(BSTNode node, int min, int max) {
        if (node == null)
            return true; // empty subtree is trivially ordered
        if (node.data < min || node.data > max) {
            return false; // value is outside its valid range
        }
        return isOrdered(node.left, min, node.data) && isOrdered(node.right, node.data, max);
    }

    /**
     * Checks the RBTree ordering property recursively, using the NIL sentinel as
     * the stop condition.
     *
     * @param node the current node
     * @param min  the minimum allowed value (exclusive) for this subtree
     * @param max  the maximum allowed value (exclusive) for this subtree
     * @param NIL  the shared sentinel NIL node
     * @return {@code true} if the subtree is correctly ordered
     */
    private static boolean isOrdered(RBNode node, int min, int max, RBNode NIL) {
        if (node == NIL)
            return true; // NIL sentinel = empty subtree
        if (node.data < min || node.data > max) {
            return false;
        }
        return isOrdered(node.left, min, node.data, NIL) && isOrdered(node.right, node.data, max, NIL);
    }

    /**
     * Checks that the root is Black (RBTree invariant #2).
     *
     * @param node the root node of the RBTree
     * @return {@code true} if the root is Black
     */
    private static boolean rootIsBlack(RBNode node) {
        return !node.isRed;
    }

    /**
     * Verifies that the BST's tracked size matches the actual node count via
     * traversal.
     *
     * @param tree the BST to check
     * @return {@code true} if the sizes are consistent
     */
    private static boolean sizeConsistency(BST tree) {
        int sizeCheck = sizeConsistencyHelper(tree.getRoot());
        return sizeCheck == tree.size();
    }

    /**
     * Verifies that the RBTree's tracked size matches the actual node count via
     * traversal.
     *
     * @param tree the RBTree to check
     * @return {@code true} if the sizes are consistent
     */
    private static boolean sizeConsistency(RBTree tree) {
        int sizeCheck = sizeConsistencyHelper(tree.getRoot(), tree.getNIL());
        return sizeCheck == tree.size();
    }

    /**
     * Counts nodes in a BST subtree by recursive traversal.
     *
     * @param node the root of the subtree
     * @return the total node count in this subtree
     */
    private static int sizeConsistencyHelper(BSTNode node) {
        if (node == null)
            return 0;
        int leftSize = sizeConsistencyHelper(node.left);
        int rightSize = sizeConsistencyHelper(node.right);
        return 1 + leftSize + rightSize;
    }

    /**
     * Counts real (non-NIL) nodes in an RBTree subtree by recursive traversal.
     *
     * @param node the root of the subtree
     * @param NIL  the shared sentinel NIL node
     * @return the total node count in this subtree
     */
    private static int sizeConsistencyHelper(RBNode node, RBNode NIL) {
        if (node == NIL)
            return 0;
        int leftSize = sizeConsistencyHelper(node.left, NIL);
        int rightSize = sizeConsistencyHelper(node.right, NIL);
        return 1 + leftSize + rightSize;
    }

    /**
     * Detects pointer cycles in a BST by tracking visited nodes with a
     * {@link HashSet}.
     * A cycle exists if any node is encountered more than once during traversal.
     *
     * @param node    the current node
     * @param visited the set of already-visited nodes
     * @return {@code true} if no cycle is detected
     */
    private static boolean noCycles(BSTNode node, HashSet<BSTNode> visited) {
        if (node == null)
            return true;
        if (visited.contains(node))
            return false; // already seen — cycle detected
        visited.add(node);
        return noCycles(node.left, visited) && noCycles(node.right, visited);
    }

    /**
     * Checks the "no consecutive reds" invariant (RBTree invariant #3).
     * A Red node must not have a Red child.
     *
     * @param node the current node
     * @param NIL  the shared sentinel NIL node
     * @return {@code true} if no consecutive Red nodes exist in this subtree
     */
    private static boolean noConsecutiveReds(RBNode node, RBNode NIL) {
        if (node == NIL)
            return true;
        if (node.isRed) {
            if (node.left.isRed || node.right.isRed)
                return false; // Red parent with Red child — violation
        }
        return noConsecutiveReds(node.left, NIL) && noConsecutiveReds(node.right, NIL);
    }

    /**
     * Validates the black-height invariant (RBTree invariant #4).
     * Every path from this node to a NIL leaf must pass through the same number
     * of Black nodes. Returns {@code -1} on violation.
     *
     * @param node the current node
     * @param NIL  the shared sentinel NIL node
     * @return the black-height of this subtree, or {@code -1} if inconsistent
     */
    private static int blackHeight(RBNode node, RBNode NIL) {
        if (node == NIL)
            return 1; // NIL itself counts as 1 Black node
        int leftBlackHeight = blackHeight(node.left, NIL);
        int rightBlackHeight = blackHeight(node.right, NIL);
        if (leftBlackHeight != rightBlackHeight)
            return -1; // unequal black-heights — violation
        else
            return ((node.isRed) ? 0 : 1) + leftBlackHeight; // add 1 only if this node is Black

    }
}
