package binarysearch;

import org.junit.Test;

public abstract class LinearS1SearchN extends AbstractSearchN {

    protected int lookup(int[] chunk, int number) {
        for(int i = 0; i != chunk.length; ++i) {
            if (chunk[i] == number) {
                return i;
            }
            else if (chunk[i] > number) {
                return ~(i);
            }
        }
        return ~chunk.length;
    }
    
    @Test
    public void test_test() {
        generateData();
//        prepareKeys();
        searchKey();
    }    
}
