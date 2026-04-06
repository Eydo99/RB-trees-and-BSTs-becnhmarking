package trees;

import trees.Node.BSTNode;
import trees.Node.RBNode;

import java.util.HashSet;

public class Validator {

    public static final boolean validate=true;

    public static void checkBST(BST tree)
    {
        assert isOrdered(tree.getRoot(),Integer.MIN_VALUE,Integer.MAX_VALUE);
        assert sizeConsistency(tree);
        assert noCycles(tree.getRoot(),new HashSet<>());

    }

    public static void checkRBTree(RBTree tree)
    {
        assert isOrdered(tree.getRoot(),Integer.MIN_VALUE,Integer.MAX_VALUE,tree.getNIL());
        assert  rootIsBlack(tree.getRoot());
        assert sizeConsistency(tree);
        assert noConsecutiveReds(tree.getRoot(),tree.getNIL());
        assert blackHeight(tree.getRoot(),tree.getNIL()) !=-1;
    }

    private static boolean isOrdered(BSTNode node, int min, int max)
    {
        if(node==null) return true;
        if(node.data<min || node.data>max)
        {
            return false;
        }
        return isOrdered(node.left,min,node.data) && isOrdered(node.right,node.data,max);
    }
    private static boolean isOrdered(RBNode node, int min, int max,RBNode NIL)
    {
        if(node==NIL) return true;
        if(node.data<min || node.data>max)
        {
            return false;
        }
        return isOrdered(node.left,min,node.data,NIL) && isOrdered(node.right,node.data,max,NIL);
    }

    private static boolean rootIsBlack(RBNode node)
    {
        return !node.isRed;
    }

    private static boolean sizeConsistency(BST tree)
    {
        int sizeCheck=sizeConsistencyHelper(tree.getRoot());
        return sizeCheck == tree.size();
    }
    private static boolean sizeConsistency(RBTree tree)
    {
        int sizeCheck=sizeConsistencyHelper(tree.getRoot(),tree.getNIL());
        return sizeCheck == tree.size();
    }

    private static int sizeConsistencyHelper(BSTNode node)
    {
        if(node==null) return 0;
        int leftSize= sizeConsistencyHelper(node.left);
        int rightSize=sizeConsistencyHelper(node.right);
        return 1+leftSize+rightSize;
    }

    private static int sizeConsistencyHelper(RBNode node,RBNode NIL)
    {
        if(node==NIL) return 0;
        int leftSize=sizeConsistencyHelper(node.left,NIL);
        int rightSize=sizeConsistencyHelper(node.right,NIL);
        return 1 + leftSize + rightSize ;
    }

    private static boolean noCycles (BSTNode node,HashSet<BSTNode> visited)
    {
        if (node==null) return true;
        if(visited.contains(node)) return false;
        visited.add(node);
        return noCycles(node.left,visited) && noCycles(node.right,visited);
    }

    private static boolean noConsecutiveReds(RBNode node,RBNode NIL)
    {
        if(node==NIL) return true;
        if (node.isRed)
        {
            if (node.left.isRed || node.right.isRed) return false;
        }
        return noConsecutiveReds(node.left,NIL) && noConsecutiveReds(node.right,NIL);
    }

    private static int blackHeight(RBNode node,RBNode NIL)
    {
        if(node==NIL) return 1;
        int leftBlackHeight=blackHeight(node.left,NIL);
        int rightBlackHeight=blackHeight(node.right,NIL);
        if(leftBlackHeight!=rightBlackHeight) return -1;
        else return ((node.isRed) ? 0 : 1) +leftBlackHeight;

    }
}
