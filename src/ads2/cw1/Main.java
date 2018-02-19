package ads2.cw1;

import ads2.cw1.Memory;
import ads2.cw1.TestBench;

/**
 * All this main function. All the work is done elsewhere
 */
public class Main {

    public static void main(String[] args) {

        final int MEM_SZ=1024; // 1024 words
        final int CACHELINE_SZ=16; // 16 words
        final int CACHE_SZ=8*CACHELINE_SZ; // 8 lines of 16 words
        
        // Create a Memory instance
        Memory mem = new Memory(MEM_SZ,CACHE_SZ,CACHELINE_SZ);
        // Create a Testbench instance
        TestBench tb = new TestBench(mem);
        // Run the first 3 tests in the testbench
        tb.run(2);
    }
}
