package binarysearch;

import org.junit.Test;

public class BinarySearch032 extends BinarySearchN {

    @Override
    protected int getChunkSize() {
        return 32;
    }

    @Test
    public void test_test() {
        generateData();
//        prepareKeys();
        searchKey();
    }    
}
