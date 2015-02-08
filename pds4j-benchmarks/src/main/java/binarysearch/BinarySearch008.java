package binarysearch;

import org.junit.Test;

public class BinarySearch008 extends BinarySearchN {

    @Override
    protected int getChunkSize() {
        return 8;
    }

    @Test
    public void test_test() {
        generateData();
//        prepareKeys();
        searchKey();
    }    
}
