package org.vrp;

import com.google.ortools.Loader;
import com.google.ortools.constraintsolver.*;
import com.google.protobuf.Duration;

import java.util.logging.Logger;

public class VrpTimeWindows implements ProblemRunner {
    private static final Logger logger = Logger.getLogger(VrpTimeWindows.class.getName());

    static class DataModel {
        public final long[][] timeMatrix = {
                {0, 6, 9, 8, 7, 3, 6, 2, 3, 2, 6, 6, 4, 4, 5, 9, 7},
                {6, 0, 8, 3, 2, 6, 8, 4, 8, 8, 13, 7, 5, 8, 12, 10, 14},
                {9, 8, 0, 11, 10, 6, 3, 9, 5, 8, 4, 15, 14, 13, 9, 18, 9},
                {8, 3, 11, 0, 1, 7, 10, 6, 10, 10, 14, 6, 7, 9, 14, 6, 16},
                {7, 2, 10, 1, 0, 6, 9, 4, 8, 9, 13, 4, 6, 8, 12, 8, 14},
                {3, 6, 6, 7, 6, 0, 2, 3, 2, 2, 7, 9, 7, 7, 6, 12, 8},
                {6, 8, 3, 10, 9, 2, 0, 6, 2, 5, 4, 12, 10, 10, 6, 15, 5},
                {2, 4, 9, 6, 4, 3, 6, 0, 4, 4, 8, 5, 4, 3, 7, 8, 10},
                {3, 8, 5, 10, 8, 2, 2, 4, 0, 3, 4, 9, 8, 7, 3, 13, 6},
                {2, 8, 8, 10, 9, 2, 5, 4, 3, 0, 4, 6, 5, 4, 3, 9, 5},
                {6, 13, 4, 14, 13, 7, 4, 8, 4, 4, 0, 10, 9, 8, 4, 13, 4},
                {6, 7, 15, 6, 4, 9, 12, 5, 9, 6, 10, 0, 1, 3, 7, 3, 10},
                {4, 5, 14, 7, 6, 7, 10, 4, 8, 5, 9, 1, 0, 2, 6, 4, 8},
                {4, 8, 13, 9, 8, 7, 10, 3, 7, 4, 8, 3, 2, 0, 4, 5, 6},
                {5, 12, 9, 14, 12, 6, 6, 7, 3, 3, 4, 7, 6, 4, 0, 9, 2},
                {9, 10, 18, 6, 8, 12, 15, 8, 13, 9, 13, 3, 4, 5, 9, 0, 9},
                {7, 14, 9, 16, 14, 8, 5, 10, 6, 5, 4, 10, 8, 6, 2, 9, 0},
        };
        public final long[][] timeWindows = {
                {0, 5}, {7, 12}, {10, 15}, {16, 18}, {10, 13}, {0, 5},
                {5, 10}, {0, 4}, {5, 10}, {0, 3}, {10, 16}, {10, 15},
                {0, 5}, {5, 10}, {7, 8}, {10, 15}, {11, 15},
        };
        public final int vehicleNumber = 4;
        public final int depot = 0;
    }

    private String printSolution(DataModel data, RoutingModel routing, RoutingIndexManager manager, Assignment solution) {
        StringBuilder result = new StringBuilder();
        RoutingDimension timeDimension = routing.getMutableDimension("Time");
        long totalTime = 0;
        for (int i = 0; i < data.vehicleNumber; ++i) {
            if (!routing.isVehicleUsed(solution, i)) continue;
            long index = routing.start(i);
            result.append("Route for Vehicle ").append(i).append(":\n");
            StringBuilder route = new StringBuilder();
            while (!routing.isEnd(index)) {
                IntVar timeVar = timeDimension.cumulVar(index);
                route.append(manager.indexToNode(index))
                        .append(" Time(").append(solution.min(timeVar)).append(",")
                        .append(solution.max(timeVar)).append(") -> ");
                index = solution.value(routing.nextVar(index));
            }
            IntVar timeVar = timeDimension.cumulVar(index);
            route.append(manager.indexToNode(index))
                    .append(" Time(").append(solution.min(timeVar)).append(",")
                    .append(solution.max(timeVar)).append(")");
            result.append(route).append("\n");
            result.append("Time of the route: ").append(solution.min(timeVar)).append("min\n");
            totalTime += solution.min(timeVar);
        }
        result.append("Total time of all routes: ").append(totalTime).append("min\n");
        return result.toString();
    }

    @Override
    public String run(String[] args, FirstSolutionStrategy.Value firstSolutionStrategy, LocalSearchMetaheuristic.Value localSearch) {
        Loader.loadNativeLibraries();
        DataModel data = new DataModel();
        RoutingIndexManager manager = new RoutingIndexManager(data.timeMatrix.length, data.vehicleNumber, data.depot);
        RoutingModel routing = new RoutingModel(manager);

        final int transitCallbackIndex = routing.registerTransitCallback((long fromIndex, long toIndex) -> {
            int fromNode = manager.indexToNode(fromIndex);
            int toNode = manager.indexToNode(toIndex);
            return data.timeMatrix[fromNode][toNode];
        });

        routing.setArcCostEvaluatorOfAllVehicles(transitCallbackIndex);

        routing.addDimension(transitCallbackIndex, 30, 30, false, "Time");
        RoutingDimension timeDimension = routing.getMutableDimension("Time");

        for (int i = 1; i < data.timeWindows.length; ++i) {
            long index = manager.nodeToIndex(i);
            timeDimension.cumulVar(index).setRange(data.timeWindows[i][0], data.timeWindows[i][1]);
        }
        for (int i = 0; i < data.vehicleNumber; ++i) {
            long index = routing.start(i);
            timeDimension.cumulVar(index).setRange(data.timeWindows[0][0], data.timeWindows[0][1]);
            routing.addVariableMinimizedByFinalizer(timeDimension.cumulVar(routing.start(i)));
            routing.addVariableMinimizedByFinalizer(timeDimension.cumulVar(routing.end(i)));
        }

        RoutingSearchParameters.Builder parametersBuilder =
                main.defaultRoutingSearchParameters().toBuilder()
                        .setFirstSolutionStrategy(firstSolutionStrategy);
        if (localSearch != null) {
            parametersBuilder.setLocalSearchMetaheuristic(localSearch)
                    .setTimeLimit(Duration.newBuilder().setSeconds(10).build())
            ;
        }

        Assignment solution = routing.solveWithParameters(parametersBuilder.build());
        if (solution == null) return "No solution found.";
        return printSolution(data, routing, manager, solution);
    }

    @Override
    public String getName() {
        return "VrpTimeWindows";
    }
}
