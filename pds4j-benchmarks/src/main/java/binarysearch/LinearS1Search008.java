package binarysearch;

import org.junit.Test;

public class LinearS1Search008 extends LinearS1SearchN {

    @Override
    protected int getChunkSize() {
        return 8;
    }

    @Test
    public void test_test() {
        generateData();
//        prepareKeys();
        searchKey();
    }    
}
