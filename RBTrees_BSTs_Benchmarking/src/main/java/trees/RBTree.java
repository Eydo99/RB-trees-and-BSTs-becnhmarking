package trees;

import trees.Node.BSTNode;
import trees.Node.RBNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;


public class RBTree implements TreeInterface {

    private RBNode root;
    private final RBNode NIL;
    private int size;
    private static final Logger logger=LoggerFactory.getLogger(RBTree.class);

    public RBTree()
    {
        this.NIL=new RBNode(0);
        NIL.left=NIL.right=NIL.parent=NIL;
        this.NIL.isRed=false;
        this.root=NIL;
        this.size=0;
    }

    @Override
    public int size(){return size;}

    @Override
    public int height()
    {
        return heightHelper(root);
    }
    private int heightHelper(RBNode node)
    {
        if(node==NIL) return 0;
        int leftHeight=heightHelper(node.left);
        int rightHeight=heightHelper(node.right);
        return 1+Math.max(leftHeight,rightHeight);
    }

    @Override
    public boolean contains(int v) {
        logger.debug("Searching for value:{}", v);
        return containsHelper(root, v);
    }

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
    private void inOrderHelper(RBNode node, ArrayList<Integer> arr) {
        if (node == NIL) return;

        inOrderHelper(node.left, arr);
        arr.add(node.data);
        inOrderHelper(node.right, arr);
    }



    @Override
    public boolean insert(int v)
    {
        logger.debug("Inserting value {} in the tree", v);
        if (contains(v))
        {
            logger.debug("Value {} already existing", v);
            return false;
        }
        InsertHelper(root, new RBNode(v));
        logger.debug("value {} has been successfully inserted to the tree",v);
        size++;
        if(Validator.validate) Validator.checkRBTree(this);
        return true;
    }

    private void InsertHelper(RBNode node,RBNode z)
    {
        RBNode y=NIL;
        RBNode x=node;
        while(x!=NIL)
        {
            y=x;
            if(z.data<x.data)
            {
                logger.debug("Inserted value {} is less than current node value {}.Traversing Left", z.data, x.data);
                x=x.left;
            }
            else {
                logger.debug("Inserted value {} is greater than current node value {}.Traversing right", z.data, x.data);
                x=x.right;
            }
        }

        logger.debug("Correct place found.Inserting value {} in the tree", z.data);
        z.parent=y;
        if(y==NIL) root = z;
        else if (z.data<y.data) y.left=z;
        else y.right=z;
        z.left=NIL;
        z.right=NIL;

        logger.debug("Fixing Node: {}",z.data);
        insertFixup(z);
    }

    private void insertFixup(RBNode z)
    {
        while (z.parent.isRed)
        {
            logger.debug("A violation exist between Node {} and Node {}",z.data,z.parent.data);
            if(z.parent==z.parent.parent.left)
            {
                logger.debug("Node {} is in the left subtree",z.data);
                RBNode uncle=z.parent.parent.right;
                if(uncle.isRed)
                {
                    logger.debug("Case 1 detected.Uncle {} is Red",uncle.data);
                    logger.debug("Recoloring parent {} and uncle {} to black and grandparent {} to red",z.parent.data,uncle.data,z.parent.parent.data);
                    uncle.isRed=false;
                    z.parent.isRed=false;
                    z.parent.parent.isRed=true;
                    z=z.parent.parent;
                }
                else
                {
                    logger.debug("Uncle {} is black",uncle.data);
                    if (z==z.parent.right)
                    {
                        logger.debug("Case 2 detected.left rotating parent {}",z.parent);
                        z=z.parent;
                        rotateLeft(z);
                    }
                    logger.debug("Case 3 detected.recoloring parent {} to black and grandparent {} to red and right rotating grandparent {} ",z.parent.data,z.parent.parent.data,z.parent.parent.data);
                    z.parent.isRed=false;
                    z.parent.parent.isRed=true;
                    rotateRight(z.parent.parent);
                }
            }
            else
            {
                logger.debug("Node {} is in the right subtree",z.data);
                RBNode uncle =z.parent.parent.left;
                if(uncle.isRed)
                {
                    logger.debug("Case 1 detected.Uncle {} is Red",uncle.data);
                    logger.debug("Recoloring parent {} and uncle {} to black and grandparent {} to red",z.parent.data,uncle.data,z.parent.parent.data);
                    uncle.isRed=false;
                    z.parent.isRed=false;
                    z.parent.parent.isRed=true;
                    z=z.parent.parent;
                }
                else
                {
                    logger.debug("Uncle {} is black",uncle.data);
                    if (z==z.parent.left)
                    {
                        logger.debug("Case 2 detected.right rotating parent {}",z.parent);
                        z=z.parent;
                        rotateRight(z);
                    }
                    logger.debug("Case 3 detected.recoloring parent {} to black and grandparent {} to red and left rotating grandparent {} ",z.parent.data,z.parent.parent.data,z.parent.parent.data);
                    z.parent.isRed=false;
                    z.parent.parent.isRed=true;
                    rotateLeft(z.parent.parent);
                }

            }
        }
        root.isRed=false;
    }


