package ads2.cw1;

/**
 * Created by wim on 28/11/2017.
 * This object tracks the status of the cache.
 * It is crucial that you ensure that the status is updated correctly whenever your cache performs an operation.
 * The purpose of the attributes is explained below
 * The getters and setters are self-explanatory
 */
class Status {
    // Was the cache operation a Read or Write operation?
    private boolean read_write_; // read = true, write=false
    // Was the accessed element in the cache or not?
    private boolean hit_; // hit = true, miss = false
    // If an eviction was needed because the cache was full and the element access was not present
    private boolean evicted_; // true if evicted, else false
    // The cache has been flushed
    private boolean flushed_;
    // The accessed memory address
    private int memAddress_;
    // The data stored at that address location
    private int data_;
    // The number of free locations for cache lines in the cache
    private int free_locs_;
    // The location that was evicted 
    private int evicted_cache_loc_;
    // The address in main memory of the cache line that was evicted
    private int evicted_cacheline_addr;

    // Getters
    String readOrWrite() { return (read_write_ ? "Read" : "Write"); }
    int memAddress() { return memAddress_; }
    int data() { return data_; }
    boolean evicted() { return evicted_; }
    boolean hit() { return hit_; }
    int freeLocations() {return free_locs_; }
    int evictedCacheLoc() { return evicted_cache_loc_; }
    int evictedCacheLineAddr() { return evicted_cacheline_addr; }
    boolean flushed() {return flushed_;}

    // Setters
    boolean setReadWrite(boolean c) { read_write_ = c; return read_write_; }
    int setAddress(int a) { memAddress_=a; return memAddress_; }
    int setData(int d) { data_=d; return data_; }
    boolean setEvicted(boolean e) { evicted_=e; return evicted_; }
    boolean setHitOrMiss(boolean e) { hit_=e; return hit_; }
    int setFreeLocations(int c) {free_locs_ = c ;return free_locs_; }
    int setEvictedCacheLoc(int e) { evicted_cache_loc_=e; return evicted_cache_loc_; }
    int setEvictedCacheLineAddr(int a) { evicted_cacheline_addr = a; return evicted_cacheline_addr; }
    boolean setFlushed(boolean b) {flushed_=b;return flushed_;}

    public String toString() {
        return "R/W: "+ readOrWrite()+" | Addr: "+memAddress()+" | Data: "+data()+" | Hit? "+hit()+" | "+" Eviction: "+evicted()+" | Free locs: "+freeLocations();
    }
}
