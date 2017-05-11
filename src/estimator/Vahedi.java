package estimator;

import simulator.SimulationResult;

/**
 * Chen estimator implementation.
 */
public class Vahedi implements Estimator {

    /**
     * The estimator initial frame size.
     */
    private final int initialFrameSize;

    /**
     * Initializes the Chen with the received initial frame size.
     *
     * @param initialFrameSize the initial frame size
     */
    public Vahedi(int initialFrameSize) {
        this.initialFrameSize = initialFrameSize;
    }

    @Override
    public String toString() {
        return getName() + " i=" + initialFrameSize;
    }

    @Override
    public Type getType() {
        return Type.SIMPLE_DFSA;
    }

    @Override
    public int initialFrameSize() {
        return initialFrameSize;
    }

    @Override
    public int nextFrameSize(int idle, int success, int collision) {

        // If has no collisions return 0
        if (collision == 0) {
            return 0;
        }

        double i = idle;
        double s = success;
        double c = collision;

        double l = i + s + c;
        double n = s + c * 2;

        double next = 0;
        double previous = -1;

        // See P1
        double notI_LpowN = Math.pow(1 - i / l, n);
        // See P2
        double arrNS = MathUtils.arrangement(n, s);
        double LIpowN = Math.pow(l - i, n);
        double LISpowNS = Math.pow(l - i - s, n - s);
        // See P3
        double CpowNS = Math.pow(c, n - s);

        double fatL_fatIfatSfatC = MathUtils.factAndDiv(l, i, s, c);

        while (previous < next) {

            /*
             * Calculating P1 optimized
             *
             * notI_LpowN = (1-(i/l))^n << outside loops (fast recalculation)
             * p1 = (1-(i/l))^n
             *    = notI_LpowN
             *
             * -- fast recalculation
             * notI_LpowN = notI_Lpow(N-1) * notI_L
             */
            double p1 = notI_LpowN;
            notI_LpowN *= 1 - i / l;

            /*
             * Calculating P2 optimized
             *
             * arrNS = arr(n s) << outside loops (fast recalculation)
             * LIpowN = (l-e)^n << outside loops (fast recalculation)
             * LISpowNS = ('l-i-s')^(n-s) << outside loops (fast recalculation)
             *
             * p2 = 'comb(n s)'*(((l-e-s)^(n-s))/((l-e)^n))*'s!'
             *    = '(n!/((n-s)!*s!))*s!'*(((l-e-s)^(n-s))/((l-e)^n))
             *    = '(n!/(n-s)!)'*(((l-e-s)^(n-s))/((l-e)^n))
             *    = 'arr(n s)'*('((l-e-s)^(n-s)')/('(l-e)^n)')
             *    = arrNS*LIpowN*LISpowNS
             *
             * -- fast recalculation
             * arrNS = arr(N-1)S*(n/(n-s))
             * LIpowN = LIpow(N-1)*(l-i)
             * LISpowNS = LISpow(N-1)S*(l-i-s) ----- (n-s always positive)
             */
            double p2 = arrNS * LISpowNS / LIpowN;
            arrNS *= n / (n - s);
            LIpowN *= l - i;
            LISpowNS *= (l - i - s);

            /*
             * Calculating P3 optimized
             *
             * func facDiv(dividend, divisors...) => dividend!/(divisors[0]!*..divisors[n]!) (optimized)
             *
             * neg1powKV = -1^(k+v) << inside loop 2
             * fatNS_fatNSK = facDiv(n-s,n-s-k) << inside loop 1 (more precise and equally faster than fatNS done outside the loops in fatNS/fatNSK)
             * fatK = k! << inside loop 1
             * fatC_fatKfatCKVfatV = c!/((c-k)!*(k!'*(c-k-v!)*v!)) << inside loop 2
             *                     = c!/((c-k)!*(fatK*(c-k-v!)*v!))
             * CKVpowNSK = (c-k-v)^(n-s-k)
             * CpowNS = c^(n-s) = (l-i-s)^(n-s) = LISpowNS << outside loops (fast recalculation) (See P2) (l-i-s == c)
             * CKVpowNSK_CpowNS = (c-k-v)^(n-s-k)/c(n-s) << inside loop 2
             *                  = ckvPOWnsk / CpowNS
             *
             * p3 = 0
             * for k=0..c << loop 1
             *   for v=0..c-k << loop 2
             *     p3 = p3+(-1^(k+v))*'comb(c k)'*'comb(c-k v)'*((n-s)!/(n-s-k)!)*((c-k-v)^(n-s-k)/c(n-s))
             *        = p3+(-1^(k+v))*(c!/('(c-k)!*'k!))*('c-k!'/((c-k-v!)*v!))*((n-s)!/(n-s-k)!)*((c-k-v)^(n-s-k)/c(n-s))
             *        = p3+(-1^(k+v))*(c!/((c-k)!*(k!*(c-k-v!)*v!)))*((n-s)!/(n-s-k)!)*((c-k-v)^(n-s-k)/c(n-s))
             *        = p3+neg1powKV*fatC_fatKfatCKVfatV*fatNS_fatNSK*
             */
            double p3 = 0;
            for (double k = 0; k <= c; k++) {
                double fatK = MathUtils.factorial(k);
                double fatNS_fatNSK = MathUtils.factAndDiv(n - s, n - s - k);
                for (double v = 0; v <= c - k; k++) {
                    simulationResult.iterations++;

                    double neg1powKV = Math.pow(-1, k + v);
                    double fatC_fatKfatCKVfatV = MathUtils.factAndDiv(c, c - k - v, v) / fatK;
                    double CKVpowNSK = Math.pow(c - k - v, n - s - k);
                    double CKVpowNSK_CpowNS = CKVpowNSK / CpowNS;
                    p3 += neg1powKV * fatC_fatKfatCKVfatV * fatNS_fatNSK * CKVpowNSK_CpowNS;
                }
            }
            CpowNS *= c;

            previous = next;
            n++;

            next = fatL_fatIfatSfatC * Math.pow(p1, i) * Math.pow(p2, s) * Math.pow(p3, c);
        }
        int nextFrameSize = (int) (n - 2) - success;
        return nextFrameSize > 0 ? nextFrameSize : 2;
    }

    /**
     * Saves the simulation result internally to count the number of iterations.
     */
    private SimulationResult simulationResult;

    @Override
    public void setSimulationResult(SimulationResult simulationResult) {
        this.simulationResult = simulationResult;
    }

    @Override
    public Estimator copy() {
        return new Vahedi(initialFrameSize);
    }
}
