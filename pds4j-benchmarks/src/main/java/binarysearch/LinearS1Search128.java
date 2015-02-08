package binarysearch;

import org.junit.Test;

public class LinearS1Search128 extends LinearS1SearchN {

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
