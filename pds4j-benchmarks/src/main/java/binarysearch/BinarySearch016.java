package binarysearch;

import org.junit.Test;

public class BinarySearch016 extends BinarySearchN {

    @Override
    protected int getChunkSize() {
        return 16;
    }

    @Test
    public void test_test() {
        generateData();
//        prepareKeys();
        searchKey();
    }    
}
