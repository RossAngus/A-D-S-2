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

class FullyAssocLiFoCache implements Cache {

	final private static boolean VERBOSE = false;

	final private int CACHE_SZ;
	final private int CACHELINE_SZ;
	final private int CL_MASK;
	final private int CL_SHIFT;

	private int last_used_loc = 0;
	// why not a 2D array?
	private int[][] cache_storage;
	// - One to manage locations in the cache WV
	private int location_stack;
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

		int[] location_stack = new int[CACHE_SZ];
		last_used_loc = CACHE_SZ / CACHELINE_SZ - 1;
		cache_storage = new int[CACHE_SZ][CACHE_SZ];
		idx = address % CACHELINE_SZ;


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
		
		//if within cache size
		if (address < 8) {
			//if first position == empty
			if (cache_storage[0][1] == 0) {
				//loop for size of cache line
				for (int i = 0; i < CACHELINE_SZ; i += 1) {
					//write entire cache line to single location
					cache_storage[0][i] = data+i;
					//debugging
					System.out.println(cache_storage[0][i]);
				}

			//else, if not first position, but not last
			} else if (address < 7) {
				//address = next free location
				address = get_next_free_location();
				//loop for size of cache line
				for (int i = 0; i < CACHELINE_SZ; i += 1) {
					//write entire cache line to single location
					cache_storage[address][i] = ((data*CACHELINE_SZ)+i);
					//debugging
					System.out.println(cache_storage[address][i]);
				}

			//if final position
			} else if (address == 7){
				//write to final position
				address = cache_storage.length - 1;
				//evict whatever is in the last position
				write_to_mem_on_evict(ram, address, data);
				//loop for size of cache line
				for (int i = 0; i < CACHELINE_SZ; i += 1) {
					//write entire cache line to single location
					cache_storage[address][i] = ((data*CACHELINE_SZ)+i);
					//debugging
					System.out.println(cache_storage[address][i]);
				}
			}
		}
		//trying to figure out how testbench determines 'free locs'
		location_stack -= 1;
	
	}
	

	private int read_data_from_cache(int[] ram, int address, Status status) {
		status.setReadWrite(true); // i.e. a read WV
		status.setAddress(address);
		status.setEvicted(false);
		status.setHitOrMiss(true); // i.e. a hit WV

		//for each piece of data
		for (int j = 0; j < 128; j++) {
			//for each cache position
			for (int i = 0; i < 8; i++) {
				//if data= address
				if (j == address) {
					//data = corresponding location
					data = cache_storage[i][j];
			}else {
					//else, read from memory
					int data = read_from_mem_on_miss(ram, address);
					}
				}
			//set data to the new value
			status.setData(data);
		}
		//return
		return data;
	}

	private int read_from_mem_on_miss(int[] ram, int address) {
		int[] cache_line = new int[CACHELINE_SZ];
		int loc = 0;
		int data = cache_line_start_mem_address(address);
		last_used_loc = loc;
		return data;
	}

	private void update_cache_entry(int address, int data) {
		int loc = 0;
		last_used_loc = loc;
	}

	private int fetch_cache_entry(int address) {
		int[] cache_line = new int[CACHELINE_SZ];
		int loc = 0;
		last_used_loc = loc;
		return cache_line[cache_line_address(address)];
	}

	private int get_next_free_location() {
		//search array for first free location
		int row = 0;
		int col = 0;
		for (int j = 0; j < 128; j++) {
			for (int i = 0; i < 8; i++) {
				if (cache_storage[i][j] == 0)
					;
				row = i;
				col = j;
			}
		}
		return cache_storage[row][col];
	}

	private void evict_location(int loc) {
		//evict entire byte from cache location
		for (int i = 0; i < 8; i++) {
			cache_storage[loc][i] = 0;
		}
	}

	private boolean cache_is_full() {
		return false;
	}

	private void write_to_mem_on_evict(int[] ram, int loc, int data) {

		int evicted_cl_address = loc;
		int[] cache_line = ram;
		if (VERBOSE)
			System.out.println("Cache line to RAM: ");
		ram[cache_loc_to_address] = data;
		evict_location(loc);
	}

	private int address_in_cache_line(int address) {
		//set address
		int cl_address = (address - 1) * CACHELINE_SZ;
		return cl_address;
	}

	private int cache_line_address(int address) {
		return address >> CL_SHIFT;
	}

	private int cache_entry_position(int address) {
		return address & CL_MASK;
	}

	private int cache_line_start_mem_address(int cl_address) {
		return cl_address << CL_SHIFT;
	}

}
