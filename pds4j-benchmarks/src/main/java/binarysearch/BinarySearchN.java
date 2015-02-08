package binarysearch;

import java.util.Arrays;

import org.junit.Test;

public abstract class BinarySearchN extends AbstractSearchN {

    protected int lookup(int[] chunk, int number) {
        return Arrays.binarySearch(chunk, number);
    }
    
    @Test
    public void test_test() {
        generateData();
//        prepareKeys();
        searchKey();
    }    
}
