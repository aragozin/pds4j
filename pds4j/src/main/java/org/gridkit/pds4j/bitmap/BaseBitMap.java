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

import java.util.Iterator;
import java.util.NoSuchElementException;

abstract class BaseBitMap implements BitMap {

    protected void bitwiseAdd(BitMap that) {
        long nthat = that.seekOne(0);
        while(nthat != -1) {
            set(nthat, true);
            nthat = that.seekOne(nthat + 1);
        }
    }

    protected void bitwiseAddWithOverflow(BitMap that, BitMap overflow) {
        long nthat = that.seekOne(0);
        while(nthat != -1) {
            if (get(nthat)) {
                overflow.set(nthat, true);
            }
            set(nthat, true);
            nthat = that.seekOne(nthat + 1);
        }
    }

    protected void bitwiseSub(BitMap that) {
        long nthat = that.seekOne(0);
        while(nthat != -1) {
            set(nthat, false);
            nthat = that.seekOne(nthat + 1);
        }
    }

    protected void bitwiseMult(BitMap that) {
        long nthis = that.seekOne(0);
        long nthat = that.seekOne(0);
        while(nthis >= 0) {
            if (nthis != nthat) {
                set(nthis, false);
            }
            else {
                nthis = seekOne(nthis + 1);
                nthat = that.seekOne(nthat + 1);
                continue;
            }
            
            if (nthat >= 0 && nthis < nthat) {
                nthis = seekOne(nthis + 1);
            }
            else {
                nthat = that.seekOne(nthat + 1);
            }
        }
    }

    @Override
    public Iterable<Long> ones() {
        return new Iterable<Long>() {
            @Override
            public Iterator<Long> iterator() {
                return new SeekerIterator(BaseBitMap.this);
            }
        };
    }

    protected static class SeekerIterator implements Iterator<Long> {
    
        private BitMap bitmap;
        private long next;
    
        public SeekerIterator(BitMap bitmap) {
            this.bitmap = bitmap;
            this.next = bitmap.seekOne(0);
        }
    
        @Override
        public boolean hasNext() {
            return next != -1;
        }
    
        @Override
        public Long next() {
            if (next == -1) {
                throw new NoSuchElementException();
            }
            long n = next;
            next = bitmap.seekOne(next + 1);
            return n;
        }
    
        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }
}
