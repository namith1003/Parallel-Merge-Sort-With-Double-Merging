public class Merge {

    // in different threads Merge two subarrays into one sorted array on the parent thread

    public static void merge(long[] data, long[] aux, int start1, int start2, int last) {
        int leftIndex = start1;
        int rightIndex = start2;
        int mergedIndex = start1;
        while (leftIndex < start2 && rightIndex < last) {
            if (data[leftIndex] < data[rightIndex]) {
                aux[mergedIndex] = data[leftIndex];
                leftIndex++;
                mergedIndex++;
            } else {
                aux[mergedIndex] = data[rightIndex];
                rightIndex++;
                mergedIndex++;
            }
        }

        // Copy any remaining elements from the first sorted subarray
        while (leftIndex < start2) {
            aux[mergedIndex++] = data[leftIndex++];
        }

        // Copy any remaining elements from the second sorted subarray
        while (rightIndex < last) {
            aux[mergedIndex++] = data[rightIndex++];
        }

        // Copy the merged array back to the original data array
        System.arraycopy(aux, start1, data, start1, last - start1);
    }

    // Merge the smallest elements from two adjacent subarrays
    public static int mergeMinimum(long[] data, long[] aux, int currentThreadStart, int nextThreadStart, int last) {

        int currentThreadIndex = currentThreadStart;
        int nextThreadIndex = nextThreadStart;
        int minIndex = currentThreadStart;
        int elementsToMerge = (last - currentThreadStart) / 2;
        int counter = 0;

        // loops until the currentThreadsIndex exceeds the next threads start or the next thread reaches the end
        while (currentThreadIndex < nextThreadStart && nextThreadIndex < last && counter < elementsToMerge) {
            // it will pick which of the 2 threads have the smaller value
            if (data[currentThreadIndex] < data[nextThreadIndex]) {
                aux[minIndex] = data[currentThreadIndex];
                currentThreadIndex++;
                minIndex++;
            } else {
                aux[minIndex] = data[nextThreadIndex];
                nextThreadIndex++;
                minIndex++;
            }
            counter++;
        }

        // If there are no elements left in the second subarray of next thread, get elements from the first sorted subarray as they are already sorted
        while (currentThreadIndex < nextThreadStart && counter < elementsToMerge) {
            aux[minIndex++] = data[currentThreadIndex++];
            counter++;
        }

        // If there are no elements left in the first subarray, get elements from the second sorted subarray as they are already sorted
        while (nextThreadIndex < last && counter < elementsToMerge) {
            aux[minIndex++] = data[nextThreadIndex++];
            counter++;
        }

        return (minIndex - currentThreadStart);
    }

    // Merge the largest elements from two adjacent subarrays
    public static int mergeMaximum(long[] data, long[] aux, int currentThreadStart, int nextThreadStart, int last) {
        int currentThreadIndex = nextThreadStart - 1;
        int nextThreadIndex = last - 1;
        int maxIndex = last - 1;
        int elementsToMerge = (int) Math.ceil((last - currentThreadStart) / 2.0);
        int counter = 0;

        while (currentThreadIndex >= currentThreadStart && nextThreadIndex >= nextThreadStart && counter < elementsToMerge) {
            if (data[currentThreadIndex] > data[nextThreadIndex]) {
                aux[maxIndex] = data[currentThreadIndex];
                currentThreadIndex--;
                maxIndex--;
            } else {
                aux[maxIndex] = data[nextThreadIndex];
                nextThreadIndex--;
                maxIndex--;
            }
            counter++;
        }

        // If there are no elements left in the second subarray, get elements from the first sorted subarray
        while (currentThreadIndex >= currentThreadStart && counter < elementsToMerge) {
            aux[maxIndex--] = data[currentThreadIndex--];
            counter++;
        }

        // If there are no elements left in the first subarray, get elements from the second sorted subarray
        while (nextThreadIndex >= nextThreadStart && counter < elementsToMerge) {
            aux[maxIndex--] = data[nextThreadIndex--];
            counter++;
        }

        return (last - maxIndex - 1);
    }

    // Initialize an array with random long values using a seed
    public static void arrayInitialize(long[] array, int seed) {
        java.util.Random random = new java.util.Random(seed);
        for (int index = 0; index < array.length; index++) {
            array[index] = random.nextLong();
        }
    }
}
