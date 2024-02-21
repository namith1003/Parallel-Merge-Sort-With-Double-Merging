import java.text.DecimalFormat;
import java.util.Arrays;

public class TestCases {

    static int availableCores = Runtime.getRuntime().availableProcessors();
    static int testIterations = 7;
    static int[] arraySizes = {10000000, 20000000, 30000000, 40000000, 50000000};
    static double[] avgDurationsSequential = new double[arraySizes.length];
    static double[] avgDurationsParallel = new double[arraySizes.length];
    static double[] avgDurationsMerge = new double[arraySizes.length];
    static double[] avgDurationsDoubleMerge = new double[arraySizes.length];

    public static void main(String[] args) {

        for (int i = 0; i < arraySizes.length; i++) {
            runSortingBenchmark(arraySizes[i], i);
        }

        System.out.println("Available Cores: " + availableCores);
        System.out.println("Array Sizes \tSequential Sort \tParallel Sort \t Merge Sort \tDouble Merge Sort");

        for (int i = 0; i < avgDurationsSequential.length; i++) {
            System.out.println(arraySizes[i] + "\t\t" + avgDurationsSequential[i] + "\t\t\t\t" + avgDurationsParallel[i] + "\t\t\t\t" + avgDurationsMerge[i] + "\t\t\t" + avgDurationsDoubleMerge[i]);
        }
    }


    public static void runSortingBenchmark(int arraySize, int setNo) {
        long[] array = new long[arraySize];
        long[] parallelArray = new long[arraySize];
        long[] mergedArray = new long[arraySize];
        long[] doubleMergedArray = new long[arraySize];

        long[] durationsSequential = new long[testIterations];
        long[] durationsParallel = new long[testIterations];
        long[] durationsMerge = new long[testIterations];
        long[] durationsDoubleMerge = new long[testIterations];

        long startTime, duration;

        for (int i = 0; i < testIterations; i++) {
            // initialize arrays
            Merge.arrayInitialize(array, arraySize+10+i);
            Merge.arrayInitialize(parallelArray, arraySize+20+i);
            Merge.arrayInitialize(mergedArray, arraySize+20+i);
            Merge.arrayInitialize(doubleMergedArray, arraySize+20+i);

            startTime = System.currentTimeMillis();
            Arrays.sort(array); // system sequential sort
            duration = System.currentTimeMillis() - startTime;
            durationsSequential[i] = duration;

            startTime = System.currentTimeMillis();
            Arrays.parallelSort(parallelArray); // system parallel sort
            duration = System.currentTimeMillis() - startTime;
            durationsParallel[i] = duration;

            startTime = System.currentTimeMillis();
            MergeSort.aux = new long[mergedArray.length];
            MergeSort.mergeSort(mergedArray, 0, mergedArray.length - 1); // parallel sort with double merging
            duration = System.currentTimeMillis() - startTime;
            durationsMerge[i] = duration;

            startTime = System.currentTimeMillis();
            DoubleMergeSort.parallelMergeSort(doubleMergedArray, availableCores); // parallel sort with double merging
            duration = System.currentTimeMillis() - startTime;
            durationsDoubleMerge[i] = duration;
        }

        long sumSequential = 0, sumParallel = 0, sumMerge = 0, sumDoubleMerge = 0;

        for (int i = 0; i < testIterations; i++) {
            sumSequential += durationsSequential[i];
            sumParallel += durationsParallel[i];
            sumMerge += durationsMerge[i];
            sumDoubleMerge += durationsDoubleMerge[i];
        }

        DecimalFormat decimalFormat = new DecimalFormat("#.##");

        avgDurationsSequential[setNo] = Double.parseDouble(decimalFormat.format((double) sumSequential / testIterations));
        avgDurationsDoubleMerge[setNo] = Double.parseDouble(decimalFormat.format((double) sumParallel / testIterations));
        avgDurationsMerge[setNo] = Double.parseDouble(decimalFormat.format((double) sumMerge / testIterations));
        avgDurationsParallel[setNo] = Double.parseDouble(decimalFormat.format((double) sumDoubleMerge / testIterations));
    }
}
