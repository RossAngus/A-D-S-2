# Extra Info about the Coursework

## Questions to guide your design

The CPU will look in the cache for data that are initially contained in the main memory.

   - What happens when the CPU performs a read?
   - How are the data loaded into the cache?
   - At which location in the cache will the data be stored?
   - What happens when the cache is full?

The CPU will write data to main memory via the cache.

   - So what happens when the CPU performs a write?
   - If the CPU writes to the cache, how does the data get written to the main memory?
   - Does the data need to be written to the main memory?
   - At which location in the cache will the data be written?
   - What happens when the cache is full?

The cache is small and the main memory is large. So there are many more address locations in the main.

   - How do you know the cache is full?
   - How do you associate a location in the cache with a main memory address?


## Definition of terms

The list below is a summary of the terms used and defined in the handout

    - DRAM memory: "Dynamic Random-access memory", the main memory in a computer.
    - Cache: a small but fast memory
    - Cache hit: when the CPU reads from or writes to a memory address and the data is present in the cache
    - Cache miss: when the CPU reads from or writes to a memory address and the data is not present in the cache
    - Contiguous: A block of memory is contiguous if the memory addresses following one another with no gaps.
    - Cache line: the content of a contiguous, fixed-size block of memory. In other words, a small array of values from the main memory 
    - Cache location: and entry in the cache that stores a cache line
    - Eviction: remove data  (i.e. a cache line) from the cache and write it back to the main memory
    - LIFO eviction policy: evict data from the most recently used location
    - Most recently used: most recently accessed for either read or write.
    - Fully associative: In general, a data structure consisting of a set of pairs with no restriction on what is contained in each element of the pair. In particular for a cache: a cache which allows to store the content of any main memory address at any cache location.
    - Write-back policy: when the CPU writes a value to an address, it is writen to the cache
    - Write allocate policy: on a write miss, a cache line is loaded into the cache, followed by the write operation. 

## Memory model

We can model the DRAM memory and the storage part of the cache as 1-D arrays of fixed size