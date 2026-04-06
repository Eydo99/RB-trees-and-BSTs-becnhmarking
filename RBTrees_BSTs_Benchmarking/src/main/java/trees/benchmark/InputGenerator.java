package trees.benchmark;

import java.util.Random;

public class InputGenerator {

    private static final int n=100000;
    private static final int seed = 40;

    public int[] generateRandom()
    {
        int[] randomArray=new int[n];
        Random random=new Random(seed);
        for(int i =0;i <n ;i++)
        {
            randomArray[i]=random.nextInt(0,10*n+1);
        }
        return randomArray;
    }


    public int[] generateNearlySorted(int swapPercent)
    {
        int[] nearlySortedArr=new int[n];
        Random random=new Random(seed);
        for(int i=0;i<n;i++)
        {
            nearlySortedArr[i]=i;
        }
        double swapsNo=(swapPercent/100.0)*n;

        for(int i=0;i<swapsNo;i++)
        {
            int firstIndex=random.nextInt(0,n);
            int secondIndex=random.nextInt(0,n);
            swap(nearlySortedArr,firstIndex,secondIndex);
        }
        return  nearlySortedArr;
    }

    private void swap(int[] arr,int i,int j)
    {
        int temp=arr[i];
        arr[i]=arr[j];
        arr[j]=temp;
    }

}
