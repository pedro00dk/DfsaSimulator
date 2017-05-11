package estimator;

/**
 * Contains some math helper methods.
 *
 * @author Pedro Henrique
 */
public class MathUtils {

    /**
     * Prevents instantiation.
     */
    private MathUtils() {
    }

    public static double factorial(double n) {
        double factorial = 1;
        for (int i = 2; i < n; i++) {
            factorial *= i;
        }
        return factorial;
    }

    public static double arrangement(double n, double p) {
        double arrangement = 1;
        for (double i = n - p + 1; i < n; i++) {
            arrangement *= i;
        }
        return arrangement;
    }

    public static double factAndDiv(double dividend, double... divisors) {
        double max = -1;
        int maxIndex = -1;
        for (int i = 0; i < divisors.length; i++) {
            if (divisors[i] > max) {
                max = divisors[i];
                maxIndex = i;
            }
        }
        divisors[maxIndex] = 0;
        double acc = 1;
        for (double n = dividend; n > max; n--) {
            double next = n;
            for (double divisor : divisors) {
                if (divisor > 1) {
                    next /= divisor;
                }
            }
            acc *= next;
        }
        return acc;
    }
}