    @Override
    public boolean delete(int v)
    {
        logger.debug("deleting value {} from the tree",v);
        RBNode z=findNode(root,v);
        if(z==NIL)
        {
            logger.debug("Value {} doesn't exist in the tree",v);
            return false;
        }
        deleteNode(z);
        logger.debug("value {} has been successfully deleted from tree",v);
        size--;
        if(Validator.validate) Validator.checkRBTree(this);
        return true;
    }
    private RBNode findNode(RBNode node, int key)
    {
        while (node!=NIL)
        {
            if(key==node.data) return node;
            else if(key<node.data) node=node.left;
            else node=node.right;
        }
        return NIL;
    }

    private void deleteNode(RBNode z) {
        RBNode y=z;
        boolean yOriginalColor=y.isRed;
        RBNode x;
        if (z.left==NIL)
        {
            x=z.right;
            transplant(z,z.right);
        }
        else if (z.right==NIL)
        {
            x=z.left;
            transplant(z,z.left);
        }
        else
        {
            y=successor(z);
            yOriginalColor=y.isRed;
            x=y.right;

            if (y.parent==z) x.parent=y;
            else
            {
                transplant(y,y.right);
                y.right=z.right;
                y.right.parent=y;
            }

            transplant(z,y);
            y.left=z.left;
            y.left.parent=y;
            y.isRed=z.isRed;
        }

        if (!yOriginalColor) fixDelete(x);
    }

    private void fixDelete(RBNode x) {
        while (x!=root && !x.isRed)
        {

            //left subtree
            if (x==x.parent.left)
            {
                RBNode w=x.parent.right;

                // Case 1:sibling is red
                if (w.isRed)
                {
                    w.isRed=false;
                    x.parent.isRed=true;
                    rotateLeft(x.parent);
                    w=x.parent.right;
                }

                // Case 2:both children black
                if (!w.left.isRed && !w.right.isRed)
                {
                    w.isRed=true;
                    x=x.parent;
                }
                else
                {
                    // Case 3:right child black
                    if (!w.right.isRed)
                    {
                        w.left.isRed=false;
                        w.isRed=true;
                        rotateRight(w);
                        w=x.parent.right;
                    }

                    // Case 4:right child red
                    w.isRed=x.parent.isRed;
                    x.parent.isRed=false;
                    w.right.isRed=false;
                    rotateLeft(x.parent);
                    x=root;
                }

            }
            else
            {
                RBNode w=x.parent.left;

                // Case 1:sibling is red
                if (w.isRed)
                {
                    w.isRed=false;
                    x.parent.isRed=true;
                    rotateRight(x.parent);
                    w=x.parent.left;
                }

                // Case 2:both children black
                if (!w.right.isRed && !w.left.isRed)
                {
                    w.isRed=true;
                    x=x.parent;
                }
                else
                {
                    // Case 3:left child black
                    if (!w.left.isRed)
                    {
                        w.right.isRed=false;
                        w.isRed=true;
                        rotateLeft(w);
                        w=x.parent.left;
                    }

                    // Case 4:left child red
                    w.isRed=x.parent.isRed;
                    x.parent.isRed=false;
                    w.left.isRed=false;
                    rotateRight(x.parent);
                    x=root;
                }
            }
        }

        x.isRed = false;
    }



    private void transplant(RBNode u, RBNode v)
    {
        if(u.parent==NIL) root =v;
        else if(u==u.parent.left) u.parent.left=v;
        else u.parent.right=v;
        v.parent=u.parent;
    }

    private RBNode successor(RBNode node)
    {
        node=node.right;
        while (node.left!=NIL) node=node.left;
        return node;
    }

    private void rotateLeft (RBNode x)
    {
        logger.debug("Rotating left at node: {}",x.data);
        RBNode y=x.right;
        x.right=y.left;
        if(y.left!=NIL) y.left.parent=x;
        y.parent=x.parent;
        if(x.parent==NIL) root = y;
        else if(x.parent.left==x) x.parent.left=y;
        else x.parent.right=y;
        y.left=x;
        x.parent=y;
    }

    private void rotateRight(RBNode y)
    {
        logger.debug("Rotating right at node: {}",y.data);
        RBNode x=y.left;
        y.left=x.right;
        if(x.right!=NIL) x.right.parent=y;
        x.parent=y.parent;
        if(y.parent==NIL) root =x;
        else if(y.parent.right==y) y.parent.right=x;
        else y.parent.left=x;
        x.right=y;
        y.parent=x;
    }

    public RBNode getRoot() {
        return root;
    }

    public RBNode getNIL() {
        return NIL;
    }
}
