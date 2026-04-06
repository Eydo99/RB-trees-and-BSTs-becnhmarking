package trees;

public interface TreeInterface {
    boolean insert (int v);
    boolean delete (int v);
    boolean contains (int v);
    int[] inOrder();
    int height();
    int size();

}
