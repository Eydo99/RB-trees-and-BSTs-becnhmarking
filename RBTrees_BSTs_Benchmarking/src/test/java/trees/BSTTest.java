package trees;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link BST} (Binary Search Tree).
 * <p>
 * Each test creates a fresh {@link BST} instance via {@link #setup()} annotated
 * with {@code @BeforeEach}. Tests cover all core operations:
 * insert, delete, contains, size, height, and inOrder traversal — including
 * edge cases such as empty trees, duplicates, and large bulk operations.
 * </p>
 */
public class BSTTest {

    BST bst;

    @BeforeEach
    void setup() {
        bst = new BST();
    }

    /** Verifies that inserting a single element increments size to 1. */
    @Test
    void singleInsert() {
        bst.insert(12);
        assertEquals(1, bst.size());
    }

    /**
     * Verifies that inserting a duplicate returns {@code false} and does not change
     * size.
     */
    @Test
    void duplicateInsert() {
        bst.insert(5);
        boolean duplicateCheck = bst.insert(5);
        assertFalse(duplicateCheck);
        assertEquals(1, bst.size());
    }

    @Test
    void multipleInserts() {
        bst.insert(20);
        bst.insert(15);
        bst.insert(25);
        bst.insert(12);
        bst.insert(1);
        bst.insert(36);
        assertEquals(6, bst.size());
    }

    @Test
    void checkAfterInsert() {
        bst.insert(10);
        boolean insertCheck = bst.contains(10);
        assertTrue(insertCheck);
    }

    @Test
    void checkAfterDeletion() {
        bst.insert(10);
        bst.delete(10);
        boolean containCheck = bst.contains(10);
        assertFalse(containCheck);
    }

    @Test
    void containsEmpty() {
        boolean checkEmpty = bst.contains(10);
        assertFalse(checkEmpty);
    }

    @Test
    void checkNoExist() {
        bst.insert(10);
        boolean checkNoExist = bst.contains(15);
        assertFalse(checkNoExist);
    }

    @Test
    void deleteExisting() {
        bst.insert(10);
        bst.insert(15);
        boolean deleteCheck = bst.delete(10);
        assertTrue(deleteCheck);
        assertEquals(1, bst.size());
    }

    @Test
    void deleteNonExisting() {
        bst.insert(15);
        boolean deleteCheck = bst.delete(10);
        assertFalse(deleteCheck);
    }

    @Test
    void deleteEmpty() {
        boolean deleteCheck = bst.delete(10);
        assertFalse(deleteCheck);
    }

    @Test
    void deleteOnlyElement() {
        bst.insert(10);
        bst.delete(10);
        boolean containsCheck = bst.contains(10);
        assertFalse(containsCheck);
        assertEquals(0, bst.size());
    }

    @Test
    void deleteWIthTwoChildren() {
        bst.insert(10);
        bst.insert(15);
        bst.insert(5);
        bst.delete(10);
        assertEquals(2, bst.size());

    }

    @Test
    void emptyHeight() {
        assertEquals(0, bst.height());
    }

    @Test
    void singleElementHeight() {
        bst.insert(10);
        assertEquals(1, bst.height());
    }

    @Test
    void multipleElementHeight() {
        bst.insert(15);
        bst.insert(5);
        bst.insert(35);
        bst.insert(2);
        bst.insert(0);
        assertEquals(4, bst.height());
    }

    @Test
    void heightAfterDeletion() {
        bst.insert(15);
        bst.insert(5);
        bst.insert(35);
        bst.insert(2);
        bst.insert(0);
        bst.delete(0);
        bst.delete(2);
        assertEquals(2, bst.height());
    }

    @Test
    void emptySize() {
        assertEquals(0, bst.size());
    }

    @Test
    void multipleInsertsSize() {
        bst.insert(15);
        bst.insert(5);
        bst.insert(35);
        bst.insert(2);
        bst.insert(0);
        bst.insert(23);
        bst.insert(17);
        bst.insert(41);
        bst.insert(12);
        bst.insert(20);
        assertEquals(10, bst.size());
    }

    @Test
    void multipleInsertsAndDeletesSize() {
        bst.insert(15);
        bst.insert(5);
        bst.insert(35);
        bst.delete(5);
        bst.insert(2);
        bst.insert(0);
        bst.insert(23);
        bst.delete(15);
        bst.delete(2);
        bst.insert(17);
        bst.insert(41);
        bst.insert(12);
        bst.delete(17);
        bst.delete(41);
        bst.insert(20);
        bst.delete(20);
        assertEquals(4, bst.size());
    }

    @Test
    void insertAndDeleteAll() {
        bst.insert(15);
        bst.insert(5);
        bst.delete(5);
        bst.insert(2);
        bst.delete(15);
        bst.delete(2);
        bst.insert(17);
        bst.insert(41);
        bst.delete(17);
        bst.delete(41);
        bst.insert(20);
        bst.delete(20);
        assertEquals(0, bst.size());
    }

    @Test
    void inOrderSorted() {
        bst.insert(15);
        bst.insert(5);
        bst.insert(35);
        bst.insert(2);
        bst.insert(0);
        bst.insert(23);
        bst.insert(17);
        bst.insert(41);
        bst.insert(12);
        bst.insert(20);
        int[] arr = { 0, 2, 5, 12, 15, 17, 20, 23, 35, 41 };
        assertArrayEquals(arr, bst.inOrder());
    }

    @Test
    void inorderEmpty() {
        int[] arr = {};
        assertArrayEquals(arr, bst.inOrder());
    }

    @Test
    void inOrderSortedAfterDeletion() {
        bst.insert(15);
        bst.insert(5);
        bst.insert(35);
        bst.insert(2);
        bst.insert(0);
        bst.insert(23);
        bst.insert(17);
        bst.insert(41);
        bst.insert(12);
        bst.delete(15);
        bst.delete(41);
        bst.insert(20);
        bst.delete(20);
        int[] arr = { 0, 2, 5, 12, 17, 23, 35 };
        assertArrayEquals(arr, bst.inOrder());
    }

    @Test
    void heightOnSortedInput() {
        bst.insert(1);
        bst.insert(2);
        bst.insert(3);
        bst.insert(4);
        bst.insert(5);
        bst.insert(6);
        bst.insert(7);
        bst.insert(8);
        bst.insert(9);
        bst.insert(10);
        assertEquals(10, bst.height());
    }

}
