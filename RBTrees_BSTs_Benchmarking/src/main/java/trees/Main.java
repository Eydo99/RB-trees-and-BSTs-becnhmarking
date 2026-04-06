package trees;

import java.util.Arrays;

public class Main {
    public static void main(String[] args) {
        System.out.println("=== BST Tests ===");
        BST bst = new BST();

        bst.insert(5);
        bst.insert(3);
        bst.insert(7);
        bst.insert(1);
        bst.insert(4);

        System.out.println("Size (expected 5): " + bst.size());
        System.out.println("Height (expected 3): " + bst.height());
        System.out.println("Contains 3 (expected true): " + bst.contains(3));
        System.out.println("Contains 9 (expected false): " + bst.contains(9));
        System.out.println("InOrder (expected [1,3,4,5,7]): " + Arrays.toString(bst.inOrder()));

        bst.delete(3);
        System.out.println("After deleting 3:");
        System.out.println("Size (expected 4): " + bst.size());
        System.out.println("Contains 3 (expected false): " + bst.contains(3));
        System.out.println("InOrder (expected [1,4,5,7]): " + Arrays.toString(bst.inOrder()));

        System.out.println("\n=== RBTree Tests ===");
        RBTree rbt = new RBTree();

        rbt.insert(5);
        rbt.insert(3);
        rbt.insert(7);
        rbt.insert(1);
        rbt.insert(4);

        System.out.println("Size (expected 5): " + rbt.size());
        System.out.println("Height (expected 3): " + rbt.height());
        System.out.println("Contains 3 (expected true): " + rbt.contains(3));
        System.out.println("Contains 9 (expected false): " + rbt.contains(9));
        System.out.println("InOrder (expected [1,3,4,5,7]): " + Arrays.toString(rbt.inOrder()));

        rbt.delete(3);
        System.out.println("After deleting 3:");
        System.out.println("Size (expected 4): " + rbt.size());
        System.out.println("Contains 3 (expected false): " + rbt.contains(3));
        System.out.println("InOrder (expected [1,4,5,7]): " + Arrays.toString(rbt.inOrder()));
    }
}