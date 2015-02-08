package binarysearch;

import org.junit.Test;

public class BinarySearch128 extends BinarySearchN {

    @Override
    protected int getChunkSize() {
        return 128;
    }

    @Test
    public void test_test() {
        generateData();
//        prepareKeys();
        searchKey();
    }    
}
