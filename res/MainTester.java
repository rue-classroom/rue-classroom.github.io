package SearchTestPkg;

import SplitScreen.*;
import Searcher.*;

public class MainTester extends Main {

    //CONSTANTS
    private static final int ITERATIONS = 1000;

    //IVARS
    private Searcher searcher;
    private MyCode mc;

    //CONSTRUCTORS

    //GETTERS

    //SETTERS

    //OTHER METHODS
    @Override
    public void runCommands() {
        this.searcher = new Searcher();
        this.mc = new MyCode();
        int[] array = buildTestArray();
        testMethodsForFunctionality(array);
        testMethodsForPerformance(array);
    }

    /* Builds an ordered array of type int, size 1000, by starting at 0 and randomly adding between 1 and 4 to
     * each element to get the next element
     */
    private int[] buildTestArray () {
        int[] array = new int[1000];
        int last = 0; //value of the last element
        for(int i = 0; i < array.length; i++) {
            int next = last + (int)(4 * Math.random()) + 1;  //randomly add between 1 and 4
            array[i] = next;
            last = next;
        }
        return array;
    }

    //tests whether or not the index found is correct for each search method
    private void testMethodsForFunctionality(int[] array) {

        //Sequential Search
        McObject sequential = new McObject() {
            @Override
            public int search(int[] array, int element) {
                return MainTester.this.mc.sequentialSearch(array, element);
            }
        };
        outputLine("Sequential Search:");
        testMcObject(sequential, array);
        outputLine(""); //space

        //Binary Search
        McObject binary = new McObject() {
            @Override
            public int search(int[] array, int element) {
                return MainTester.this.mc.binarySearch(array, element);
            }
        };
        outputLine("Binary Search:");
        testMcObject(binary, array);
        outputLine(""); //space

        //Custom Search
        McObject custom = new McObject() {
            @Override
            public int search(int[] array, int element) {
                return MainTester.this.mc.customSearch(array, element);
            }
        };
        outputLine("Custom Search:");
        testMcObject(custom, array);
        outputLine(""); //space
    }

    //tests the defined search method for accuracy by searching for min, max, and 15 random indexes between the two
    private void testMcObject(McObject mcObject, int[] array) {
        int lowest = array[0];
        int highest = array[array.length - 1];
        //search for min
        if(this.searcher.binarySearch(array, lowest) != mcObject.search(array, lowest)) {
            outputLine("Failed");
            outputLine("Searched for: " + lowest + ", Expected: " + this.searcher.binarySearch(array, lowest) +
                ", Received: " + mcObject.search(array, lowest));
            return;
        }
        //search for max
        if(this.searcher.binarySearch(array, highest) != mcObject.search(array, highest)) {
            outputLine("Failed");
            outputLine("Searched for: " + highest + ", Expected: " + this.searcher.binarySearch(array, highest) +
                    ", Received: " + mcObject.search(array, highest));
            return;
        }
        //search for 15 random elements between lowest and highest
        for(int i = 0; i < 15; i++) {
            int searchElement = random(lowest, highest);
            if(this.searcher.binarySearch(array, searchElement) != mcObject.search(array, searchElement)) {
                outputLine("Failed");
                outputLine("Searched for: " + searchElement + ", Expected: " +
                        this.searcher.binarySearch(array, searchElement) +
                        ", Received: " + mcObject.search(array, searchElement));
                return;
            }
        }
        outputLine("Passed");
    }

    //compares the execution time for each search method against a standard binary search algorithm
    private void testMethodsForPerformance(int[] array) {
        PerformanceTest ptSequential = new PerformanceTest() {
            @Override
            public void codeToExecute() {
                //Change this section to test searches of elements in different positions in the array
                int searchElement = getSearchElement(mc.position(), array);
                mc.sequentialSearch(array, searchElement);
            }
        };
        PerformanceTest ptBinary = new PerformanceTest() {
            @Override
            public void codeToExecute() {
                int searchElement = getSearchElement(mc.position(), array);
                mc.binarySearch(array, searchElement);
            }
        };
        PerformanceTest ptCustom = new PerformanceTest() {
            @Override
            public void codeToExecute() {
                int searchElement = getSearchElement(mc.position(), array);
                mc.customSearch(array, searchElement);
            }
        };
        PerformanceTest ptStandardBinary = new PerformanceTest() {
            @Override
            public void codeToExecute() {
                int searchElement = getSearchElement(mc.position(), array);
                MainTester.this.searcher.binarySearch(array, searchElement);
            }
        };
        outputLine("");  //space
        double percentage;
        percentage = testAgainst(ptSequential, ptStandardBinary);
        outputLine("Sequential Search time was " + percentage + "% of " + "Standard Binary Search time.");
        percentage = testAgainst(ptBinary, ptStandardBinary);
        outputLine("Binary Search time was " + percentage + "% of " + "Standard Binary Search time.");
        percentage = testAgainst(ptCustom, ptStandardBinary);
        outputLine("Custom Search time was " + percentage + "% of " + "Standard Binary Search time.");
    }

    //returns a randomized search element based on the Enum Pos (General Position in the array)
    private int getSearchElement(Pos position, int[] array) {
        switch(position) {
            case BEGINNING:
                return array[(int)(Math.random() * 20)]; //randomly pick one of the first 20 elements
            case END:
                return array[array.length - 1 - (int)(Math.random() * 20)];  //randomly pick one of the last 20 elements
            case MID_HIGH:
                return array[array.length / 2 + (int)(Math.random() * 20)];  //one of the first 20 elements above middle
            case MID_LOW:
                return array[array.length / 2 - (int)(Math.random() * 20)];  //one of the first 20 elements below middle
            case RANDOM:
                return array[(int)(Math.random() * array.length)];  //completely random element
        }
        throw new RuntimeException("Position " + position + " not found.");
    }

    //returns a random int between 'lowest' (inclusive) and 'highest' (inclusive)
    private int random(int lowest, int highest) {
        int range = highest - lowest + 1;
        return (int)(Math.random() * range) + lowest;
    }

    //returns the percentage (test1 avg time / test2 avg time) * 100
    private double testAgainst(PerformanceTest test1, PerformanceTest test2) {
        test1.run(ITERATIONS * 10);  //warm up
        test2.run(ITERATIONS * 10);

        //average of 5
        double percentageTotal = 0;
        for(int i = 0; i < 5; i++) {
            long test1Time = 0;
            long test2Time = 0;
            int runs = 100;
            for(int j = 0; j < runs; j++) {
                if(Math.random() < 0.5) {  //randomize the order
                    test1Time += test1.run(ITERATIONS);
                    test2Time += test2.run(ITERATIONS);
                } else {
                    test2Time += test2.run(ITERATIONS);
                    test1Time += test1.run(ITERATIONS);
                }
            }
            test1Time /= runs;  //average
            test2Time /= runs;  //average
            double percentage = (test1Time / (double)test2Time) * 100;
            percentageTotal += percentage;
        }
        percentageTotal /= 5;  //average of the 5 tests
        return (int)(percentageTotal * 100 + 0.5) / 100.0;  //round to 2 decimal places
    }

    //used to pass a definable search method into another method
    abstract class McObject {
        public abstract int search(int[] array, int element);
    }

} //END OF CLASS
