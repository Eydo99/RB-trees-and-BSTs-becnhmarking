package trees.benchmark;

import java.util.ArrayList;
import java.util.List;

public class SortComparison {

    String name;

    public SortComparison() {
        this.name = "MergeSort";
    }


    public void sort(int[] array) {
        mergeSort(array);
    }

    private void mergeSort(int[] array) {
        int n = array.length;
        if (n < 2)
            return;

        int mid = n / 2;
        int[] left = new int[mid];
        int[] right = new int[n - mid];
        int l = 0, r = 0;
        for (; l < n; l++) {
            if (l < mid)
                left[l] = array[l];
            else {
                right[r] = array[l];
                r++;
            }
        }

        mergeSort(left);
        mergeSort(right);
        merge(array, left, right);
    }

    private void merge(int[] array, int[] left, int[] right) {
        int leftSize = left.length;
        int rightSize = right.length;
        int l = 0, r = 0, i = 0;

        while (l < leftSize && r < rightSize) {
            if (left[l] < right[r]) {
                array[i] = left[l];
                l++;
                i++;

            } else {
                array[i] = right[r];
                r++;
                i++;

            }

        }
        while (l < leftSize) {
            array[i] = left[l];
            l++;
            i++;


        }
        while (rightSize > r) {
            array[i] = right[r];
            r++;
            i++;

        }
    }
}

