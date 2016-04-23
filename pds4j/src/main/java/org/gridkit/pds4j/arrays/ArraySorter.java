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

package org.gridkit.pds4j.arrays;

public abstract class ArraySorter<K> {

	/**
	 * Return object associated with entry. Object could be reused between call to this
	 * method, but not shared with {@link #keyAt_B(int)}
	 * 
	 * @return comparable object
	 */
	protected K keyAt_A(int entry) {
		throw new UnsupportedOperationException();
	}

	/**
	 * Return object associated with entry. Object could be reused between call to this
	 * method, but not shared with {@link #keyAt_A(int)}
	 * 
	 * @return comparable object
	 */
	protected K keyAt_B(int entry) {
		throw new UnsupportedOperationException();
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	protected int keysCompare(K a, K b) {
		return ((Comparable)a).compareTo(b);
	}

	public int[] order(int from, int to) {
		if (from > to) {
			throw new IllegalArgumentException("Negative range");
		}
		int[] refs = new int[to - from];
		for (int i = 0; i < refs.length; ++i) {
			refs[i] = from + i;
		}

		sortRefs(refs);
		return refs;
	}

	protected void sortRefs(int[] rows) {
		sort_internal(rows, 0, rows.length);
	}

	/**
	 * Based on Apache Harmony code.
	 */
	protected void sort_internal(int[] rows, int start, int end) {
        int length = end - start;
        if (length < 7) {
            for (int i = start + 1; i < end; i++) {
                for (int j = i; j > start && cmp(rows[j - 1], rows[j]) > 0; j--) {
                    int temp = rows[j];
                    rows[j] = rows[j - 1];
                    rows[j - 1] = temp;
                }
            }
            return;
        }
        int middle = (start + end) / 2;
        if (length > 7) {
            int bottom = start;
            int top = end - 1;
            if (length > 40) {
                length /= 8;
                bottom = med(rows, bottom, bottom + length, bottom + (2 * length));
                middle = med(rows, middle - length, middle, middle + length);
                top = med(rows, top - (2 * length), top - length, top);
            }
            middle = med(rows, bottom, middle, top);
        }
        
        int partionValue = rows[middle];
        int a, b, c, d;
        a = b = start;
        c = d = end - 1;
        while (true) {
        	int cmp = 0;
            while (b <= c && (cmp = cmp(rows[b], partionValue)) <= 0) {
                if (cmp == 0) {
                	swap(rows, a++, b);
                }
                b++;
            }
            while (c >= b && (cmp = cmp(rows[c], partionValue)) >= 0) {
                if (cmp == 0) {
                	swap(rows, c, d--);
                }
                c--;
            }
            if (b > c) {
                break;
            }
            swap(rows, c--, b++);
        }
        length = a - start < b - a ? a - start : b - a;
        int l = start;
        int h = b - length;
        while (length-- > 0) {
        	swap(rows, l++, h++);	
        }
        length = d - c < end - 1 - d ? d - c : end - 1 - d;
        l = b;
        h = end - length;
        while (length-- > 0) {
        	swap(rows, l++, h++);
        }
        if ((length = b - a) > 0) {
        	sort_internal(rows, start, start + length);
        }
        if ((length = d - c) > 0) {
        	sort_internal(rows, end - length, end);
        }
    }

	protected int cmp(int[] rows, int a, int b) {
		return cmp(rows[a], rows[b]);
	}

	protected int cmp(int a, int b) {
		return keysCompare(keyAt_A(a), keyAt_B(b));
	}

    protected int med(int rows[], int a, int b, int c) {
		return (cmp(rows, a, b) < 0 ?
			(cmp(rows, b, c) < 0 ? b : cmp(rows, a, c) < 0 ? c : a) :
			(cmp(rows, b, c) > 0 ? b : cmp(rows, a, c) > 0 ? c : a));
    }
	
	private static void swap(int x[], int a, int b) {
		int t = x[a];
		x[a] = x[b];
		x[b] = t;
	}
}
