package ads2.cw1;

/**
 * Created by wim on 28/11/2017.
 * Your implementation of the cache must follow this interface
 */

interface Cache {

    // Read a value from a given main memory address location
    int read(int address,int[] ram,Status status);
    // Write a value to a given main memory address location
    void write(int address,int data, int[] ram,Status status);
    // Flush means remove all data from the cache and write it back to memory
    void flush(int[] ram,Status status);

    // Interfaces don't have constructors
//    Cache(int cacheSize, int cacheLineSize);

}
