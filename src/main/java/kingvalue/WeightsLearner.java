package kingvalue;

import kingvalue.search.Board;
import kingvalue.search.Move;
import kingvalue.search.Engine;

import java.util.*;

/**
 * Class containing the implementation of the heuristic optimization using a genetic algorithm.
 */
public class WeightsLearner {

    private static final int WEIGHT_NUM = 11;
    private static final int POPSIZE = 24;
    private static final int ITER_DIFF = 50;

    private static final int MMX_PARENTS = 3;
    private static final double FEE_A = -0.001;
    private static final double FEE_B = -0.5;
    private static final double FEE_C = 0.7;
    private static final double FEE_D = 0.25;

    private static final double MAX_LINES_WITHOUT_OUTPUT = 100000;

    /**
     * Executes the optimization process.
     */
    public static void main (String args[]) {

        learn();
    }

    /**
     * Implements the optimization process.
     */
    private static void learn() {

        Random rand = new Random();
        long time;

        List<Double> bestFit = new ArrayList<>();
        List<Double> avgFit = new ArrayList<>();

        System.out.println("Initializating population... [POPSIZE = " + POPSIZE + "]");
        Individual[] pop = initialization(rand);

        for (int i = 0; i < ITER_DIFF || avgFit.get(i - 1) > avgFit.get(i - ITER_DIFF); i++) {

            System.out.println("\nIteration " + (i + 1));
            time = System.currentTimeMillis();

            System.out.println("Selection...");
            Individual[] sPop = selection(pop, rand);
            System.out.println("Crossover...");
            Individual[] xPop = crossover(sPop, rand);
            System.out.println("Replacement...");
            pop = replacement(pop, xPop);

            time = System.currentTimeMillis() - time;
            System.out.println("Iteration completed in " + (time / 1000.0) + " seconds");

            double sum = 0;
            Individual best = pop[0];

            for (int j = 0; j < POPSIZE; j++) {

                sum += pop[j].eval;
                if (pop[j].eval > best.eval) best = pop[j];
            }

            System.out.println("Best fitness: " + best.eval + " (age " + best.age + ")");
            System.out.println("Avg. fitness: " + (sum / POPSIZE));

            bestFit.add(best.eval);
            avgFit.add(sum / POPSIZE);

            System.out.println("bestFit = " + Misc.listToString(bestFit));
            System.out.println("avgFit = " + Misc.listToString(avgFit));
        }
    }

    /**
     * Initializes the population.
     */
    private static Individual[] initialization(Random rand) {

        Individual[] pop = new Individual[POPSIZE];

        for (int i = 0; i < POPSIZE; i++) {
            double[] weights = randomWeights(rand);
            pop[i] = new Individual(weights, 0, 1);
            System.out.println(pop[i]);
        }

        return pop;
    }

    /**
     * Selects individuals for crossover.
     */
    private static Individual[] selection(Individual[] pop, Random rand) {

        Individual[] sPop = new Individual[POPSIZE / 2];

        ArrayList<Individual> pool = new ArrayList<Individual>(POPSIZE);
        pool.addAll(Arrays.asList(pop));
        Collections.shuffle(pool, rand);

        for (int i = 0; i < sPop.length; i++) {

            Individual ind1 = pool.get(2 * i);
            Individual ind2 = pool.get(2 * i + 1);

            if (ind1.eval < ind2.eval) sPop[i] = ind2;
            else sPop[i] = ind1;
        }

        return sPop;
    }

    /**
     * Generates new individuals using the crossover operator (MMX).
     */
    private static Individual[] crossover(Individual[] sPop, Random rand) {

        Individual[] xPop = new Individual[(sPop.length / MMX_PARENTS) * 2];

        for (int i = 0; i < sPop.length / MMX_PARENTS; i++) {

            double[] ch1 = new double[WEIGHT_NUM];
            double[] ch2 = new double[WEIGHT_NUM];

            Individual[] parents = new Individual[MMX_PARENTS];
            for (int j = 0; j < MMX_PARENTS; j++) parents[j] = sPop[MMX_PARENTS * i + j];

            double[][] intervals = xIntervals(parents);

            for (int j = 0; j < WEIGHT_NUM; j++) {

                ch1[j] = Math.min(1, Math.max(0,
                        intervals[j][0] + (intervals[j][1] - intervals[j][0]) * rand.nextDouble()));

                ch2[j] = Math.min(1, Math.max(0,
                        intervals[j][0] + intervals[j][1] - ch1[j]));
            }

            Misc.normalizeArray(ch1);
            xPop[2 * i] = new Individual(ch1, 0, 1);
            System.out.println(xPop[2 * i]);

            Misc.normalizeArray(ch2);
            xPop[2 * i + 1] = new Individual(ch2, 0, 1);
            System.out.println(xPop[2 * i + 1]);
        }

        return xPop;
    }

    /**
     * Computes the intervals for MMX.
     */
    private static double[][] xIntervals(Individual[] parents) {

        double[][] intervals = new double[WEIGHT_NUM][2];

        double[] fee = new double[WEIGHT_NUM];
        double[] min = new double[WEIGHT_NUM];
        double[] max = new double[WEIGHT_NUM];

        for (int i = 0; i < WEIGHT_NUM; i++) min[i] = 1;

        for (int i = 0; i < parents.length; i++) {

            double[] parent = parents[i].weights;

            for (int j = 0; j < WEIGHT_NUM; j++) {

                if (parent[j] < min[j]) min[j] = parent[j];
                if (parent[j] > max[j]) max[j] = parent[j];
            }
        }

        // Genetic diversity
        for (int i = 0; i < WEIGHT_NUM; i++) fee[i] = max[i] - min[i];

        for (int i = 0; i < WEIGHT_NUM; i++) {

            // Exploration-Exploitation function
            if (fee[i] < FEE_C) fee[i] = FEE_A + (fee[i] * ((FEE_B - FEE_A) / FEE_C));
            else fee[i] = ((fee[i] - FEE_C) * (FEE_D / (1 - FEE_C)));

            // Crossover intervals
            intervals[i][0] = min[i] + fee[i];
            intervals[i][1] = max[i] - fee[i];
        }

        return intervals;
    }

    /**
     * Selects the population for the next iteration.
     */
    private static Individual[] replacement(Individual[] pop, Individual[] xPop) {

        List<Individual> newPop = new ArrayList<>(pop.length + xPop.length);
        newPop.addAll(Arrays.asList(pop));
        newPop.addAll(Arrays.asList(xPop));

        tournament(newPop);

        newPop.sort(new IndividualComparator());
        return newPop.subList(0, pop.length).toArray(new Individual[0]);
    }

    private static void tournament(List<Individual> newPop) {

        //TODO
    }

    /**
     * Generates a random individual.
     */
    private static double[] randomWeights(Random rand) {

        double[] weights = new double[WEIGHT_NUM];
        for (int i = 0; i <weights.length; i++) weights[i] = rand.nextDouble();
        Misc.normalizeArray(weights);
        return weights;
    }
}