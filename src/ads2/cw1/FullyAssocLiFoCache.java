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
import java.util.Map;

class FullyAssocLiFoCache implements Cache {

	final private static boolean VERBOSE = false;

	final private int CACHE_SZ;
	final private int CACHELINE_SZ;
	final private int CL_MASK;
	final private int CL_SHIFT;

	// because the cache replacement policy is "Last In First Out" you only need to
	// know the "Last Used" location WV
	// "Last Used" means accessed for either read or write WV
	// The helper functions below contain all needed assignments to last_used_loc so
	// I recommend you use these. WV

	private int last_used_loc =0 ;
	// - One for the cache storage WV
	private int[] cache_storage = {};
	// - One to manage locations in the cache WV
	private int location_stack = 0;
	// - One to translate between memory addresses and cache locations WV
	private Map<Integer, Integer> address_to_cache_loc = new HashMap<Integer, Integer>();
	// - One to translate between cache locations and memory addresses WV
	private Map<Integer, Integer> cache_loc_to_address = new HashMap<Integer, Integer>();
	private int value = 0;
	private int data = 0;

	FullyAssocLiFoCache(int cacheSize, int cacheLineSize) {

		CACHE_SZ = cacheSize;
		CACHELINE_SZ = cacheLineSize;
		CL_MASK = CACHELINE_SZ - 1;
		Double cls = Math.log(CACHELINE_SZ) / Math.log(2);
		CL_SHIFT = cls.intValue();

		last_used_loc = CACHE_SZ / CACHELINE_SZ - 1;
		cache_storage = new int[CACHE_SZ];

		Stack<Integer> location_stack = new Stack<Integer>();
		Map<Integer, Integer> address_to_cache_loc = new HashMap<Integer, Integer>();
		Map<Integer, Integer> cache_loc_to_address = new HashMap<Integer, Integer>();

		/**
		 * These were my previous initialisations. I changed to hashmaps cos they look
		 * like they make more sense RA value = cache_storage[address]; idx = address %
		 * CACHELINE_SZ; address_to_cache_loc = address & CL_MASK; cache_loc_to_address
		 * = cache_line_start_mem_address(0) + cache_entry_position(0);
		 **/

	}

	// what is the purpose of this method? i mean, what should i implement here? is
	// this to clear the cache? RA
	public void flush(int[] ram, Status status) {
		if (VERBOSE)
			System.out.println("Flushing cache");
		// Your other data structures here WV
		status.setFlushed(true);
	}

	public int read(int address, int[] ram, Status status) {
		return read_data_from_cache(ram, address, status);
	}

	public void write(int address, int data, int[] ram, Status status) {
		write_data_to_cache(ram, address, data, status);
	}

	// The next two methods are the most important ones as they implement read() and
	// write() WV
	// Both methods modify the status object that is provided as argument WV

	private void write_data_to_cache(int[] ram, int address, int data, Status status) {
		status.setReadWrite(false); // i.e. a write
		status.setAddress(address);
		status.setData(data);
		status.setEvicted(false);

		// if address already exists in cache: don't write, break away RA
		while (address_in_cache_line(address) != cache_line_address(address)) {
			// if cache not empty RA
			if (CACHE_SZ == 0) {
				// value to input = cached data at address location RA
				// cache_line_address(address) returns address corresponding to the cache from
				// main memory address
				address_to_cache_loc.put(cache_line_address(address), data);
				// else if cache isn't empty but not full RA
			} else if (CACHE_SZ != cache_storage.length) {
				// address + 1 RA
				address += 1;
				address_to_cache_loc.put(cache_line_address(address), data);
			} else {
				// if cache full set address to last position RA
				address = cache_storage.length - 1;
				write_to_mem_on_evict(ram, address, data);
				// overwrite last position RA
				address_to_cache_loc.put(cache_line_address(address), data);
			}
			break;
		}

		// The cache policy is write-back, so the writes are always to the cache. WV
		// The update policy is write allocate: on a write miss, a cache line is loaded
		// to cache, followed by a write operation. WV

	}

