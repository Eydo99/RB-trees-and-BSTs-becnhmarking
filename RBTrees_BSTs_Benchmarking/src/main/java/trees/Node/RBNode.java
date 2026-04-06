package trees.Node;

public class RBNode {
    RBNode parent;
    RBNode left;
    RBNode right;
    int data;
    boolean isRed;

    public RBNode(int data)
    {
        this.data=data;
        this.isRed=true;
    }
}
