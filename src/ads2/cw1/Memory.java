package ads2.cw1;

/**
 * Created by wim on 28/11/2017.
 * This is an array-based memory model
 * The main memory consists of a fixed-size array of integers
 * The cache object is what you have to implement
 */

import ads2.cw1.Cache;

class Memory {
    // Main memory storage
    protected int[] ram;
    // The cache
    private Cache cache;
    // The size (in 32-bit integers) of the main memory
    final private int MEM_SZ;
    // The size (in 32-bit integers) of the cache
    final private int CACHE_SZ;
    // The size (in 32-bit integers) of a cache line
    final private int CACHELINE_SZ;
    
    // The status of the memory, see the Status class for documentation
    protected Status status;

    // Memory read operation. All reads go through the cache
    int read(int address) {
        return cache.read(address,ram,status);
    }
    // Memory write operation. All writes go through the cache    
    void write(int address,int data) {
        cache.write(address,data,ram,status);
    }
    // Flushing the cache means removing all its contents and writing it back to the main memory
    void flush() {
        cache.flush(ram,status);
    }

    // Getters
    Status getStatus() {
        return status;
    }
    
    int getMemSize() {
        return MEM_SZ;
    }

    int getCacheSize() {
        return CACHE_SZ;
    }

    int getCacheLineSize() {
        return CACHELINE_SZ;
    }

    // There are no Setters: all operations on ram, status and cache must be done in your cache implementation

    // Constructor, instantiates ram and cache with the provided size and creates the status object.
    Memory(int memSize,int cacheSize, int cacheLineSize) {
        MEM_SZ = memSize;
        CACHE_SZ = cacheSize;
        CACHELINE_SZ = cacheLineSize;

        ram = new int[memSize];
        status = new Status();
        cache = new FullyAssocLiFoCache(cacheSize, cacheLineSize);

    }

    // This lets you peek directly at the content of a location in the main memory
    // Normally you should not need this.
    int peek(int address) {
        return ram[address];
    }
    

}
