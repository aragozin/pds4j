/**
 * Copyright 2016 Alexey Ragozin
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.gridkit.pds4j.hash;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import org.junit.Assert;
import org.junit.Test;

public class SimpleHashLookupTest {

    @Test
    public void put_get() {
        
        HashStore store = new HashStore();
        
        store.add(100);
        
        verify(store, 100);        
    }

    @Test
    public void put_get_negative_hash() {
        
        HashStore store = new HashStore();
        
        store.add(-10, 10);
        
        verify(store, -10, 10);        
    }

    @Test
    public void put_get_20() {
        
        HashStore store = new HashStore();
        
        store.add(1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20);
        
        verify(store, 1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20);        
    }

    @Test
    public void test_clear() {
        
        HashStore store = new HashStore();
        
        int[] vals = serie(2000, -1000, 3);
        store.add(vals);
        
        verify(store, vals);
        
        store.clear();
        
        verify(store);

        store.add(vals);

        verify(store, vals);        
    }

    @Test
    public void test_reset() {
        
        HashStore store = new HashStore();
        
        int[] vals = serie(2000, -1000, 3);
        store.add(vals);
        
        verify(store, vals);
        
        store.reset();
        
        verify(store);
        
        store.add(vals);
        
        verify(store, vals);        
    }
    
    @Test
    public void test_hash_collision() {

        HashStore store = new HashStore();

        store.add(1001, 1002, 2001, 2002, 3001, 3003, 4001, 5001, 6001, 7001, 8002);
        
        verify(store, 1001, 1002, 2001, 2002, 3001, 3003, 4001, 5001, 6001, 7001, 8002);        
    }

    @Test
    public void test_rehash() {

        HashStore store = new HashStore();

        int[] vals = serie(5000, -1000, 1);
        
        store.add(vals);
        
        verify(store, vals);        
    }

    @Test
    public void test_duplicates() {

        HashStore store = new HashStore();
        
        store.add(10001, 20001, 30001, 2, 3, 4);
        
        assertEquals(store.getAll(1), 10001, 20001, 30001);
        
        store.update(1, 20002);

        assertEquals(store.getAll(1), 10001, 30001);
        
        store.add(serie(5000, -2500, 1));

        assertEquals(store.getAll(1), 10001, 30001, 1);
    }
    
    @Test
    public void random_ops() {
        Random rnd = new Random(1);
        HashStore store = new HashStore();
        Set<Integer> etalon = new HashSet<Integer>();
        
        for(int i = 0; i != 10000; ++i) {
            float f = rnd.nextFloat();
            if (f > 0.5) {
                int x = rnd.nextInt(20000);
                if (etalon.contains(x)) {
                    continue;
                }
                store.add(x);
                etalon.add(x);
            }
            else if (f > 0.2) {
                int x = rnd.nextInt(20000);

                if (etalon.contains(x)) {
                    continue;
                }
                
                int n = rnd.nextInt(store.values.length);
                if (store.values[n] == Integer.MIN_VALUE) {
                    continue;
                }
                
                etalon.remove(store.values[n]);
                store.update(n, x);
                etalon.add(x);                
            }
            else {
                int n = rnd.nextInt(store.values.length);
                if (store.values[n] == Integer.MIN_VALUE) {
                    continue;
                }

                etalon.remove(store.values[n]);
                store.erase(n);
            }
            if (i % 100 == 1) {
                try {
                    verify(store, toArray(etalon));
                }
                catch(AssertionError e) {
                    AssertionError ee = new AssertionError(e.getMessage() + " step " + i);
                    ee.setStackTrace(e.getStackTrace());
                    throw ee;
                }
            }
        }
        verify(store, toArray(etalon));
        Assert.assertEquals(etalon.size(), store.size);
    }
    
    public int[] toArray(Set<Integer> set) {
        int[] array = new int[set.size()];
        int n = 0;
        for(int x: set) {
            array[n++] = x;
        }
        return array;
    }
    
    public int[] serie(int count, int start, int step) {
        int[] array = new int[count];
        for(int i = 0; i != array.length; ++i) {
            array[i] = start + i * step;
        }
        return array;
    }
    
    public void assertEquals(int[] actual, int... expected) {
        Assert.assertEquals(Arrays.toString(expected), Arrays.toString(actual));
    }
    
    public void verify(HashStore store, int... values) {
        for(int i: values) {
            Assert.assertTrue("Value [" +i + "] should be present", store.seek(i) >= 0);
        }
        int[] actual = store.toArray();
        int[] expected = Arrays.copyOf(values, values.length);
        Arrays.sort(actual);
        Arrays.sort(expected);
        
        Assert.assertEquals(Arrays.toString(expected), Arrays.toString(actual));
    }
    
    public static class HashStore extends SimpleHashLookup<Integer> {
        
        private int[] values = new int[0];

        @Override
        protected Integer keyAt(int row) {
            return Integer.valueOf(values[row]);
        }
        
        protected int keyHash(Integer key) {
            return key % 1000;
        };        
        
        public void add(int... val) {
            int n = values.length;
            values = Arrays.copyOf(values, values.length + val.length);
            for(int x: val) {
                values[n] = x;
                put(n);
                ++n;
            }
        }

        public void update(int entry, int val) {
            values[entry] = val;
            put(entry);
        }

        public void erase(int entry) {
            removeEntry(entry);
            values[entry] = Integer.MIN_VALUE;
        }
        
        @Override
        protected boolean keysEqual(Integer a, Integer b) {
            return a % 10000 == b % 10000;
        }
        
        public int[] getAll(int key) {
            int[] array = new int[16];
            int n = 0;
            int p = seek(key);
            while(p >= 0) {
                if (n >= array.length) {
                    array = Arrays.copyOf(array, 2 * array.length);
                }
                array[n++] = values[p];
                p = seekNextDuplicate(key, p);
            }
            return Arrays.copyOf(array, n);
        }
        
        public int[] toArray() {
            int[] array = new int[size];
            int n = 0;
            for(int x: values) {
                if (x != Integer.MIN_VALUE) {
                    array[n++] = x;
                }
            }
            
            return Arrays.copyOf(array, n);
        }

        @Override
        public void clear() {
            values = new int[0];
            super.clear();
        }

        @Override
        public void reset() {
            values = new int[0];
            super.reset();
        }
    }
}
