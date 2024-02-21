public class MergeSort {

    public static void mergeSort(long[] array, int first, int last) {
        if (first == last) {
            return;
        }
        int middle = (first + last) / 2;
        mergeSort(array, middle + 1, last);
        mergeSort(array, first, middle);
        Merge.merge(array, aux, first, middle + 1, last + 1);
    }

    // Auxiliary array for merging
    static long[] aux;

}

