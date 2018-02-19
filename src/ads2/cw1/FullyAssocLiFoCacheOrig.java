package ads2.cw1;

/**
 * Created by wim on 28/11/2017.
 * The public interface of this class is provided by Cache
 * All other methods are private. 
 * You must implement/complete all these methods
 * You are allow to create helper methods to do this, put them at the end of the class 
 */
import ads2.cw1.Cache;

import java.util.Stack;
import java.util.HashMap;
import java.util.Set;

class FullyAssocLiFoCacheOrig implements Cache {

    final private static boolean VERBOSE = false;

    final private int CACHE_SZ;
    final private int CACHELINE_SZ;
    final private int CL_MASK;
    final private int CL_SHIFT;

    // WV: because the cache replacement policy is "Last In First Out" you only need to know the "Last Used" location
    // "Last Used" means accessed for either read or write
    // The helper functions below contain all needed assignments to last_used_loc so I recommend you use these.

    private int last_used_loc;
    // WV: Your other data structures here
    // Hint: You need 4 data structures
    // - One for the cache storage
    private int[] cache_storage;    
    // - One to manage locations in the cache
    private int[]  location_stack;    
    // And because the cache is Fully Associative:
    // - One to translate between memory addresses and cache locations
    private int[]  address_to_cache_loc;
    // - One to translate between cache locations and memory addresses  
    //private int[]  cache_loc_to_address;
    


    public FullyAssocLiFoCacheOrig(int cacheSize, int cacheLineSize) {

        CACHE_SZ =  cacheSize;
        CACHELINE_SZ = cacheLineSize;
        CL_MASK = CACHELINE_SZ - 1;
        Double cls = Math.log(CACHELINE_SZ)/Math.log(2);
        CL_SHIFT = cls.intValue();
        int number_of_locations = CACHE_SZ /CACHELINE_SZ;
        cache_storage = new int[CACHE_SZ];
        location_stack = new int[number_of_locations];
        address_to_cache_loc =new int[number_of_locations];
        //cache_loc_to_address =new int[number_of_locations];
        
        last_used_loc = CACHE_SZ/CACHELINE_SZ - 1;
        // WV: Your initialisations here
       
    }

    public void flush(int[] ram, Status status) {
        if (VERBOSE) System.out.println("Flushing cache");
        // WV: Your other data structures here

        status.setFlushed(true);
    }

    public int read(int address,int[] ram,Status status) {
        return read_data_from_cache( ram, address, status);
    }

    public void write(int address,int data, int[] ram,Status status) {
        write_data_to_cache(ram, address, data, status);
    }

    // The next two methods are the most important ones as they implement read() and write()
    // Both methods modify the status object that is provided as argument

    private void write_data_to_cache(int[] ram, int address, int data, Status status){
        status.setReadWrite(false); // i.e. a write
        status.setAddress(address);
        status.setData(data);
        status.setEvicted(false);
        // Your code here
        // The cache policy is write-back, so the writes are always to the cache. 
        // The update policy is write allocate: on a write miss, a cache line is loaded to cache, followed by a write operation. 
         // ...
        
        int idx = getCacheLoc(address);
        System.out.println("get loc idx "+idx);
        if(idx>-1) {
        	System.out.println("found address write here");
        	cache_storage[idx*CACHELINE_SZ]=data;
        	address_to_cache_loc[idx]=address;
        	last_used_loc=idx;
        }else {
        	
        	idx = get_next_free_location();
        	System.out.println("next free "+idx);
        	if(idx>-1) {
        		System.out.println("found free write here " +idx);
        		cache_storage[idx*CACHELINE_SZ]=data;
        		address_to_cache_loc[idx]=address;
            	last_used_loc=idx;
        	}else {
        		System.out.println("evict & write to "+last_used_loc);
        		write_to_mem_on_evict(ram, last_used_loc);
        		evict_location(last_used_loc);
        		cache_storage[last_used_loc*CACHELINE_SZ]=data;
        		address_to_cache_loc[last_used_loc]=address;
        		status.setEvicted(true);
        	}
        }

    }
        
    private int getCacheLoc(int address) {
		for (int i = 0; i < address_to_cache_loc.length; i++) {
			if(address_to_cache_loc[i]==address) {
				return i;
			}
		}
		return -1;
	}
    
    private int getAddress(int loc) {
		return address_to_cache_loc[loc];	
	}

	private int read_data_from_cache(int[] ram,int address, Status status){
        status.setReadWrite(true); // i.e. a read
        status.setAddress(address);
        status.setEvicted(false);
        status.setHitOrMiss(true); // i.e. a hit
        int data=0;
        int idx = getCacheLoc(address);
        if(idx>-1) {
        	data= cache_storage[idx];
        }
        // Your code here
        // Reads are always to the cache. On a read miss you need to fetch a cache line from the DRAM
        // If the data is not yet in the cache (read miss),fetch it from the DRAM
        // Get the data from the cache
         // ...

        status.setData(data);
        return data;
    }

    // You might want to use the following methods as helpers
    // but it is not mandatory, you may write your own as well
    
    // On read miss, fetch a cache line    
    private void read_from_mem_on_miss(int[] ram,int address){
        int[] cache_line = new int[CACHELINE_SZ];
        int loc =0;
        // Your code here
         // ...

        last_used_loc=loc;
   }

    // On write, modify a cache line
    private void update_cache_entry(int address, int data){
        int loc =0;
         // Your code here
         // ...

        last_used_loc=loc;
       }

    // When we fetch a cache entry, we also update the last used location
    private int fetch_cache_entry(int address){
        int[] cache_line= {0};
        int loc =0;
         // Your code here
         // ...
        last_used_loc=loc;
        return cache_line[cache_line_address(address)];
    }

    // Should return the next free location in the cache
    private int get_next_free_location(){
         for (int i = 0; i < cache_storage.length; i+=CACHELINE_SZ) {
			if(cache_storage[i]==0) {
				return i/CACHELINE_SZ;
			}
		}
    	return -1;
        
    }

    // Given a cache location, evict the cache line stored there
    private void evict_location(int loc){
    	// 
    	System.out.println("I predicta riot, evict evict");
        
    	
        
    }

    private boolean cache_is_full(){
         // Your code here
         // ...
        return false;
    }

    // When evicting a cache line, write its contents back to main memory
    private void write_to_mem_on_evict(int[] ram, int loc){

        int evicted_cl_address;
        int[] cache_line;
        if (VERBOSE) System.out.println("Cache line to RAM: ");
        
        

        evict_location(loc);
    }

    // Test if a main memory address is in a cache line stored in the cache
    // In other words, is the value for this memory address stored in the cache?
    private boolean address_in_cache_line(int address) {
        // Your code here
         // ...
    	return false;
        
    }

    // Given a main memory address, return the corresponding cache line address
    private int cache_line_address(int address) {
        return address>>CL_SHIFT;
    }

    // Given a main memory address, return the corresponding index into the cache line
    private int cache_entry_position(int address) {
        return address & CL_MASK;
    }
    // Given a cache line address, return the corresponding main memory address
    // This is the starting address of the cache line in main memory
    private int cache_line_start_mem_address(int cl_address) {
        return cl_address<<CL_SHIFT;
    }

}
