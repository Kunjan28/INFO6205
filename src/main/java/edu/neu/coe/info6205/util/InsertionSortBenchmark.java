/*
  (c) Copyright 2018, 2019 Phasmid Software
 */
package edu.neu.coe.info6205.util;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Random;
import java.util.function.Supplier;

import edu.neu.coe.info6205.sort.BaseHelper;
import edu.neu.coe.info6205.sort.GenericSort;
import edu.neu.coe.info6205.sort.simple.InsertionSort;

public class InsertionSortBenchmark {

    public static void main(String[] args) throws IOException {
        
        InsertionSortBenchmark benchmark = new InsertionSortBenchmark();
        for(int i = 1000;i<=16000;i*=2) {
        	benchmark.sortIntegers(i);
        }
        
    }

   
    private void sortIntegers(final int n) {
    	logger.info("Begin Insertion sort for "+n+" integers");
        final Random random = new Random();
      
        BaseHelper<Integer> helper = new BaseHelper<>("InsertionSort", n);
        GenericSort<Integer> sorter = new InsertionSort<Integer>(helper);
        
        // sort RandomInteger[]
        //logger.info("Insertion sort for Random Integers");
        final Supplier<Integer[]> randomIntegers = () -> {
            Integer[] result = (Integer[]) Array.newInstance(Integer.class, n);
            for (int i = 0; i < n; i++) result[i] = random.nextInt();
            return result;
        };

        final double t1 = new Benchmark_Timer<Integer[]>(
                "randomIntegerArraysorter",
                (xs) -> Arrays.copyOf(xs, xs.length),
                (xs) -> sorter.sort(xs,0,xs.length),
                null
        ).runFromSupplier(randomIntegers, 100);
        for (TimeLogger timeLogger : timeLoggersQuadratic) timeLogger.log(t1, n);
        
        for (TimeLogger timeLogger : timeLoggersLinearithmic) timeLogger.log(t1, n);
        
     // sort OrderedInteger[]
        //logger.info("Insertion sort for Ordered Integers");
        final Supplier<Integer[]> orderedIntegers = () -> {
            Integer[] result = (Integer[]) Array.newInstance(Integer.class, n);
            for (int i = 0; i < n; i++) result[i] = i;
            return result;
        };

        final double t2 = new Benchmark_Timer<Integer[]>(
                "orderedIntegerArraysorter",
                (xs) -> Arrays.copyOf(xs, xs.length),
                (xs) -> sorter.sort(xs,0,xs.length),
                null
        ).runFromSupplier(orderedIntegers, 100);
        for (TimeLogger timeLogger : timeLoggersLinearithmic) timeLogger.log(t2, n);
        
        // sort reverseOrderedInteger[]
       // logger.info("Insertion sort for Reverse Ordered Integers");
        final Supplier<Integer[]> reverseOrdIntegers = () -> {
            Integer[] result = (Integer[]) Array.newInstance(Integer.class, n);
            for (int i = n-1; i >= 0; i--) result[i] = i;
            return result;
        };

        final double t3 = new Benchmark_Timer<Integer[]>(
                "reverseOrderedIntegerArraysorter",
                (xs) -> Arrays.copyOf(xs, xs.length),
                (xs) -> sorter.sort(xs,0,xs.length),
                null
        ).runFromSupplier(reverseOrdIntegers, 100);
        for (TimeLogger timeLogger : timeLoggersLinearithmic) timeLogger.log(t3, n);
        
     // sort partiallyOrderedInteger[]
        //logger.info("Insertion sort for Partially Ordered Integers");
        final Supplier<Integer[]> partiallyOrdIntegers = () -> {
            Integer[] result = (Integer[]) Array.newInstance(Integer.class, n);
            for (int i = 0,j=n/2; i < n/2 && j<n; i++,j++)
            {
            	result[i] = random.nextInt();
            	result[j] = j;
            }
            return result;
        };

        final double t4 = new Benchmark_Timer<Integer[]>(
                "partialOrderedIntegerArraysorter",
                (xs) -> Arrays.copyOf(xs, xs.length),
                (xs) -> sorter.sort(xs,0,xs.length),
                null
        ).runFromSupplier(partiallyOrdIntegers, 100);
        for (TimeLogger timeLogger : timeLoggersLinearithmic) timeLogger.log(t4, n);
        
    }


    /**
     * For mergesort, the number of array accesses is actually 6 times the number of comparisons.
     * That's because, in addition to each comparison, there will be approximately two copy operations.
     * Thus, in the case where comparisons are based on primitives,
     * the normalized time per run should approximate the time for one array access.
     */
    public final static TimeLogger[] timeLoggersLinearithmic = {
            new TimeLogger("Raw time per run (mSec): ", (time, n) -> time),
            new TimeLogger("Normalized time per run (n log n): ", (time, n) -> time / minComparisons(n) / 6 * 1e6)
    };

    final static LazyLogger logger = new LazyLogger(InsertionSortBenchmark.class);

    
    /**
     * This is based on log2(n!)
     *
     * @param n the number of elements.
     * @return the minimum number of comparisons possible to sort n randomly ordered elements.
     */
    static double minComparisons(int n) {
        double lgN = Utilities.lg(n);
        return n * (lgN - LgE) + lgN / 2 + 1.33;
    }

    /**
     * This is the mean number of inversions in a randomly ordered set of n elements.
     * For insertion sort, each (low-level) swap fixes one inversion, so on average there are this number of swaps.
     * The minimum number of comparisons is slightly higher.
     *
     * @param n the number of elements
     * @return one quarter n-squared more or less.
     */
    static double meanInversions(int n) {
        return 0.25 * n * (n - 1);
    }

   
    
    /**
     * For (basic) insertionsort, the number of array accesses is actually 6 times the number of comparisons.
     * That's because, for each inversions, there will typically be one swap (four array accesses) and (at least) one comparision (two array accesses).
     * Thus, in the case where comparisons are based on primitives,
     * the normalized time per run should approximate the time for one array access.
     */
    private final static TimeLogger[] timeLoggersQuadratic = {
            new TimeLogger("Raw time per run (mSec): ", (time, n) -> time),
            new TimeLogger("Normalized time per run (n^2): ", (time, n) -> time / meanInversions(n) / 6 * 1e6)
    };
 

   
    private static final double LgE = Utilities.lg(Math.E);

    
}
