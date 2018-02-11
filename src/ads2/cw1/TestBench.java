package ads2.cw1;

/**
 * Created by wim on 29/11/2017.
 *
 * The testbench will fill the memory and then test a number of access patterns to the cache
 * In particular, tests verify that:
 *  - on first reads from the memory, the cache gets populated
 *  - on subsequent reads from addresses in a cache line, we get cache hits
 *  - when the cache is full, we see evictions
 *
 *  To make this quick and easy we need a small cache, e.g. 8 cachelines, and a 1K memory
 *
 *  The we also test writes to see if the eviction updates the memory.
 *  The third test is a more complex access pattern
 *  The fourth test is not implemented but I will run it against your code, it is also
 *  a more complex access pattern
 */

import ads2.cw1.Memory;

import java.util.Arrays;

class TestBench {

    private int[] reference;
    private int[] buffer;
    private Memory mem;

    private int MEM_SZ;
    private int CACHE_SZ;
    private int CACHELINE_SZ;
    private int CL_SHIFT;

    void run(int ntests) {
        if (ntests>0) test1();
        if (ntests>1) test2();
        if (ntests>2) test3();
        if (ntests>3) test4();
    }

    TestBench(Memory mem_) {
        mem=mem_;
        MEM_SZ = mem.getMemSize();
        CACHE_SZ = mem.getCacheSize();
        CACHELINE_SZ = mem.getCacheLineSize();

        Double cls = Math.log(CACHELINE_SZ)/Math.log(2);
        CL_SHIFT = cls.intValue();
        
        reference = new int[MEM_SZ];
        buffer = new int[MEM_SZ];

        populate_ref();
    }

    private void populate_ref() {
        for (int i=0; i<MEM_SZ;i++) {
            reference[i]=i;
        }
    }

    void test1() {
        String info = "Test 1: Write reference data into memory.";
        System.out.println(info);
        int score=0;
        for (int i=0; i<MEM_SZ;i++) {
//            System.out.println("Writing "+i+" to "+i);
            mem.write(i,reference[i]);

//            System.out.println("Assert: "+mem.status.hit());

            // Every i % CACHELINE_SZ should be miss, all other should be hit
            boolean hit_miss;
            if (i % CACHELINE_SZ == 0) {
                hit_miss = !mem.status.hit() ;
            } else {
                hit_miss = mem.status.hit() ;
            }
            // The first CACHE_SZ/CACHELINE_SZ cache lines will have evict = false,
            // then every first access to a cache line will result in eviction
            boolean evicted;
            if (i<CACHE_SZ) {
                evicted = !mem.status.evicted();
            } else {
                // for i >= CACHE_SZ, the first access leads to eviction
                if (i % CACHELINE_SZ ==0) {
                    evicted = mem.status.evicted();
                } else {
                    evicted = !mem.status.evicted();
                }
            }
            // The stack size will decrease with every cache line until it hits 0
            boolean free;
            if ( i < CACHE_SZ ) {
//                free = mem.status.freeLocations() == 7 - (i>>4);
                free = mem.status.freeLocations() == (CACHE_SZ/CACHELINE_SZ) - 1 - (i >> CL_SHIFT);
            } else {
                free = mem.status.freeLocations() == 0;
            }

            /*
            This is more complicated. The reference and the memory should eventually be identical;
            but on the first write to the cache,
            mem.ram[i] == 0;
            Then on evict, the evicted line should be the same as the reference
            so say we have i = 128, cache is full, we evict cache line 7 = last_used_loc
            then words 112 to 127 are written to the memory
            i.e. 16*last_used_loc+ (0 .. 15)
             */
            boolean content_ok = true;
            if (mem.status.evicted()) {
                int cla = mem.status.evictedCacheLineAddr();
                for (int j = 0; j<CACHELINE_SZ;j++) {
                    int addr = CACHELINE_SZ * cla + j;
                    content_ok = content_ok  && (reference[addr] == mem.ram[addr]);
                }
            } else {
                content_ok = mem.ram[i] == 0;
            }
//            System.out.println("Content: "+mem.ram[i] );

            if (hit_miss) ++score;
            if (evicted) ++score;
            if (free) ++score;
            if (content_ok) ++score;
            if (!hit_miss || !evicted || !free || ! content_ok) {
                System.out.println(mem.status.toString());
            }
            assert hit_miss;
            assert evicted;
            assert free;
            assert content_ok;
        }
        mem.flush();
        boolean mem_content_ok =  Arrays.equals(reference, mem.ram);
//        System.out.println(mem_content_ok);
        assert mem_content_ok;
        if (mem_content_ok) {
            score+=MEM_SZ;
        } else {
            for (int k=0;k<MEM_SZ;k++) {
                if (reference[k] == buffer[k]) {
                    score++;
                }
            }
        }        

        System.out.println("Score for test 1: "+(score*100/MEM_SZ/5)+"%");
    }

