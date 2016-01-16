/**
 * Copyright 2014-2016 Alexey Ragozin
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
package org.gridkit.pds4j.bitmap;

/**
 * Simple bit map using paged long array for storage.
 * Untouched pages are not allocated, so it is reasonably efficient
 * for bitmaps with large gaps.
 *
 * @author Alexey Ragozin (alexey.ragozin@gmail.com)
 */
public class PagedBitMap extends BaseBitMap implements BitMap, LongArrayBackedBitMap {

    private final LongArray array;
    
    public PagedBitMap() {
        this(new PagedLongArray());
    }

    public PagedBitMap(boolean spare) {
        if (spare) {
            this.array = new SparsePagedLongArray();            
        }
        else {
            this.array = new PagedLongArray();
        }
    }
    
    protected PagedBitMap(LongArray longArray) {
        this.array = longArray;
    }

    @Override
    public LongArray backingArray() {
        return array;
    }
    
    @Override
    public boolean get(long index) {
        if (index < 0) {
            throw new IllegalArgumentException("Negative bit index: " + index);
        }        
        long lindex = index / 64;
        long bit = 1l << (index % 64);
        return (0 != (bit & array.get(lindex))); // Long.toBinaryString(array.get(lindex))
    }

    @Override
    public long seekOne(long start) {
        if (start < 0) {
            throw new IllegalArgumentException("Negative bit index: " + start);
        }
        long n = start;
        while(true) {
            long lindex = n / 64;
            long bit = 1l << (n % 64);

            long word = array.get(lindex);
            if (word == 0) {
                long nn = array.seekNext(lindex);
                if (nn < 0) {
                    return -1;
                }
                n = 64 * nn;
                continue;
            }
            while(bit != 0) {
                if (0 != (bit & word)) {
                    return n;
                }
                ++n;
                bit <<= 1;
            }
        }
    }

    @Override
    public void set(long index, boolean value) {
        if (index < 0) {
            throw new IllegalArgumentException("Negative bit index: " + index);
        }
        long lindex = index / 64;
        long bit = 1l << (index % 64);
        if (value) {
            array.set(lindex, bit | array.get(lindex));
        }
        else {
            array.set(lindex, (~bit) & array.get(lindex));
        }
    }

    @Override
    public boolean getAndSet(long index, boolean value) {
        if (index < 0) {
            throw new IllegalArgumentException("Negative bit index: " + index);
        }
        long lindex = index / 64;
        long bit = 1l << (index % 64);
        long ov = array.get(lindex);
        if (value) {
        array.set(lindex, bit | ov);
        }
        else {
            array.set(lindex, (~bit) & ov);
        }
        return 0 != (bit & ov);
    }

    /**
     * Bitwise <br/>
     * <code>this = this | that</code>
     */
    @Override
    public void add(BitMap that) {
        if (that instanceof LongArrayBackedBitMap) {
            LongArray ta = ((LongArrayBackedBitMap) that).backingArray();
            long n = 0;
            while(true) {
                n = ta.seekNext(n);
                if (n < 0) {
                    break;
                }
                long v = array.get(n) | ta.get(n);
                array.set(n, v);
                ++n;
            }
        }
        else {
            bitwiseAdd(that);
        }
    }

    /**
     * Bitwise <br/>
     * <code>overflow = this & that</code>
     * <br/>
     * <code>this = this | that</code>
     */
    @Override
    public void addWithOverflow(BitMap that, BitMap overflow) {
        if (that instanceof LongArrayBackedBitMap && overflow instanceof LongArrayBackedBitMap) {
            LongArray ta = ((LongArrayBackedBitMap)that).backingArray();
            LongArray of = ((LongArrayBackedBitMap)overflow).backingArray();
            long n = 0;
            while(true) {
                n = ta.seekNext(n);
                if (n < 0) {
                    break;
                }
                long o = array.get(n) & ta.get(n);
                long v = array.get(n) | ta.get(n);
                array.set(n, v);
                if (o != 0) {
                    o |= of.get(n);
                    of.set(n, o);
                }
                ++n;
            }
        }
        else {
            bitwiseAddWithOverflow(that, overflow);
        }
    }

    /**
     * Bitwise <br/>
     * <code>this = this & (~that)</code>
     */
    @Override
    public void sub(BitMap that) {
        if (that instanceof LongArrayBackedBitMap) {
            LongArray ta = ((LongArrayBackedBitMap) that).backingArray();
            long n = 0;
            while(true) {
                n = ta.seekNext(n);
                if (n < 0) {
                    break;
                }
                long v = array.get(n) & ~ta.get(n);
                array.set(n, v);
                ++n;
            }
        }
        else {
            bitwiseSub(that);
        }
    }

    /**
     * Bitwise <br/>
     * <code>this = this & that</code>
     */
    @Override
    public void mult(BitMap that) {
        if (that instanceof LongArrayBackedBitMap) {
            LongArray ta = ((LongArrayBackedBitMap) that).backingArray();
            long n = 0;
            while(true) {
                n = ta.seekNext(n);
                if (n < 0) {
                    break;
                }
                long v = array.get(n) & ta.get(n);
                array.set(n, v);
                ++n;
            }
        }
        else {
            bitwiseMult(that);
        }
    }

    @Override
    @SuppressWarnings("unused")
    public long countOnes() {
        long n = 0;
        for(Long l: ones()) {
            ++n;
        }
        return n;
    }    
}
