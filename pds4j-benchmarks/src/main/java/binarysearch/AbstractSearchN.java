package binarysearch;

import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;

@State(Scope.Benchmark)
public abstract class AbstractSearchN {

    public static int M = 1 << 20;
    
    private int[][] array;
    private int chunkSize = getChunkSize();
    private int chunksCount = 64 * M / chunkSize;
    
    private AtomicInteger seed = new AtomicInteger(1);
    
    protected abstract int getChunkSize();
    
    @Setup(Level.Trial)
    public void generateData() {
        array = new int[chunksCount][];
        int remaining = chunksCount;
        Random rnd = new Random(1);
        while(remaining > 0) {
            int n = rnd.nextInt(chunksCount);
            if (array[n] == null) {
                --remaining;
                int[] chunk = new int[chunkSize / 2 + rnd.nextInt(chunkSize)];
                array[n] = chunk;
                for(int i = 0; i != chunk.length; ++i) {
                    array[n][i] = rnd.nextInt();
                }
                Arrays.sort(array[n]);
                int b1 = Arrays.binarySearch(chunk, 0);
                int b2 = Arrays.binarySearch(chunk, chunk[chunk.length / 2]);
                int s1 = lookup(chunk, 0);
                int s2 = lookup(chunk, chunk[chunk.length / 2]);
                if (b1 != s1 || b2 != s2) {
                    throw new IllegalArgumentException("Verification have failed");
                }
            }
        }        
    }
    
    long superTotal = 0;
    
    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    public void searchKey() {
        Random rnd = new Random(seed.incrementAndGet());
        for(int i = 0; i != 1000000; ++i) {
            int n = rnd.nextInt(chunksCount);
            int[] chunk = array[n];
            int m = rnd.nextInt();
            superTotal += lookup(chunk, m);
        }
    }
    
    protected abstract int lookup(int[] chunk, int number);
    
//    @Test
//    public void test_test() {
//        generateData();
//        prepareKeys();
//        searchKey();
//    }    
}
