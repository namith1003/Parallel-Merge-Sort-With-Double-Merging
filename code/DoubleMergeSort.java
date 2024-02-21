import java.util.Arrays;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

public class DoubleMergeSort extends Thread {
    private final int threadID;
    private final CyclicBarrier barrier;
    private final long[] array;
    private final long[] aux;
    private final int numberOfThreads;

    public DoubleMergeSort(int threadID, CyclicBarrier barrier, long[] array, long[] aux, int numberOfThreads) {
        super("thread " + threadID);
        this.threadID = threadID;
        this.barrier = barrier;
        this.array = array;
        this.aux = aux;
        this.numberOfThreads = numberOfThreads;
    }

    @Override
    public void run() {
        try {
            // the block size for each thread
            int blockSize = array.length / numberOfThreads;
            //finding the first and last element for each thread
            int first = threadID * blockSize;
            int last = first + blockSize;
            if (threadID == numberOfThreads - 1)
                last = array.length;

            // Each thread sorts its sub-array and waits for others at the barrier
            Arrays.sort(array, first, last);
            barrier.await();

            int numberOfBlocks = numberOfThreads;

            // Two threads for each pair of blocks. If there is an odd number of blocks,
            // the last one will not be merged, so no thread is necessary for that block
            int activeThreads = (numberOfBlocks % 2 == 0) ? numberOfBlocks : numberOfBlocks - 1;

            while (numberOfBlocks > 1) {
                // check if the thread ID is odd or even
                if (threadID < activeThreads && threadID % 2 == 0) {
                    //start index of the thread
                    int currentThreadStart = threadID * blockSize;
                    // start index of the next thread
                    int nextThreadStart = currentThreadStart + blockSize;
                    // last index of the next thread
                    int end = nextThreadStart + blockSize;
                    if (threadID + 2 == numberOfBlocks)
                        end = array.length;

                    // Merge the smallest elements from two adjacent blocks
                    int mergedElements = Merge.mergeMinimum(array, aux, currentThreadStart, nextThreadStart, end);
                    barrier.await();
                    // Copy back the merged block to the original array
                    System.arraycopy(aux, currentThreadStart, array, currentThreadStart, mergedElements);
                } else if (threadID < activeThreads) {
                    int currentThreadStart = (threadID - 1) * blockSize;
                    int nextThreadStart = currentThreadStart + blockSize;
                    int end = nextThreadStart + blockSize;
                    if (threadID + 1 == numberOfBlocks)
                        end = array.length;

                    // Merge the largest elements from two adjacent blocks
                    int mergedElements = Merge.mergeMaximum(array, aux, currentThreadStart, nextThreadStart, end);
                    barrier.await();
                    // Copy back the merged block to the original array
                    System.arraycopy(aux, end - mergedElements, array, end - mergedElements, mergedElements);
                } else {
                    // Idle looping threads wait to synchronize
                    barrier.await();
                }
                blockSize *= 2;
                // The number of blocks is rounded up since, if there's an odd number of blocks,
                // all consecutive block pairs are merged, but the last one is not merged
                numberOfBlocks = (int) Math.ceil(numberOfBlocks / 2.0);
                activeThreads = (numberOfBlocks % 2 == 0) ? numberOfBlocks : numberOfBlocks - 1;
                barrier.await();
            }

        } catch (InterruptedException | BrokenBarrierException ex) {
            System.out.println("Exception error message: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    public static void parallelMergeSort(long[] array, int numberOfThreads) {
        long[] aux = new long[array.length];

        // Create a barrier to synchronize threads
        CyclicBarrier barrier = new CyclicBarrier(numberOfThreads);

        DoubleMergeSort[] threads = new DoubleMergeSort[numberOfThreads];
        for (int i = 0; i < threads.length; i++) {
            threads[i] = new DoubleMergeSort(i, barrier, array, aux, numberOfThreads);
            threads[i].start();
        }

        // The main thread waits for the first thread to finish.
        // It could have waited for any other thread; all finish simultaneously.
        try {
            threads[0].join();
        } catch (InterruptedException ie) {
            ie.printStackTrace();
        }
    }
}
