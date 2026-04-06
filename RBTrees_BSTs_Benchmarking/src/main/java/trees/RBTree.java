package trees;

import trees.Node.BSTNode;
import trees.Node.RBNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;

/**
 * Red-Black Tree (RBTree) implementation of {@link TreeInterface}.
 * <p>
 * A self-balancing BST that guarantees O(log N) worst-case time for all core
 * operations (insert, delete, contains) by enforcing four invariants:
 * <ol>
 * <li>Every node is Red or Black.</li>
 * <li>The root is always Black.</li>
 * <li>No two consecutive Red nodes on any root-to-leaf path.</li>
 * <li>Every path from root to a NIL leaf has the same number of Black nodes
 * (the "black-height" property).</li>
 * </ol>
 * </p>
 * <p>
 * <b>Sentinel NIL node:</b> Instead of {@code null}, all empty leaf slots and
 * the root's parent point to a single shared black {@code NIL} node. This
 * eliminates null-pointer checks throughout the rotation and fixup code.
 * </p>
 * <p>
 * Debug-level logging via SLF4J traces every traversal decision, recoloring,
 * and rotation. Structural validation via {@link Validator} can be enabled by
 * setting {@code Validator.validate = true} (requires JVM {@code -ea} flag).
 * </p>
 */
public class RBTree implements TreeInterface {

    /** The root of the tree. Points to NIL when the tree is empty. */
    private RBNode root;

    /**
     * The shared sentinel NIL node used instead of {@code null}.
     * Always Black. Its left, right, and parent pointers all point to itself.
     */
    private final RBNode NIL;

    /** Tracks the number of elements currently in the tree. */
    private int size;

    /** SLF4J logger for tracing RBTree operations at DEBUG level. */
    private static final Logger logger = LoggerFactory.getLogger(RBTree.class);

