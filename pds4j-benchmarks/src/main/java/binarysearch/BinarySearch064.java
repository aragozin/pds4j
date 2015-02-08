package binarysearch;

import org.junit.Test;

public class BinarySearch064 extends BinarySearchN {

    @Override
    protected int getChunkSize() {
        return 64;
    }

    @Test
    public void test_test() {
        generateData();
//        prepareKeys();
        searchKey();
    }    
}
