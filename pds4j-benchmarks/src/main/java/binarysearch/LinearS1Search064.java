package binarysearch;

import org.junit.Test;

public class LinearS1Search064 extends LinearS1SearchN {

    @Override
    protected int getChunkSize() {
        return 64;
    }

    @Test
    public void test_test() {
        generateData();
//        prepareKeys();
        searchKey();
    }    
}
