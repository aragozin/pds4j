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

/**
 * A simple implementation of hash table.
 * Closed addressing hash table is used. Hash collisions
 * are resolved by chaining entries.
 * <br/>
 * Hash table tolerates hash duplicates, which are stored in
 * collision chain.
 * <br/>
 * If collision chain is growing large (due to bad hashing or duplicate key)
 * performance may suffer.
 * 
 * @author Alexey Ragozin (alexey.ragozin@gmail.com)
 *
 * @param <K>
 */
public abstract class SimpleHashLookup<K> {

    protected int size = 0;
    protected int loadFactor = (int)(0.75 * (1 << 10)); // fixed point number

    protected int[] nexts;
    protected int[] hashes;

    protected int[] heads;
    protected int load;

    public SimpleHashLookup() {
        reset();
    }

    protected abstract K keyAt(int entryId);

    protected int keyHash(K key) {
        return key.hashCode();
    }

    /**
     * Negative values have special meaning in {@link #hashes} table,
     * so we want hash to be positive.
     */
    private int fixedKeyHash(K key) {
        return 0x7FFFFFFF & keyHash(key);
    }

    protected boolean keysEqual(K a, K b) {
        return a.equals(b);
    }

    /**
     * @return number of entries in hash table
     */
    public int getSize() {
        return size;
    }

    /**
     * Insert or updates entry in hash table. 
     * @param entryId to be inserted/updated 
     */
    public void put(int entryId) {
        if (entryId < 0) {
            throw new IllegalArgumentException("EntryId is out of range: " + entryId);
        }

        grow(entryId);

        int oldHash = hashes[entryId];
        if (oldHash == -1) {
            // entry were not in hash, grow hash
            size++;
        }
        if (size > load) {
            rehash();
        }

        K key = keyAt(entryId);
        int hashCode = fixedKeyHash(key);
        if (oldHash != hashCode) {
            if (oldHash != -1) {
                removeEntry(entryId);
                size++;
            }
            int head = getHead(hashCode, entryId);
            if (head != entryId) {
                while (true) {
                    if (nexts[head] >= 0) {
                        head = nexts[head];
                    } else {
                        nexts[head] = entryId;
                        break;
                    }
                }
            }
            hashes[entryId] = hashCode;
        }
    }

    /**
     * Erases entry in hash table
     * @param entryId
     * @return <code>true</code> if another entry with same hash MAY BE present in hash
     */
    public boolean removeEntry(int entryId) {
        if (entryId < 0) {
            throw new IllegalArgumentException("EntryId is out of range: " + entryId);
        }
        if (entryId >= hashes.length) {
            return false;
        }
        
        int oldHash = hashes[entryId];

        if (oldHash != -1) {
            size--;
        } else {
            return false;
        }

        int head = getHead(oldHash, entryId);

        boolean found = false;
        boolean hasMoreSameHash = false;
        int prevHead = -1;
        while (head > -1) {

            if (!found) {
                if (head == entryId) {
                    if (prevHead != -1) {
                        nexts[prevHead] = nexts[entryId];
                        nexts[entryId] = -1;
                        found = true;
                        if (hasMoreSameHash) {
                            break;
                        }
                    } else {
                        resetHead(oldHash, nexts[entryId]);
                        found = true;
                    }
                } else {
                    if (hashes[head] == oldHash) {
                        hasMoreSameHash = true;
                    }
                }
            } else {
                if (hashes[head] == oldHash) {
                    hasMoreSameHash = true;
                    break;
                }
            }
            prevHead = head;
            head = nexts[head];
        }
        nexts[entryId] = -1;
        hashes[entryId] = -1;
        return hasMoreSameHash;
    }

    /**
     * @param key
     * @return first matching entry or -1 if nothing found
     */
    public int seek(K key) {

        int head = getHead(fixedKeyHash(key), -1);
        boolean found = false;
        while (head >= 0) {
            if (keysEqual(key, keyAt(head))) {
                found = true;
                break;
            }
            head = nexts[head];
        }
        return found ? head : -1;
    }

    /**
     * Find next entryId for given key
     * @param key key
     * @param prevEntryId previous found entry
     * @return next entry for same key or <code>-1</code>
     */
    public int seekNextDuplicate(K key, int prevEntryId) {
        int h = hashes[prevEntryId];
        int r = nexts[prevEntryId];
        while(r >= 0) {
            if (h == hashes[r] && keysEqual(key, keyAt(r))) {
                return r;
            }
            r = nexts[r];
        }
        return -1;
    }
    
    /**
     * Removes all entries from tables, 
     * but without reinitialized internal structures.
     * 
     * @see SimpleHashLookup#reset()
     */
    public void clean() {
        Arrays.fill(nexts, -1);
        Arrays.fill(hashes, -1);
        Arrays.fill(heads, -1);
        load = (loadFactor * heads.length) >> 10;
        size = 0;        
    }
    
    /**
     * Removes all entries, reinitialized internal
     * structures to default sizes;
     * 
     * @see SimpleHashLookup#clean()
     */
    public void reset() {

        nexts = new int[1024];
        Arrays.fill(nexts, -1);
        hashes = new int[1024];
        Arrays.fill(hashes, -1);
        heads = new int[1024];
        Arrays.fill(heads, -1);
        load = (loadFactor * heads.length) >> 10;
        size = 0;
    }

    protected void rehash() {

        int[] nheads = new int[heads.length*2];
        Arrays.fill(nheads, -1);

        for (int h = 0; h < heads.length; h++) {

            while (heads[h] != -1) {

                int entry = heads[h];

                int hash = hashes[entry];
                int i = (hash & (nheads.length - 1));
                if (nheads[i] == -1) {
                    nheads[i] = entry;
                } else {
                    int r = nheads[i];
                    while (true) {
                        if (nexts[r] == -1) {
                            nexts[r] = entry;
                            break;
                        } else {
                            r = nexts[r];
                        }
                    }
                }

                heads[h] = nexts[entry];
                nexts[entry] = -1;
            }
        }
        heads = nheads;
        load = (loadFactor * heads.length) >> 10;
    }

    protected void grow(int targetSize) {

        if (targetSize < hashes.length) {
            return;
        }
        int length = hashes.length;
        while (targetSize >= length) {
            length *= 2;
        }
        int[] nhashes = new int[length];
        Arrays.fill(nhashes, -1);
        int[] nnexts = new int[length];
        Arrays.fill(nnexts, -1);
        System.arraycopy(hashes, 0, nhashes, 0, hashes.length);
        System.arraycopy(nexts, 0, nnexts, 0, nexts.length);
        hashes = nhashes;
        nexts = nnexts;
    }

    protected int resetHead(int hash, int entry) {
        int idx = hash2head(hash);
        int lastHead = heads[idx];
        heads[idx] = entry;
        return lastHead;
    }

    protected int getHead(int hash, int entry) {
        int idx = hash2head(hash);
        if (heads[idx] == -1) {
            heads[idx] = entry;
            return entry;
        }
        return heads[idx];
    }

    protected int hash2head(int hash) {
        return (hash & (heads.length - 1));
    }
}
