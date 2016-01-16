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

import java.util.BitSet;

class BitSetAdapter extends BaseBitMap implements BitMap {

    private final BitSet bitSet;
    
    public BitSetAdapter(BitSet bitSet) {
        this.bitSet = bitSet;
    }

    @Override
    public long countOnes() {
        return bitSet.cardinality();
    }

    @Override
    public void mult(BitMap that) {
        bitwiseMult(that);
    }

    @Override
    public void sub(BitMap that) {
        bitwiseSub(that);
    }

    @Override
    public void addWithOverflow(BitMap that, BitMap overflow) {
        bitwiseAddWithOverflow(that, overflow);
    }

    @Override
    public void add(BitMap that) {
        bitwiseAdd(that);        
    }

    @Override
    public boolean getAndSet(long index, boolean value) {
        checkIndex(index);
        boolean val = bitSet.get((int)index);
        return val;
    }

    protected void checkIndex(long index) {
        if (index < 0 || index > Integer.MAX_VALUE) {
            throw new IllegalArgumentException("Index out of range for java.util.BitSet " + index);
        }
    }

    @Override
    public void set(long index, boolean value) {
        checkIndex(index);
        bitSet.set((int) index, value);
    }

    @Override
    public long seekOne(long start) {
        checkIndex(start);
        return bitSet.nextSetBit((int) start);
    }

    @Override
    public boolean get(long index) {
        checkIndex(index);
        return bitSet.get((int) index);
    }
}