	private int read_data_from_cache(int[] ram, int address, Status status) {
		status.setReadWrite(true); // i.e. a read WV
		status.setAddress(address);
		status.setEvicted(false);
		status.setHitOrMiss(true); // i.e. a hit WV

		// while address exists in cache
		if (address_in_cache_line(address) == cache_line_address(address)) {
			// search the cache for the corresponding key RA
			// value = hash value corresponding to key (address) RA
			if(address_to_cache_loc.get(address) !=null) {
				value = address_to_cache_loc.get(address);
				// write value to corresponding cache location RA
				cache_loc_to_address.put(cache_line_start_mem_address(address), value);
				// read to data RA
				// I don't think this actually updates the data field.... ? RA
				data = fetch_cache_entry(address);
			}
		} else {
			// address doesn't exist, read from memory RA
			// I don't think this actually updates the data field.... ? RA
			int data = read_from_mem_on_miss(ram, address);

			// Reads are always to the cache. On a read miss you need to fetch a cache line
			// from the DRAM 
			// If the data is not yet in the cache (read miss),fetch it from the DRAM 
			// Get the data from the cache WV
		}
		// return read value RA
		status.setData(data);
		return data;
	}

	// You might want to use the following methods as helpers 
	// but it is not mandatory, you may write your own as well WV

	// On read miss, fetch a cache line WV

	// If its a read miss then it isn't in the cache. so am i fetching the value
	// from the main memory? I assume yes, but am i then just reading from the main
	// memory or is the point in this fetch so that i can write it to the cache? For 
	// now i'm just going to read from MM RA
	private int read_from_mem_on_miss(int[] ram, int address) {
		int[] cache_line = new int[CACHELINE_SZ];
		int loc = 0;
		int data = cache_line_start_mem_address(address);
		last_used_loc = loc;
		return data;
	}

	// I don't think this method is necessary but i could be wrong RA
	/**
	 * On write, modify a cache line private void update_cache_entry(int address,
	 * int data){ int loc = 0; // Your code here // ... last_used_loc=loc; }
	 **/

	// When we fetch a cache entry, we also update the last used location WV
	private int fetch_cache_entry(int address) {
		int[] cache_line = cache_storage;
		int loc = 0;
		// Your code here WV
		last_used_loc = loc;
		return cache_line[cache_line_address(address)];
	}

	// Should return the next free location in the cache WV
	// Again, i don't think this is necessary, aren't hashmaps smarter than using 
	// empty space (change size dynamically)? RA
	/**
	 * private int get_next_free_location() { return CACHELINE_SZ; // Your code here
	 * WV
	 * 
	 * }
	 **/

	// Given a cache location, evict the cache line stored there WV
	private void evict_location(int loc) {
		// maybe works? i don't know if i need to evict if I'm overwriting the last location anyway RA
		address_to_cache_loc.remove(loc);
		// Your code here WV

	}

	// I also don't think this method is required
	/**
	 * private boolean cache_is_full() { return false; // Your code here WV }
	 **/

	// When evicting a cache line, write its contents back to main memory WV
	private void write_to_mem_on_evict(int[] ram, int loc, int data) {
		
		data = 0;
		//um, i guess i could do this? or just use loc? RA
		int evicted_cl_address = loc;
		//I'll assume I'm meant to use this but I haven't RA
		int[] cache_line = ram;
		if (VERBOSE)
			System.out.println("Cache line to RAM: ");
		 	//place this data back in memory (i dont think this is how to do it) RA
		 	cache_loc_to_address.put(cache_line_start_mem_address(evicted_cl_address), data);
		// Your code here WV
		 	
		 //evict this data after written back to memory RA
		evict_location(loc);
	}

	// Test if a main memory address is in a cache line stored in the cache WV
	// In other words, is the value for this memory address stored in the cache? WV
	private int address_in_cache_line(int address) {
		int cl_address = (address - 1) * CACHELINE_SZ;
		return cl_address;
	}

	// Given a main memory address, return the corresponding cache line address WV
	private int cache_line_address(int address) {
		return address >> CL_SHIFT;
	}

	// Given a main memory address, return the corresponding index into the cache
	// line WV
	private int cache_entry_position(int address) {
		return address & CL_MASK;
	}

	// Given a cache line address, return the corresponding main memory address WV
	// This is the starting address of the cache line in main memory WV
	private int cache_line_start_mem_address(int cl_address) {
		return cl_address << CL_SHIFT;
	}

}
