package binarysearch;

import org.junit.Test;

public class LinearS2Search256 extends LinearS2SearchN {

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
