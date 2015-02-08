package binarysearch;

import org.junit.Test;

public class LinearS1Search512 extends LinearS1SearchN {

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
