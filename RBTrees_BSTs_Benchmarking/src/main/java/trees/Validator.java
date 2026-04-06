package trees;

import trees.Node.BSTNode;

import java.util.HashSet;

public class Validator {

    public static final boolean validate=false;

    public static void checkBST(BST tree)
    {
        assert isOrdered(tree.getRoot(),Integer.MIN_VALUE,Integer.MAX_VALUE);
        assert sizeConsistency(tree);
        assert noCycles(tree.getRoot(),new HashSet<>());
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

    private static boolean sizeConsistency(BST tree)
    {
        int sizeCheck=sizeConsistencyHelper(tree.getRoot());
        return sizeCheck == tree.size();
    }

    private static int sizeConsistencyHelper(BSTNode node)
    {
        if(node==null) return 0;
        int leftSize= sizeConsistencyHelper(node.left);
        int rightSize=sizeConsistencyHelper(node.right);
        return 1+leftSize+rightSize;
    }

    private static boolean noCycles (BSTNode node,HashSet<BSTNode> visited)
    {
        if (node==null) return true;
        if(visited.contains(node)) return false;
        visited.add(node);
        return noCycles(node.left,visited) && noCycles(node.right,visited);
    }
}
