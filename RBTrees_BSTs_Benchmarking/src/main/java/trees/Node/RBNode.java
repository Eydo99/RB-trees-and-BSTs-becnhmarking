package trees.Node;

/**
 * Node class for the Red-Black Tree ({@link trees.RBTree}).
 * <p>
 * In addition to the standard BST child pointers, each RBNode also holds:
 * <ul>
 * <li>A {@code parent} pointer — required for the rotation and fixup
 * algorithms that keep the tree balanced.</li>
 * <li>A {@code boolean isRed} color flag — the core of the Red-Black
 * invariant tracking.</li>
 * </ul>
 * The tree uses a single shared <em>sentinel</em> NIL node (also an RBNode,
 * always black) in place of {@code null} to simplify boundary checks.
 * </p>
 */
public class RBNode {

    /** The parent node. Points to the NIL sentinel when this node is the root. */
    public RBNode parent;

    /**
     * The left child (values less than {@code data}). Points to NIL sentinel if
     * absent.
     */
    public RBNode left;

    /**
     * The right child (values greater than {@code data}). Points to NIL sentinel if
     * absent.
     */
    public RBNode right;

    /** The integer value stored in this node. */
    public int data;

    /**
     * Color of this node. {@code true} = Red, {@code false} = Black.
     * New nodes always start as Red — the insertFixup procedure re-colors as
     * needed.
     */
    public boolean isRed;

    /**
     * Constructs a new RBNode with the given value, colored Red by default.
     * Child and parent pointers are left unset (caller assigns them).
     *
     * @param data the integer value to store in this node
     */
    public RBNode(int data) {
        this.data = data;
        this.isRed = true; // new nodes are always inserted as Red
    }
}
