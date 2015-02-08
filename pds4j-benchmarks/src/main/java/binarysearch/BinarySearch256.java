package binarysearch;

import org.junit.Test;

public class BinarySearch256 extends BinarySearchN {

    @Override
    protected int getChunkSize() {
        return 256;
    }

    @Test
    public void test_test() {
        generateData();
//        prepareKeys();
        searchKey();
    }    
}
