package binarysearch;

import org.junit.Test;

public class BinarySearch512 extends BinarySearchN {

    @Override
    protected int getChunkSize() {
        return 512;
    }

    @Test
    public void test_test() {
        generateData();
//        prepareKeys();
        searchKey();
    }    
}
