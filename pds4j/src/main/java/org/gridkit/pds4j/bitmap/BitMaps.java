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
 * Static factory for bitmap structures.
 *  
 * @author Alexey Ragozin (alexey.ragozin@gmail.com)
 */
public class BitMaps {

    public static BitMap wrap(BitSet bitSet) {        
        return new BitSetAdapter(bitSet);
    }
    
    public static BitMap sparseBitMap() {
        return new PagedBitMap(true);
    }

    public static BitMap semiSparceBitmap() {
        return new PagedBitMap(new PagedLongArray());
    }
}
