package binarysearch;

import org.junit.Test;

public class LinearS1Search256 extends LinearS1SearchN {

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
