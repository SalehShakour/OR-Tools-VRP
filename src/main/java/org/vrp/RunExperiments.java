package org.vrp;

import com.google.ortools.Loader;
import com.google.ortools.constraintsolver.FirstSolutionStrategy;
import com.google.ortools.constraintsolver.LocalSearchMetaheuristic;

import java.io.FileWriter;
import java.io.PrintWriter;
import java.time.Duration;
import java.time.Instant;

public class RunExperiments {
    public static void main(String[] args) throws Exception {
        Loader.loadNativeLibraries();
        PrintWriter writer = new PrintWriter(new FileWriter("experiment_results.txt"));

        ProblemRunner[] problems = {
                new TspCities(),
                new VrpGlobalSpan(),
                new VrpCapacity(),
                new VrpPickupDelivery(),
                new VrpTimeWindows()
        };

        FirstSolutionStrategy.Value[] firstStrategies = {
                FirstSolutionStrategy.Value.PATH_CHEAPEST_ARC,
                FirstSolutionStrategy.Value.PARALLEL_CHEAPEST_INSERTION,
                FirstSolutionStrategy.Value.SAVINGS,
                FirstSolutionStrategy.Value.LOCAL_CHEAPEST_INSERTION
        };

        LocalSearchMetaheuristic.Value[] localStrategies = {
                null,
                LocalSearchMetaheuristic.Value.GUIDED_LOCAL_SEARCH,
                LocalSearchMetaheuristic.Value.TABU_SEARCH,
                LocalSearchMetaheuristic.Value.SIMULATED_ANNEALING
        };

        for (ProblemRunner problem : problems) {
            for (FirstSolutionStrategy.Value first : firstStrategies) {
                for (LocalSearchMetaheuristic.Value local : localStrategies) {
                    writer.println("------------------------------------------------");
                    writer.println("Problem: " + problem.getName());
                    writer.println("First Solution Strategy: " + first);
                    writer.println("Local Search Strategy: " + (local == null ? "None" : local));

                    Instant start = Instant.now();
                    String result = problem.run(args, first, local);
                    Instant end = Instant.now();
                    Duration timeElapsed = Duration.between(start, end);

                    writer.println("Execution Time: " + timeElapsed.toMillis() + " ms");
                    writer.println("Result Summary:\n" + result);
                    writer.flush();
                }
            }
        }

        writer.close();
        System.out.println("Experiments completed and results saved to experiment_results.txt");
    }
}
