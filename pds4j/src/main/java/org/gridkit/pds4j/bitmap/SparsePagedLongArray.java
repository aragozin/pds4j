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


import java.util.Arrays;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * Very much like {@link PagedLongArray}, but
 * instead of array tree is used to keep set of 
 * allocated pages, which allow it to be used as
 * backing bit storage for whole 64 bit address ranges.
 * <br/>
 * Hash would be more efficient for lookup, but
 * bit seek operation requires traversing pages in memory order. 
 * 
 * @author Alexey Ragozin (alexey.ragozin@gmail.com)
 *
 */
class SparsePagedLongArray implements LongArray {

	private final static int PAGE_BITS = 10;
	private final static int PAGE_MASK = ~(-1 << PAGE_BITS);
	private final static int PAGE_SIZE = 1 << PAGE_BITS;

	public final static long NULL_VALUE = 0;

	// originally this class was written to keep sets of memory address from JVM heap dump
	// some OS (Solaris in particular) allocates high memory ranges in 64 bit space
	// so I have to use tree instead of array for tracking pages of bits. 
	protected SortedMap<Long, long[]> pages = new TreeMap<Long, long[]>();
	protected long lastIndex = -1;

    public long get(long n) {
        if (n > lastIndex) {
            return NULL_VALUE;
        }
		long bi = n >>> PAGE_BITS;
		if (bi < 0) {
		    throw new ArrayIndexOutOfBoundsException("" + bi);
		}
		long[] page = getPageForRead(bi);
		if (page == null) {
			return NULL_VALUE;
		}
		return page[(int) (n & PAGE_MASK)];
	}

    public long seekNext(long start) {
        long startPage = start >> PAGE_BITS;
        SortedMap<Long, long[]> pages = this.pages.tailMap(startPage);
        for(Map.Entry<Long, long[]> entry: pages.entrySet()) {
            long pi = entry.getKey();
            long[] page = entry.getValue();
            long ps = ((long)pi) << PAGE_BITS;
            long pe = ps + PAGE_SIZE;
            for(long i = Math.max(ps, start); i != pe; ++i) {
                if (page[(int) (i & PAGE_MASK)] != 0) {
                    return i;
                }
            }
        }
        return -1;
    }

    public void set(long n, long value) {
		lastIndex = Math.max(lastIndex, n);
		long bi = n >>> PAGE_BITS;
		long[] page = value == NULL_VALUE ? getPageForRead(bi) : getPageForWrite(bi);
		if (page == null && value == NULL_VALUE) {
		    if (value == NULL_VALUE) {
		        return;
		    }
		}
		page[(int) (n & PAGE_MASK)] = value;
	}

    protected long[] getPageForRead(long bi) {
        long[] page = pages.get(bi);
        return page;
    }

    protected long[] getPageForWrite(long bi) {
        long[] page = pages.get(bi);
        if (page == null) {
            page = new long[PAGE_SIZE];
            pages.put(bi, page);
            Arrays.fill(page, NULL_VALUE);
        }        
        return page;
    }
}
