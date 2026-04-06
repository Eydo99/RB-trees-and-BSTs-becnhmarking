package trees;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;


public class RBTreeTest {
    RBTree rbt;
    BST bst;

    @BeforeEach
    void setup()
    {
        rbt=new RBTree();
    }

    @Test
    void singleInsert()
    {
        rbt.insert(12);
        assertEquals(1, rbt.size());
    }
    @Test
    void duplicateInsert()
    {
        rbt.insert(5);
        boolean duplicateCheck=rbt.insert(5);
        assertFalse(duplicateCheck);
        assertEquals(1,rbt.size());
    }

    @Test
    void multipleInserts() {
        rbt.insert(20);
        rbt.insert(15);
        rbt.insert(25);
        rbt.insert(12);
        rbt.insert(1);
        rbt.insert(36);
        assertEquals(6, rbt.size());
    }
    @Test
    void checkAfterInsert()
    {
        rbt.insert(10);
        boolean insertCheck= rbt.contains(10);
        assertTrue(insertCheck);
    }


    @Test
    void checkAfterDeletion()
    {
        rbt.insert(10);
        rbt.delete(10);
        boolean containCheck= rbt.contains(10);
        assertFalse(containCheck);
    }

    @Test
    void containsEmpty()
    {
        boolean checkEmpty=rbt.contains(10);
        assertFalse(checkEmpty);
    }

    @Test
    void checkNoExist()
    {
        rbt.insert(10);
        boolean checkNoExist=rbt.contains(15);
        assertFalse(checkNoExist);
    }

    @Test
    void deleteExisting()
    {
        rbt.insert(10);
        rbt.insert(15);
        boolean deleteCheck=rbt.delete(10);
        assertTrue(deleteCheck);
        assertEquals(1,rbt.size());
    }

    @Test
    void deleteNonExisting()
    {
        rbt.insert(15);
        boolean deleteCheck=rbt.delete(10);
        assertFalse(deleteCheck);
    }

    @Test
    void deleteEmpty()
    {
        boolean deleteCheck=rbt.delete(10);
        assertFalse(deleteCheck);
    }

    @Test
    void deleteOnlyElement()
    {
        rbt.insert(10);
        rbt.delete(10);
        boolean containsCheck=rbt.contains(10);
        assertFalse(containsCheck);
        assertEquals(0,rbt.size());
    }

    @Test
    void deleteWIthTwoChildren()
    {
        rbt.insert(10);
        rbt.insert(15);
        rbt.insert(5);
        rbt.delete(10);
        assertEquals(2,rbt.size());

    }


    @Test
    void emptyHeight()
    {
        assertEquals(0,rbt.height());
    }

    @Test
    void singleElementHeight()
    {
        rbt.insert(10);
        assertEquals(1,rbt.height());
    }

    @Test
    void multipleElementHeight()
    {
        rbt.insert(15);
        rbt.insert(5);
        rbt.insert(35);
        rbt.insert(2);
        rbt.insert(0);
        assertEquals(3,rbt.height());
    }
    @Test
    void heightAfterDeletion()
    {
        rbt.insert(15);
        rbt.insert(5);
        rbt.insert(35);
        rbt.insert(2);
        rbt.insert(0);
        rbt.delete(0);
        rbt.delete(2);
        assertEquals(2,rbt.height());
    }



    @Test
    void emptySize()
    {
        assertEquals(0,rbt.size());
    }

    @Test
    void multipleInsertsSize()
    {
        rbt.insert(15);
        rbt.insert(5);
        rbt.insert(35);
        rbt.insert(2);
        rbt.insert(0);
        rbt.insert(23);
        rbt.insert(17);
        rbt.insert(41);
        rbt.insert(12);
        rbt.insert(20);
        assertEquals(10,rbt.size());
    }
    @Test
    void multipleInsertsAndDeletesSize()
    {
        rbt.insert(15);
        rbt.insert(5);
        rbt.insert(35);
        rbt.delete(5);
        rbt.insert(2);
        rbt.insert(0);
        rbt.insert(23);
        rbt.delete(15);
        rbt.delete(2);
        rbt.insert(17);
        rbt.insert(41);
        rbt.insert(12);
        rbt.delete(17);
        rbt.delete(41);
        rbt.insert(20);
        rbt.delete(20);
        assertEquals(4,rbt.size());
    }

    @Test
    void insertAndDeleteAll()
    {
        rbt.insert(15);
        rbt.insert(5);
        rbt.delete(5);
        rbt.insert(2);
        rbt.delete(15);
        rbt.delete(2);
        rbt.insert(17);
        rbt.insert(41);
        rbt.delete(17);
        rbt.delete(41);
        rbt.insert(20);
        rbt.delete(20);
        assertEquals(0,rbt.size());
    }

    @Test
    void inOrderSorted()
    {
        rbt.insert(15);
        rbt.insert(5);
        rbt.insert(35);
        rbt.insert(2);
        rbt.insert(0);
        rbt.insert(23);
        rbt.insert(17);
        rbt.insert(41);
        rbt.insert(12);
        rbt.insert(20);
        int[] arr={0,2,5,12,15,17,20,23,35,41};
        assertArrayEquals(arr,rbt.inOrder());
    }
    @Test
    void inorderEmpty()
    {
        int[] arr={};
        assertArrayEquals(arr,rbt.inOrder());
    }

    @Test
    void inOrderSortedAfterDeletion()
    {
        rbt.insert(15);
        rbt.insert(5);
        rbt.insert(35);
        rbt.insert(2);
        rbt.insert(0);
        rbt.insert(23);
        rbt.insert(17);
        rbt.insert(41);
        rbt.insert(12);
        rbt.delete(15);
        rbt.delete(41);
        rbt.insert(20);
        rbt.delete(20);
        int[] arr={0,2,5,12,17,23,35};
        assertArrayEquals(arr,rbt.inOrder());
    }

    @Test
    void heightOnSortedInput()
    {
        rbt.insert(1);
        rbt.insert(2);
        rbt.insert(3);
        rbt.insert(4);
        rbt.insert(5);
        rbt.insert(6);
        rbt.insert(7);
        rbt.insert(8);
        rbt.insert(9);
        rbt.insert(10);
        assertEquals(5,rbt.height());
    }

    @Test
    void heightComparison()
    {
        bst=new BST();
        bst.insert(1);
        bst.insert(2);
        bst.insert(3);
        bst.insert(4);
        rbt.insert(1);
        rbt.insert(2);
        rbt.insert(3);
        rbt.insert(4);
        assertTrue(rbt.height()<bst.height());

    }
}
