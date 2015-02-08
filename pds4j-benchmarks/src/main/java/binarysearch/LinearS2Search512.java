package binarysearch;

import org.junit.Test;

public class LinearS2Search512 extends LinearS2SearchN {

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
