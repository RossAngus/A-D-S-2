# Memory and Cache Line Addressing

In what follows we assume that `MEM_SZ` and `CACHELINE_SZ` are integer powers of 2 and that `MEM_SZ % CACHELINE_SZ == 0` .

### Main memory model

The main memory is a one-dimensional array:

        protected int[] ram = new int[MEM_SZ];

To access a value in the main memory, we index into the array:

        int address = ...;
        int value = ram[address];

### Cache lines

As explained, the cache does not store individual values from the main memory but _cache lines_ which are small fixed-sized arrays:

        int[] cache_line = new int[CACHELINE_SZ];

We can view the main memory as a contiguous list of cache lines. The address of the first element of the _k_ th cache line in main memory is:

        int cl_address = (k-1)*CACHELINE_SZ;

Obviously, the number of cache lines is `MEM_SZ/CACHELINE_SZ` and we can view the main memory logically as an array of cache lines. With that view, the address of the _k_ th cache line is `k-1`.

### Location of the cache line for a memory address

Given a main memory address, how do we know in which cache line it is located? In other words, what is the _cache line address_? As is clear from the above, we can do this by dividing the main memory address by `CACHELINE_SZ`.
We can do this most efficiently using a bitwise operation called right shift: `address >> m` shifts the integer _address_  _m_ bits to the right, i.e. we remove the first _m_ bits. This is the same as the integer division of _address_ by `2**m`. ( `**` is the to-the-power-of operator). To do this we define `CL_SHIFT` such that `CACHELINE_SZ == 2**CL_SHIFT`.

In the provided code this is done as follows:        

        Double cls = Math.log(CACHELINE_SZ)/Math.log(2);
        int CL_SHIFT = cls.intValue();

Now we can get the address of the cache line in which a given main memory address is located (which we call the  _cache line address_) as:

        int cache_line_address = address >> CL_SHIFT;

### Position of an entry inside a cache line

Furthermore, if we want to know the position of a main memory address _inside_ a cache line, we must calculate the index in the `cache_line` array based on the main memory address. We could do this by taking the modulo of the address:

        idx = address % CACHELINE_SZ;

However, the most efficient way is to use a bitwise AND operation (`&`) that returns only the first `CL_SHIFT` bits from the address. To do this we define `CL_MASK` as a number which in binary representation consists of `CL_SHIFT` ones:

        int CL_MASK = CACHELINE_SZ - 1;

(Recall that if `CACHELINE_SZ` is the _k_ th power of 2, in binary this means, reading from right to left, _k_ zeros followed by a 1. And that if we subtract one form this we get _k_ ones: 10000 - 1 = 1111.)

Now we can get the index inside the cache line for a given main memory address as:

        int cache_entry_position = address & CL_MASK;

### Main memory address from cache line address and position

Finally, given a cache line address `cl_address` and a cache entry position `cl_pos`, how do we calculate the corresponding main memory address? Quite simply: we shift `cl_address` `CL_SHIFT` bits to the left (using `<<`) and add `cl_pos`:

        cache_line_start_mem_address = cl_address << CL_SHIFT;
        address = cl_start_mem_address + cl_pos;

For the operation of the cache, we are normally only concerned with the main memory address corresponding to the start of the cache line, i.e. `cache_line_start_mem_address`.
