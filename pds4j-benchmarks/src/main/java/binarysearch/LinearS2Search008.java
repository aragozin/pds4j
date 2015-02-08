package binarysearch;

import org.junit.Test;

public class LinearS2Search008 extends LinearS2SearchN {

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
