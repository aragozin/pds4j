package binarysearch;

import org.junit.Test;

public abstract class LinearS2SearchN extends AbstractSearchN {

    protected int lookup(int[] chunk, int number) {
        int n = 0;
        while(n < chunk.length) {
            if (chunk[n] == number) {
                return n;
            }
            else if (chunk[n] > number) {
                if (n > 0) {
                    if (chunk[n - 1] == number) {
                        return n - 1;
                    }
                    else if (chunk[n - 1] > number) {
                        return ~(n - 1);                        
                    }
                }
                return ~n;
            }
            n += 2;
        }
        if (n == chunk.length) {
            if (chunk[n - 1] == number) {
                return n - 1;
            }
            else if (chunk[n - 1] > number) {
                return ~(n - 1);                        
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
