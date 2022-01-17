package simpledb;

/** A class to represent a fixed-width histogram over a single integer-based field.
 */
public class IntHistogram {

    private int min;
    private int max;
    private int buckets;
    public int ntups;
    private int width;
    private int[] intHistogram;

    /**
     * Create a new IntHistogram.
     * 
     * This IntHistogram should maintain a histogram of integer values that it receives.
     * It should split the histogram into "buckets" buckets.
     * 
     * The values that are being histogrammed will be provided one-at-a-time through the "addValue()" function.
     * 
     * Your implementation should use space and have execution time that are both
     * constant with respect to the number of values being histogrammed.  For example, you shouldn't 
     * simply store every value that you see in a sorted list.
     * 
     * @param buckets The number of buckets to split the input value into.
     * @param min The minimum integer value that will ever be passed to this class for histogramming
     * @param max The maximum integer value that will ever be passed to this class for histogramming
     */
    public IntHistogram(int buckets, int min, int max) {
        this.min = min;
        this.max = max;
        this.buckets = buckets;
        width = (int) Math.ceil((double) (1+max-min)/buckets);
        ntups = 0;
        intHistogram = new int[buckets];
    }

//    private int valueToIndex(int v) {
//        if (v == max) {
//            return buckets - 1;
//        } else {
//            return (v - min) / width;
//        }
//    }

    /**
     * Add a value to the set of values that you are keeping a histogram of.
     * @param v Value to add to the histogram
     */
    public void addValue(int v) {
        int index;
        if (v == max) {
            index = buckets - 1;
        } else {
            index = (v - min) / width;
        }
        intHistogram[index]++;
        ntups++;
    }

    /**
     * Estimate the selectivity of a particular predicate and operand on this table.
     * 
     * For example, if "op" is "GREATER_THAN" and "v" is 5, 
     * return your estimate of the fraction of elements that are greater than 5.
     * 
     * @param op Operator
     * @param v Value
     * @return Predicted selectivity of this particular operator and value
     */
    public double estimateSelectivity(Predicate.Op op, int v) {
        int index;
        if (v == max) {
            index = buckets - 1;
        } else {
            index = (v - min) / width;
        }
        int height;
        int leftIndex = index * width + min;
        int rightIndex = leftIndex + width - 1;

        switch (op) {
            case EQUALS:
                if (v < min || v > max) {
                    return 0.0;
                } else {
                    return (intHistogram[index] * 1.0 / width) / ntups;
                }

            case GREATER_THAN:
                if (v < min) {
                    return 1.0;
                }
                if (v > max) {
                    return 0.0;
                }
                height = intHistogram[index];
//                double p1 = ((rightIndex - v) / width * 1.0) * (height * 1.0 / ntups);
                double indexPart = ( (rightIndex - v) / (double)width) * ((double) height / ntups);
                int rightPart = 0;
                for (int i = index + 1; i < buckets; i++) {
                    rightPart += intHistogram[i];
                }
                double fraction = (double) rightPart/ ntups;
                return indexPart + fraction;

            case LESS_THAN:
                if (v < min) {
                    return 0.0;
                }
                if (v > max) {
                    return 1.0;
                }
                height = intHistogram[index];
                double indexPart2 = ((v - leftIndex) / (double)width ) * ((double) height/ ntups);
                int leftPart = 0;
                for (int i = index - 1; i >= 0; i--) {
                    leftPart += intHistogram[i];
                }
                double fraction2 = leftPart * 1.0 / ntups;
                return indexPart2 + fraction2;

            case LESS_THAN_OR_EQ:
                return estimateSelectivity(Predicate.Op.LESS_THAN, v) + estimateSelectivity(Predicate.Op.EQUALS, v);

            case GREATER_THAN_OR_EQ:
                return estimateSelectivity(Predicate.Op.GREATER_THAN, v) + estimateSelectivity(Predicate.Op.EQUALS, v);

            case LIKE:
                return avgSelectivity();

            case NOT_EQUALS:
                return 1 - estimateSelectivity(Predicate.Op.EQUALS, v);

            default:
                throw new RuntimeException("Runtime exception");
        }
    }
    
    /**
     * @return
     *     the average selectivity of this histogram.
     *     
     *     This is not an indispensable method to implement the basic
     *     join optimization. It may be needed if you want to
     *     implement a more efficient optimization
     * */
    public double avgSelectivity()
    {
        // some code goes here
        return 1.0;
    }
    
    /**
     * @return A string describing this histogram, for debugging purposes
     */
    public String toString() {
        // some code goes here
        return null;
    }
}
