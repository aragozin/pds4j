package binarysearch;

import org.junit.Test;

public class LinearS1Search016 extends LinearS1SearchN {

    @Override
    protected int getChunkSize() {
        return 16;
    }

    @Test
    public void test_test() {
        generateData();
//        prepareKeys();
        searchKey();
    }    
}