    void test2() {
        String info = "Test 2: Read memory into buffer, compare to reference";
        System.out.println(info);
        /*
        We flushed the cache before so what we expect to see is the cache being filled with the consecutive reads,
        and the eviction of a single line, and that line getting filled over and over.
        * */
        int score=0;
        for (int i=0; i<MEM_SZ;i++) {
//            System.out.println("Reading " + i + " from mem and writing to " + i);
            buffer[i] = mem.read(i);


            boolean hit_miss;
            if (i % CACHELINE_SZ == 0) {
                hit_miss = !mem.status.hit() ;
            } else {
                hit_miss = mem.status.hit() ;
            }
            // The first 8 cache lines will have evict = false,
            // then every first access to a cache line will result in eviction
            boolean evicted;
            if (i<CACHE_SZ) {
                evicted = !mem.status.evicted();
            } else {
                // for i >= 8*16, the first access leads to eviction
                if (i % CACHELINE_SZ==0) {
                    evicted = mem.status.evicted();
                } else {
                    evicted = !mem.status.evicted();
                }
            }
            // The stack size will decrease with every cache line until it hits 0
            boolean free;
            if ( i < CACHE_SZ ) {
//                free = mem.status.freeLocations() == 7 - (i>>4);
                free = mem.status.freeLocations() == (CACHE_SZ/CACHELINE_SZ) - 1 - (i >> CL_SHIFT);
            } else {
                free = mem.status.freeLocations() == 0;
            }

//            boolean content_ok = true;
            // This is a read operation, so the memory is not affected.

            if (hit_miss) ++score;
            if (evicted) ++score;
            if (free) ++score;
//            if (content_ok) ++score;
            assert hit_miss;
            assert evicted;
            assert free;
//            assert content_ok;
            if (!hit_miss || !evicted || !free ) {
                System.out.println(mem.status.toString());
            }
        }
        mem.flush();
        boolean mem_content_ok =  Arrays.equals(reference, buffer);
        assert mem_content_ok;
        if (mem_content_ok) {
            score+=MEM_SZ;
        } else {
            for (int k=0;k<MEM_SZ;k++) {
                if (reference[k] == buffer[k]) {
                    score++;
                }
            }
        }
        System.out.println("Score for test 2: "+(score*100/MEM_SZ/4)+"%");

    }

    void test3() {
        String info = "Test 3: Read stencil from memory and update buffer, compare to reference";
        System.out.println(info);
        /*
        So what I want do do here is read a 4-point stencil and update the centre using
        x>>1+(...)>>2
        and of course only if the elements are reachable
        So the cache access pattern will be very interesting.
         */
//        final int ROW_SZ = 32;
        Double mem_nbits_dbl = Math.log(MEM_SZ)/Math.log(2);
        int ROW_SHIFT = mem_nbits_dbl.intValue()>>1;
        final int ROW_SZ = 1 << ROW_SHIFT;
//        System.out.println(ROW_SZ);
        int score=0;
        for (int i=0; i<MEM_SZ;i++){

            int up_r = i-ROW_SZ > 0 ? reference[i-ROW_SZ] : 0;
            int down_r = i+ROW_SZ < MEM_SZ ? reference[i+ROW_SZ] : 0;
            int left_r = (i % ROW_SZ)-1 > 0 ? reference[i-1] : 0;
            int right_r = (i % ROW_SZ)+1 < ROW_SZ-1 ?  reference[i+1] : 0;
            int centre_r = reference[i];
            int avg_r = centre_r >> 1 +(up_r+down_r+left_r+right_r) >> 2;
//            buffer[i]=avg_r;

            int up = i-ROW_SZ > 0 ? mem.read(i-ROW_SZ) : 0;
            if (up==up_r) {
                score++;
            } else {
                System.out.println(mem.status.toString());
            }
            int down = i+ROW_SZ < MEM_SZ ? mem.read(i+ROW_SZ) : 0;
            if (down==down_r) {
                score++;
            } else {
                System.out.println(mem.status.toString());
            }
            int left = (i % ROW_SZ)-1 > 0 ? mem.read(i-1) : 0;
            if (left==left_r) {
                score++;
            } else {
                System.out.println(mem.status.toString());
            }
            int right = (i % ROW_SZ)+1 < ROW_SZ-1 ?  mem.read(i+1) : 0;
            if (right==right_r) {
                score++;
            } else {
                System.out.println(mem.status.toString());
            }
            int centre = mem.read(i);
            if (centre==centre_r) {
                score++;
            } else {
                System.out.println(mem.status.toString());
            }

            int avg = centre >> 1 +(up+down+left+right) >> 2;

            assert avg_r == avg;


        }

        System.out.println("Score for test 3: "+(score*100/MEM_SZ/5)+"%");
    }
    // This test is not implemented 
    // Red-back is a pattern like a checkerboard, so read and write locations are always differnt
    void test4() {
        String info = "Test 4: Read stencil from memory and update memory using red/back pattern, compare to reference";
        assert false;
        System.out.println(info);
        Double mem_nbits_dbl = Math.log(MEM_SZ)/Math.log(2);
        int ROW_SHIFT = mem_nbits_dbl.intValue()>>1;
        final int ROW_SZ = 1 << ROW_SHIFT;
        int score=0;
        for (int i=0; i<MEM_SZ;i++) {
            // ...
        }
        System.out.println("Score for test 4: "+(score*100/MEM_SZ/4)+"%");
    }
}
