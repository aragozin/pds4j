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

import java.util.BitSet;

public abstract class ArrayShuffler {
	
	public static void reorder(int[] array, int[] order) {
		new IntArrayShuffler(array).reorder(order);
	}

	public static void reorder(long[] array, int[] order) {
		new LongArrayShuffler(array).reorder(order);
	}
	
	public static void reorder(float[] array, int[] order) {
		new FloatArrayShuffler(array).reorder(order);
	}
	
	public static void reorder(double[] array, int[] order) {
		new DoubleArrayShuffler(array).reorder(order);
	}
	
	protected abstract void swap(int a, int b);
	
	protected abstract int length();
	
	public void reorder(int[] positions) {
		int lim = length();
		BitSet bits = new BitSet(lim);		
		
		while(true) {
			int n = bits.nextClearBit(0);
			if (n >= length()) {
				break;
			}
			
			int mv = n;
			while(true) {
				int tv = positions[mv];
				if (tv < 0 || tv >= lim) {
					throw new IllegalArgumentException("Inconsitent order");
				}
				bits.set(mv);
				if (tv == n) {
					break;
				}
				swap(mv, tv);
				mv = tv;
				if (bits.get(mv)) {
					throw new IllegalArgumentException("Inconsitent order");
				}
			}			
		}		
	}
	
	private static final class IntArrayShuffler extends ArrayShuffler {
		
		private final int[] array;

		public IntArrayShuffler(int[] array) {
			this.array = array;
		}

		@Override
		protected void swap(int a, int b) {
			int t = array[a];
			array[a] = array[b];
			array[b] = t;			
		}

		@Override
		protected int length() {
			return array.length;
		}
	}

	private static final class LongArrayShuffler extends ArrayShuffler {
		
		private final long[] array;
		
		public LongArrayShuffler(long[] array) {
			this.array = array;
		}
		
		@Override
		protected void swap(int a, int b) {
			long t = array[a];
			array[a] = array[b];
			array[b] = t;			
		}
		
		@Override
		protected int length() {
			return array.length;
		}
	}

	private static final class FloatArrayShuffler extends ArrayShuffler {
		
		private final float[] array;
		
		public FloatArrayShuffler(float[] array) {
			this.array = array;
		}
		
		@Override
		protected void swap(int a, int b) {
			float t = array[a];
			array[a] = array[b];
			array[b] = t;			
		}
		
		@Override
		protected int length() {
			return array.length;
		}
	}
	
	private static final class DoubleArrayShuffler extends ArrayShuffler {
		
		private final double[] array;
		
		public DoubleArrayShuffler(double[] array) {
			this.array = array;
		}
		
		@Override
		protected void swap(int a, int b) {
			double t = array[a];
			array[a] = array[b];
			array[b] = t;			
		}
		
		@Override
		protected int length() {
			return array.length;
		}
	}	
}
