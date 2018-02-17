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
import java.util.Arrays;


class FullyAssocLiFoCache implements Cache {

	final private static boolean VERBOSE = false;

	final private int CACHE_SZ;
	final private int CACHELINE_SZ;
	final private int CL_MASK;
	final private int CL_SHIFT;

	private int last_used_loc =0 ;
	// - One for the cache storage WV
	private int[] cache_storage = {};
	// - One to manage locations in the cache WV
	private int location_stack = 0;
	// - One to translate between memory addresses and cache locations WV
	private int address_to_cache_loc;
	// - One to translate between cache locations and memory addresses WV
	private int cache_loc_to_address;
	private int value;
	private int idx;
	private int data = 0;
	private int address = 0;

	FullyAssocLiFoCache(int cacheSize, int cacheLineSize) {

		CACHE_SZ = cacheSize;
		CACHELINE_SZ = cacheLineSize;
		CL_MASK = CACHELINE_SZ - 1;
		Double cls = Math.log(CACHELINE_SZ) / Math.log(2);
		CL_SHIFT = cls.intValue();

		last_used_loc = CACHE_SZ / CACHELINE_SZ - 1;
		cache_storage = new int[CACHE_SZ];
		Stack<Integer> location_stack = new Stack<Integer>();
		idx = address % CACHELINE_SZ;
		value = cache_storage[address];
		
		address_to_cache_loc = address & CL_MASK;
		cache_loc_to_address = cache_line_start_mem_address(0) + cache_entry_position(0);

	}

	public void flush(int[] ram, Status status) {
		if (VERBOSE)
			System.out.println("Flushing cache");
		status.setFlushed(true);
	}

	public int read(int address, int[] ram, Status status) {
		return read_data_from_cache(ram, address, status);
	}

	public void write(int address, int data, int[] ram, Status status) {
		write_data_to_cache(ram, address, data, status);
	}


	private void write_data_to_cache(int[] ram, int address, int data, Status status) {
		status.setReadWrite(false); // i.e. a write
		status.setAddress(address);
		status.setData(data);
		status.setEvicted(false);

		// if address already exists in cache: don't write, break away RA
		while (address_in_cache_line(address) != cache_line_address(address)) {
			// if cache empty RA
			if (CACHE_SZ == 0) {
				//write to first location
				cache_storage[CACHE_SZ] = data;
				//if not empty but not full
			} else if (CACHE_SZ != cache_storage.length) {
				address = get_next_free_location();
				//write to first available location
				cache_storage[address] = data;
			} else {
				// if cache full set address to last position RA
				address = cache_storage.length - 1;
				write_to_mem_on_evict(ram, address, data);
				// overwrite last position RA
				cache_storage[address] = data;
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
			if(cache_storage[address] != 0) {
				value = cache_storage[address];
				// write value to corresponding cache location RA
				cache_loc_to_address = cache_line_start_mem_address(address) + cache_entry_position(address);
				// read to data RA
				data = fetch_cache_entry(cache_loc_to_address);
			}
		} else {
			int data = read_from_mem_on_miss(ram, address);
		}
		// return read value RA
		status.setData(data);
		return data;
	}


	private int read_from_mem_on_miss(int[] ram, int address) {
		int[] cache_line = new int[CACHELINE_SZ];
		int loc = 0;
		int data = cache_line_start_mem_address(address);
		last_used_loc = loc;
		return data;
	}


	private void update_cache_entry(int address, int data){ 
		int loc = 0; 
	 last_used_loc=loc; }
	 

	// When we fetch a cache entry, we also update the last used location WV
	private int fetch_cache_entry(int address) {
		int[] cache_line = cache_storage;
		int loc = 0;
		// Your code here WV
		last_used_loc = loc;
		return cache_line[cache_line_address(address)];
	}

	 private int get_next_free_location() { 
		 int i = 0;
		 for(i=0; i < cache_storage.length; i++)
			    if(cache_storage[i] == 0)
		 
		return i;
		return i;
	 }

	private void evict_location(int loc) {
		cache_storage[loc] = 0;
	}

	 private boolean cache_is_full() {
		 return false; 
	 }

	// When evicting a cache line, write its contents back to main memory WV
	private void write_to_mem_on_evict(int[] ram, int loc, int data) {
		
		int evicted_cl_address = loc;
		int[] cache_line = ram;
		if (VERBOSE)
			System.out.println("Cache line to RAM: ");
			ram[cache_loc_to_address] = data;
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
