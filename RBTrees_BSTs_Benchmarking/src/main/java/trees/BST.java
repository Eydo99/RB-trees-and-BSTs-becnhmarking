package trees;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import trees.Node.BSTNode;

import java.util.ArrayList;

public class BST implements  TreeInterface {

    private BSTNode root;

    public BSTNode getRoot() {
        return root;
    }

    public void setRoot(BSTNode root) {
        this.root = root;
    }

    private int size;
    private static final Logger logger = LoggerFactory.getLogger(BST.class);

    public BST() {
        this.root = null;
        this.size = 0;
    }

    @Override
    public boolean contains(int v) {
        logger.debug("Searching for value:{}", v);
        return containsHelper(root, v);
    }

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


    @Override
    public int size() {
        return size;
    }

    @Override
    public int height() {
        return heightHelper(root);

    }

    private int heightHelper(BSTNode node) {
        if (node == null) return 0;
        int leftHeight = heightHelper(node.left);
        int rightHeight = heightHelper(node.right);
        return 1 + Math.max(leftHeight, rightHeight);
    }


    public int[] inOrder() {
        ArrayList<Integer> inOrderArr = new ArrayList<>();
        inOrderHelper(root, inOrderArr);
        int[] inOrder = new int[size];
        for (int i = 0; i < size; i++) {
            inOrder[i] = inOrderArr.get(i);
        }
        return inOrder;
    }

    private void inOrderHelper(BSTNode node, ArrayList<Integer> arr) {
        if (node == null) return;

        inOrderHelper(node.left, arr);
        arr.add(node.data);
        inOrderHelper(node.right, arr);
    }

    @Override
    public boolean insert(int v) {
        logger.debug("Inserting value {} in the tree", v);
        if (contains(v)) {
            logger.debug("Value {} already existing", v);
            return false;
        }
        root = insertHelper(root, v);
        logger.debug("value {} has been successfully inserted to the tree",v);
        size++;
        if(Validator.validate)  Validator.checkBST(this);
        return true;
    }

    private BSTNode insertHelper(BSTNode node, int v) {
        if (node == null) {
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

    @Override
    public boolean delete(int v) {
        logger.debug("deleting value {} from the tree",v);
        if (contains(v)) {
            root = deleteHelper(root, v);
            logger.debug("value {} has been successfully deleted from tree",v);
            size--;
            if(Validator.validate) Validator.checkBST(this);
            return true;
        }
        logger.debug("Value {} doesn't exist in the tree",v);
        return false;
    }

    private BSTNode deleteHelper(BSTNode node, int v) {
        if (node == null) return node;
        if (v < node.data) {
            logger.debug("deleted value {} is less than current node value {}.Traversing Left", v, node.data);
            node.left = deleteHelper(node.left, v);
        } else if (v > node.data) {
            logger.debug("deleted value {} is greater than current node value {}.Traversing Right", v, node.data);
            node.right = deleteHelper(node.right, v);
        } else {
            logger.debug("value {} to be deleted is found,deleting it",v);
            if (node.left == null && node.right == null) node = null;
            else if (node.right != null) {
                node.data = successor(node);
                node.right = deleteHelper(node.right,node.data);
            } else {
                node.data = predecessor(node);
                node.left = deleteHelper(node.left,node.data);
            }
        }
        return node;
    }

    private int successor(BSTNode node) {
        node = node.right;
        while (node.left != null) {
            node = node.left;
        }
        return node.data;
    }
    private int predecessor(BSTNode node)
    {
        node=node.left;
        while(node.right!=null)
        {
            node=node.right;
        }
        return node.data;
    }
}

