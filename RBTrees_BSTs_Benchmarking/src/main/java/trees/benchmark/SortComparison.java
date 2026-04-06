package trees.benchmark;

import java.util.ArrayList;
import java.util.List;

public class SortComparison {
     static class SortStep
     {
        public int[] array;
        public int activeIndex;
        public SortStep(int[] array, int activeIndex) {
            this.array = array.clone();
            this.activeIndex = activeIndex;
        }
    }

    private long comparisons=0;
    private  long interchanges=0;
    private List<SortStep> steps=new ArrayList<>();
    String name;

    public SortComparison() {
        this.name = "MergeSort";
    }

    public void reset(){
        steps.clear();
        comparisons=0;
        interchanges=0;
    }

    public void sort(int[] array) {
        reset();
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
                interchanges++;
            } else {
                array[i] = right[r];
                r++;
                i++;
                interchanges++;
            }

            comparisons++;
        }
        while (l < leftSize) {
            array[i] = left[l];
            l++;
            i++;
            interchanges++;

        }

        while (rightSize > r) {
            array[i] = right[r];
            r++;
            i++;
            interchanges++;
        }
    }
}

