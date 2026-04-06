package trees.Node;

public class RBNode {
   public RBNode parent;
   public RBNode left;
   public RBNode right;
   public int data;
   public boolean isRed;

    public RBNode(int data)
    {
        this.data=data;
        this.isRed=true;
    }
}
