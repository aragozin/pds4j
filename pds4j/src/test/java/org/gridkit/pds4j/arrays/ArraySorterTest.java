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

import java.util.Arrays;
import java.util.Random;

import org.junit.Assert;
import org.junit.Test;

public class ArraySorterTest {


	@Test
	public void simple_int_cases() {
		verify_int_sorting(); // empty
		verify_int_sorting(0);
		verify_int_sorting(0, 1);
		verify_int_sorting(1, 0);
		verify_int_sorting(1, 1);
		verify_int_sorting(0, 1, 2, 3, 4, 5);
		verify_int_sorting(5, 4, 3, 2, 1, 0);
		verify_int_sorting(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11);
		verify_int_sorting(11, 10, 9, 8, 7, 6, 5, 4, 3, 2, 1, 0);
	}
	
	@Test
	public void random_int_array_sorting() {
		Random rnd = new Random(1);
		for(int i = 0; i != 1000; ++i) {
			int[] data = randomIntArray(rnd, 0, 2000);
			verify_int_sorting(data);
		}
	}
	
	private int[] randomIntArray(Random rnd, int min, int max) {
		int len  = min + rnd.nextInt(max + 1 - min);
		int[] array = new int[len];
		for(int i = 0; i != array.length; ++i) {
			array[i] = rnd.nextInt();
		}
		return array;
	}

	@Test
	public void simple_double_cases() {
		verify_double_sorting(); // empty
		verify_double_sorting(0);
		verify_double_sorting(0, 1);
		verify_double_sorting(1, 0);
		verify_double_sorting(1, 1);
		verify_double_sorting(0, 1, 2, 3, 4, 5);
		verify_double_sorting(5, 4, 3, 2, 1, 0);
		verify_double_sorting(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11);
		verify_double_sorting(11, 10, 9, 8, 7, 6, 5, 4, 3, 2, 1, 0);
		verify_double_sorting(Double.NaN, 0d, Double.POSITIVE_INFINITY, Double.NEGATIVE_INFINITY);
		verify_double_sorting(Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, 0, Double.NaN);
	}
	
	@Test
	public void random_double_array_sorting() {
		Random rnd = new Random(1);
		for(int i = 0; i != 1000; ++i) {
			double[] data = randomDoubleArray(rnd, 0, 2000);
			verify_double_sorting(data);
		}
	}
	
	private double[] randomDoubleArray(Random rnd, int min, int max) {
		int len  = min + rnd.nextInt(max + 1 - min);
		double[] array = new double[len];
		for(int i = 0; i != array.length; ++i) {
			array[i] = rnd.nextDouble();
		}
		return array;
	}

	public void verify_int_sorting(int... array) {
		int[] order = new IntSorter(array).order(0, array.length);
		int[] ref = array.clone();
		Arrays.sort(ref);
		ArrayShuffler.reorder(array, order);
		
		if (!Arrays.equals(array, ref)) {
			Assert.assertEquals(Arrays.toString(ref), Arrays.toString(array));
		}
	}

	public void verify_double_sorting(double... array) {
		int[] order = new DoubleSorter(array).order(0, array.length);
		double[] ref = array.clone();
		Arrays.sort(ref);
		ArrayShuffler.reorder(array, order);
		
		if (!Arrays.equals(array, ref)) {
			Assert.assertEquals(Arrays.toString(ref), Arrays.toString(array));
		}
	}
	
	public static class IntSorter extends ArraySorter<Void> {

		private final int[] array;
		
		public IntSorter(int[] array) {
			this.array = array;
		}

		@Override
		protected int cmp(int a, int b) {
			return new Integer(array[a]).compareTo(array[b]);
		}
	}

	public static class DoubleSorter extends ArraySorter<Void> {
		
		private final double[] array;
		
		public DoubleSorter(double[] array) {
			this.array = array;
		}
		
		@Override
		protected int cmp(int a, int b) {
			return new Double(array[a]).compareTo(array[b]);
		}
	}
}
