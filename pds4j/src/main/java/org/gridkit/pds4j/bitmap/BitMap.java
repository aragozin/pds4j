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

/**
 * Unlike {@link BitSet}, interface allow 64bit addressing of bits.
 * Practical implementation are rely on space properties of bit population
 * and employ various lazy page allocation strategies.
 * 
 * @author Alexey Ragozin (alexey.ragozin@gmail.com)
 */
public interface BitMap {

    long countOnes();

    Iterable<Long> ones();

    void mult(BitMap that);

    void sub(BitMap that);

    void addWithOverflow(BitMap that, BitMap overflow);

    void add(BitMap that);

    boolean getAndSet(long index, boolean value);

    void set(long index, boolean value);

    long seekOne(long start);

    boolean get(long index);

}
