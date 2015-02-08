package binarysearch;

import org.junit.Test;

public class LinearS1Search032 extends LinearS1SearchN {

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