    /**
     * Constructs an empty Red-Black Tree.
     * <p>
     * Initializes the sentinel NIL node (Black, self-referential) and sets
     * the root to NIL to represent an empty tree.
     * </p>
     */
    public RBTree() {
        this.NIL = new RBNode(0);
        NIL.left = NIL.right = NIL.parent = NIL; // NIL points to itself in all directions
        this.NIL.isRed = false; // NIL is always Black
        this.root = NIL; // empty tree: root is NIL
        this.size = 0;
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
     * Recursively computes height as {@code 1 + max(leftHeight, rightHeight)},
     * stopping at the NIL sentinel. Returns 0 for an empty tree.
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
    private int heightHelper(RBNode node) {
        if (node == NIL)
            return 0; // NIL sentinel = empty subtree
        int leftHeight = heightHelper(node.left);
        int rightHeight = heightHelper(node.right);
        return 1 + Math.max(leftHeight, rightHeight);
    }

    /**
     * {@inheritDoc}
     * <p>
     * Performs a standard iterative BST search comparing against the NIL sentinel.
     * </p>
     */
    @Override
    public boolean contains(int v) {
        logger.debug("Searching for value:{}", v);
        return containsHelper(root, v);
    }

    /**
     * Recursive contains helper. Traverses left/right until a match or NIL is
     * reached.
     *
     * @param node the current node being examined
     * @param v    the value to search for
     * @return {@code true} if {@code v} is found; {@code false} if NIL is reached
     */
    private boolean containsHelper(RBNode node, int v) {
        if (node == NIL) {
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
     * <p>
     * Performs an in-order traversal (left → node → right), collecting results
     * into an {@code ArrayList} which is then copied to a plain {@code int[]}.
     * </p>
     */
    @Override
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
     * Recursive in-order traversal helper. Stops at the NIL sentinel.
     *
     * @param node the current node
     * @param arr  the list accumulating values in sorted order
     */
    private void inOrderHelper(RBNode node, ArrayList<Integer> arr) {
        if (node == NIL)
            return;

        inOrderHelper(node.left, arr);
        arr.add(node.data);
        inOrderHelper(node.right, arr);
    }

    /**
     * {@inheritDoc}
     * <p>
     * Rejects duplicate values. Inserts a new Red node using standard BST
     * insertion,
     * then restores Red-Black properties via {@link #insertFixup(RBNode)}.
     * After a successful insertion, optionally validates the tree via
     * {@link Validator#checkRBTree(RBTree)}.
     * </p>
     */
    @Override
    public boolean insert(int v) {
        logger.debug("Inserting value {} in the tree", v);
        if (contains(v)) {
            logger.debug("Value {} already existing", v);
            return false; // duplicates are not allowed
        }
        InsertHelper(root, new RBNode(v));
        logger.debug("value {} has been successfully inserted to the tree", v);
        size++;
        if (Validator.validate)
            Validator.checkRBTree(this); // structural check (disabled by default)
        return true;
    }

    /**
     * Standard BST insertion for a new {@link RBNode}.
     * Walks the tree to find the correct position, attaches the node,
     * then calls {@link #insertFixup(RBNode)} to restore Red-Black invariants.
     *
     * @param node the current root of the tree (starting point)
     * @param z    the new node to insert (already colored Red)
     */
    private void InsertHelper(RBNode node, RBNode z) {
        RBNode y = NIL; // y tracks the parent of the current position
        RBNode x = node; // x walks down the tree
        while (x != NIL) {
            y = x;
            if (z.data < x.data) {
                logger.debug("Inserted value {} is less than current node value {}.Traversing Left", z.data, x.data);
                x = x.left;
            } else {
                logger.debug("Inserted value {} is greater than current node value {}.Traversing right", z.data,
                        x.data);
                x = x.right;
            }
        }

        logger.debug("Correct place found.Inserting value {} in the tree", z.data);
        z.parent = y; // set new node's parent
        if (y == NIL)
            root = z; // tree was empty — new node becomes root
        else if (z.data < y.data)
            y.left = z; // attach as left child
        else
            y.right = z; // attach as right child
        z.left = NIL; // new node's children are NIL
        z.right = NIL;

        logger.debug("Fixing Node: {}", z.data);
        insertFixup(z); // restore Red-Black properties
    }

    /**
     * Restores Red-Black invariants after a new Red node {@code z} is inserted.
     * <p>
     * Loops while the parent is Red (which violates the "no consecutive reds"
     * rule).
     * Handles two symmetric groups of cases depending on whether the parent is a
     * left or right child of the grandparent:
     * </p>
     * <ul>
     * <li><b>Case 1 — Uncle is Red:</b> Recolor parent and uncle to Black,
     * grandparent to Red, then move {@code z} up to grandparent and repeat.</li>
     * <li><b>Case 2 — Uncle is Black, z is an inner child:</b> Rotate parent
     * in the opposite direction to convert to Case 3.</li>
     * <li><b>Case 3 — Uncle is Black, z is an outer child:</b> Recolor parent
     * Black and grandparent Red, then rotate grandparent to restore balance.</li>
     * </ul>
     * After the loop, the root is always forced Black.
     *
     * @param z the newly inserted node (starts Red)
     */
    private void insertFixup(RBNode z) {
        while (z.parent.isRed) // violation: consecutive red nodes
        {
            logger.debug("A violation exist between Node {} and Node {}", z.data, z.parent.data);
            if (z.parent == z.parent.parent.left) {
                // z's parent is a LEFT child of the grandparent
                logger.debug("Node {} is in the left subtree", z.data);
                RBNode uncle = z.parent.parent.right;
                if (uncle.isRed) {
                    // Case 1: uncle is Red — recolor and move up
                    logger.debug("Case 1 detected.Uncle {} is Red", uncle.data);
                    logger.debug("Recoloring parent {} and uncle {} to black and grandparent {} to red", z.parent.data,
                            uncle.data, z.parent.parent.data);
                    uncle.isRed = false;
                    z.parent.isRed = false;
                    z.parent.parent.isRed = true;
                    z = z.parent.parent; // move z up to grandparent
                } else {
                    logger.debug("Uncle {} is black", uncle.data);
                    if (z == z.parent.right) {
                        // Case 2: z is an inner (right) child — rotate parent left to reach Case 3
                        logger.debug("Case 2 detected.left rotating parent {}", z.parent);
                        z = z.parent;
                        rotateLeft(z);
                    }
                    // Case 3: z is an outer (left) child — recolor and rotate grandparent right
                    logger.debug(
                            "Case 3 detected.recoloring parent {} to black and grandparent {} to red and right rotating grandparent {} ",
                            z.parent.data, z.parent.parent.data, z.parent.parent.data);
                    z.parent.isRed = false;
                    z.parent.parent.isRed = true;
                    rotateRight(z.parent.parent);
                }
            } else {
                // z's parent is a RIGHT child of the grandparent — mirror cases
                logger.debug("Node {} is in the right subtree", z.data);
                RBNode uncle = z.parent.parent.left;
                if (uncle.isRed) {
                    // Case 1 (mirror): uncle is Red — recolor and move up
                    logger.debug("Case 1 detected.Uncle {} is Red", uncle.data);
                    logger.debug("Recoloring parent {} and uncle {} to black and grandparent {} to red", z.parent.data,
                            uncle.data, z.parent.parent.data);
                    uncle.isRed = false;
                    z.parent.isRed = false;
                    z.parent.parent.isRed = true;
                    z = z.parent.parent;
                } else {
                    logger.debug("Uncle {} is black", uncle.data);
                    if (z == z.parent.left) {
                        // Case 2 (mirror): z is an inner (left) child — rotate parent right
                        logger.debug("Case 2 detected.right rotating parent {}", z.parent);
                        z = z.parent;
                        rotateRight(z);
                    }
                    // Case 3 (mirror): z is an outer (right) child — recolor and rotate grandparent
                    // left
                    logger.debug(
                            "Case 3 detected.recoloring parent {} to black and grandparent {} to red and left rotating grandparent {} ",
                            z.parent.data, z.parent.parent.data, z.parent.parent.data);
                    z.parent.isRed = false;
                    z.parent.parent.isRed = true;
                    rotateLeft(z.parent.parent);
                }

            }
        }
        root.isRed = false; // root must always be Black
    }

    /**
     * {@inheritDoc}
     * <p>
     * Locates the node with value {@code v} iteratively. If found, performs
     * a standard RB-Tree deletion (handling three structural cases) and restores
     * Red-Black invariants via {@link #fixDelete(RBNode)} if a Black node was
     * removed.
     * After deletion, optionally validates the tree via
     * {@link Validator#checkRBTree(RBTree)}.
     * </p>
     */
    @Override
    public boolean delete(int v) {
        logger.debug("deleting value {} from the tree", v);
        RBNode z = findNode(root, v);
        if (z == NIL) {
            logger.debug("Value {} doesn't exist in the tree", v);
            return false;
        }
        deleteNode(z);
        logger.debug("value {} has been successfully deleted from tree", v);
        size--;
        if (Validator.validate)
            Validator.checkRBTree(this); // structural check (disabled by default)
        return true;
    }

    /**
     * Iteratively searches for a node with the given key.
     *
     * @param node the starting node (typically root)
     * @param key  the value to find
     * @return the matching {@link RBNode}, or NIL if not found
     */
    private RBNode findNode(RBNode node, int key) {
        while (node != NIL) {
            if (key == node.data)
                return node;
            else if (key < node.data)
                node = node.left;
            else
                node = node.right;
        }
        return NIL; // not found
    }

    /**
     * Removes node {@code z} from the tree, handling three structural cases:
     * <ol>
     * <li><b>No left child</b> — transplant {@code z.right} into {@code z}'s
     * position.</li>
     * <li><b>No right child</b> — transplant {@code z.left} into {@code z}'s
     * position.</li>
     * <li><b>Two children</b> — find in-order successor {@code y}, move {@code y}'s
     * right
     * child into {@code y}'s old position, then transplant {@code y} into
     * {@code z}'s
     * position, inheriting {@code z}'s color.</li>
     * </ol>
     * If the node physically removed was Black, calls {@link #fixDelete(RBNode)}
     * to restore the black-height invariant.
     *
     * @param z the node to remove
     */
    private void deleteNode(RBNode z) {
        RBNode y = z; // y = the node that's actually removed/moved
        boolean yOriginalColor = y.isRed; // remember original color to decide if fixup is needed
        RBNode x; // x = node that takes y's original position

        if (z.left == NIL) {
            // Case 1: no left child — replace z with its right child
            x = z.right;
            transplant(z, z.right);
        } else if (z.right == NIL) {
            // Case 2: no right child — replace z with its left child
            x = z.left;
            transplant(z, z.left);
        } else {
            // Case 3: z has two children — find in-order successor (leftmost in right
            // subtree)
            y = successor(z);
            yOriginalColor = y.isRed;
            x = y.right;

            if (y.parent == z) {
                // successor is z's direct right child — x's parent pointer needs update
                x.parent = y;
            } else {
                // move successor's right child into successor's old spot
                transplant(y, y.right);
                y.right = z.right;
                y.right.parent = y;
            }

            // put successor into z's position
            transplant(z, y);
            y.left = z.left;
            y.left.parent = y;
            y.isRed = z.isRed; // inherit z's color to preserve black-height
        }

        // if the removed node was Black, we may have broken the black-height invariant
        if (!yOriginalColor) {
            fixDelete(x);
        }
    }

    /**
     * Restores Red-Black invariants after a Black node was removed.
     * <p>
     * Node {@code x} is treated as carrying an "extra Black" (double-black).
     * The loop eliminates the extra Black using four cases (mirrored for
     * left/right):
     * </p>
     * <ul>
     * <li><b>Case 1 — Sibling is Red:</b> Recolor sibling Black and parent Red,
     * rotate parent, update sibling. Converts to Cases 2–4.</li>
     * <li><b>Case 2 — Both sibling's children are Black:</b> Recolor sibling Red
     * and move the extra Black up to the parent.</li>
     * <li><b>Case 3 — Sibling's far child is Black:</b> Recolor sibling's near
     * child
     * Black and sibling Red, rotate sibling. Converts to Case 4.</li>
     * <li><b>Case 4 — Sibling's far child is Red:</b> Set sibling's color to
     * parent's
     * color, color parent and sibling's far child Black, rotate parent. Done.</li>
     * </ul>
     * At the end, {@code x} is colored Black to absorb the extra Black.
     *
     * @param x the node that is "double-black" after deletion
     */
    private void fixDelete(RBNode x) {
        while (x != root && !x.isRed) {

            if (x == x.parent.left) {
                RBNode w = x.parent.right; // w is x's sibling

                // Case 1: sibling is RED
                if (w.isRed) {
                    w.isRed = false;
                    x.parent.isRed = true;
                    rotateLeft(x.parent);
                    w = x.parent.right; // update sibling after rotation
                }

                // Case 2: both children BLACK
                if (!w.left.isRed && !w.right.isRed) {
                    w.isRed = true; // push double-black up
                    x = x.parent;
                } else {

                    // Case 3: right child BLACK (far child) — prepare for Case 4
                    if (!w.right.isRed) {
                        w.left.isRed = false;
                        w.isRed = true;
                        rotateRight(w);
                        w = x.parent.right;
                    }

                    // Case 4: right child RED — fix double-black and terminate
                    w.isRed = x.parent.isRed;
                    x.parent.isRed = false;
                    w.right.isRed = false;
                    rotateLeft(x.parent);
                    x = root; // signal loop termination
                }

            } else {
                // 🔁 MIRROR CASES (x is right child)

                RBNode w = x.parent.left; // w is x's sibling (on the left)

                // Case 1 (mirror): sibling is RED
                if (w.isRed) {
                    w.isRed = false;
                    x.parent.isRed = true;
                    rotateRight(x.parent);
                    w = x.parent.left;
                }

                // Case 2 (mirror): both children BLACK
                if (!w.right.isRed && !w.left.isRed) {
                    w.isRed = true;
                    x = x.parent;
                } else {

                    // Case 3 (mirror): left child BLACK
                    if (!w.left.isRed) {
                        w.right.isRed = false;
                        w.isRed = true;
                        rotateLeft(w);
                        w = x.parent.left;
                    }

                    // Case 4 (mirror): left child RED
                    w.isRed = x.parent.isRed;
                    x.parent.isRed = false;
                    w.left.isRed = false;
                    rotateRight(x.parent);
                    x = root;
                }
            }
        }

        x.isRed = false; // absorb the extra Black (or color root Black)
    }

    /**
     * Replaces subtree rooted at {@code u} with the subtree rooted at {@code v}.
     * Updates {@code u}'s parent to point to {@code v} instead of {@code u},
     * and sets {@code v}'s parent pointer to {@code u}'s old parent.
     *
     * @param u the node being replaced
     * @param v the node taking {@code u}'s place
     */
    private void transplant(RBNode u, RBNode v) {
        if (u.parent == NIL)
            root = v; // u was the root — v becomes new root
        else if (u == u.parent.left)
            u.parent.left = v; // u was a left child
        else
            u.parent.right = v; // u was a right child
        v.parent = u.parent; // always update v's parent
    }

    /**
     * Finds the in-order successor of {@code node}: the minimum node in its right
     * subtree.
     * Walks right once, then as far left as possible.
     *
     * @param node the node whose successor is needed (must have a right child)
     * @return the successor {@link RBNode}
     */
    private RBNode successor(RBNode node) {
        node = node.right;
        while (node.left != NIL)
            node = node.left;
        return node;
    }

    /**
     * Performs a left rotation at node {@code x}.
     * <p>
     * Promotes {@code x.right} (call it {@code y}) up to {@code x}'s position.
     * {@code y.left} becomes {@code x.right}, and {@code x} becomes {@code y.left}.
     * All parent pointers are updated accordingly.
     * </p>
     *
     * @param x the node to rotate around (goes down-left)
     */
    private void rotateLeft(RBNode x) {
        logger.debug("Rotating left at node: {}", x.data);
        RBNode y = x.right; // y is x's right child — it will move up
        x.right = y.left; // y's left subtree becomes x's right subtree
        if (y.left != NIL)
            y.left.parent = x; // update parent pointer if not NIL
        y.parent = x.parent; // y takes x's place in the tree
        if (x.parent == NIL)
            root = y; // x was root — y is the new root
        else if (x.parent.left == x)
            x.parent.left = y; // x was a left child
        else
            x.parent.right = y; // x was a right child
        y.left = x; // x becomes y's left child
        x.parent = y;
    }

    /**
     * Performs a right rotation at node {@code y}.
     * <p>
     * Promotes {@code y.left} (call it {@code x}) up to {@code y}'s position.
     * {@code x.right} becomes {@code y.left}, and {@code y} becomes
     * {@code x.right}.
     * All parent pointers are updated accordingly.
     * </p>
     *
     * @param y the node to rotate around (goes down-right)
     */
    private void rotateRight(RBNode y) {
        logger.debug("Rotating right at node: {}", y.data);
        RBNode x = y.left; // x is y's left child — it will move up
        y.left = x.right; // x's right subtree becomes y's left subtree
        if (x.right != NIL)
            x.right.parent = y; // update parent pointer if not NIL
        x.parent = y.parent; // x takes y's place in the tree
        if (y.parent == NIL)
            root = x; // y was root — x is the new root
        else if (y.parent.right == y)
            y.parent.right = x; // y was a right child
        else
            y.parent.left = x; // y was a left child
        x.right = y; // y becomes x's right child
        y.parent = x;
    }

    /**
     * Returns the root node. Used by {@link Validator} to perform structural
     * checks.
     *
     * @return the root {@link RBNode}, or NIL if the tree is empty
     */
    public RBNode getRoot() {
        return root;
    }

    /**
     * Returns the sentinel NIL node. Used by {@link Validator} to distinguish
     * empty leaf positions from real nodes.
     *
     * @return the shared NIL {@link RBNode}
     */
    public RBNode getNIL() {
        return NIL;
    }

}
