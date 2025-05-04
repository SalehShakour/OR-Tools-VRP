package org.vrp;

import com.google.ortools.Loader;
import com.google.ortools.constraintsolver.*;
import com.google.protobuf.Duration;

public class TspCities implements ProblemRunner {

    public static class DataModel {
        public final long[][] distanceMatrix = {
                {0, 548, 776, 696, 582, 274, 502, 194, 308, 194, 536, 502, 388, 354, 468, 776, 662},
                {548, 0, 684, 308, 194, 502, 730, 354, 696, 742, 1084, 594, 480, 674, 1016, 868, 1210},
                {776, 684, 0, 992, 878, 502, 274, 810, 468, 742, 400, 1278, 1164, 1130, 788, 1552, 754},
                {696, 308, 992, 0, 114, 650, 878, 502, 844, 890, 1232, 514, 628, 822, 1164, 560, 1358},
                {582, 194, 878, 114, 0, 536, 764, 388, 730, 776, 1118, 400, 514, 708, 1050, 674, 1244},
                {274, 502, 502, 650, 536, 0, 228, 308, 194, 240, 582, 776, 662, 628, 514, 1050, 708},
                {502, 730, 274, 878, 764, 228, 0, 536, 194, 468, 354, 1004, 890, 856, 514, 1278, 480},
                {194, 354, 810, 502, 388, 308, 536, 0, 342, 388, 730, 468, 354, 320, 662, 742, 856},
                {308, 696, 468, 844, 730, 194, 194, 342, 0, 274, 388, 810, 696, 662, 320, 1084, 514},
                {194, 742, 742, 890, 776, 240, 468, 388, 274, 0, 342, 536, 422, 388, 274, 810, 468},
                {536, 1084, 400, 1232, 1118, 582, 354, 730, 388, 342, 0, 878, 764, 730, 388, 1152, 354},
                {502, 594, 1278, 514, 400, 776, 1004, 468, 810, 536, 878, 0, 114, 308, 650, 274, 844},
                {388, 480, 1164, 628, 514, 662, 890, 354, 696, 422, 764, 114, 0, 194, 536, 388, 730},
                {354, 674, 1130, 822, 708, 628, 856, 320, 662, 388, 730, 308, 194, 0, 342, 422, 536},
                {468, 1016, 788, 1164, 1050, 514, 514, 662, 320, 274, 388, 650, 536, 342, 0, 764, 194},
                {776, 868, 1552, 560, 674, 1050, 1278, 742, 1084, 810, 1152, 274, 388, 422, 764, 0, 798},
                {662, 1210, 754, 1358, 1244, 708, 480, 856, 514, 468, 354, 844, 730, 536, 194, 798, 0},
        };
        public final int vehicleNumber = 1;
        public final int depot = 0;
    }


    private String getSolutionString(
            RoutingModel routing, RoutingIndexManager manager, Assignment solution) {
        StringBuilder result = new StringBuilder();
        result.append("Objective: ").append(solution.objectiveValue()).append(" miles\n");
        result.append("Route:\n");
        long routeDistance = 0;
        long index = routing.start(0);
        while (!routing.isEnd(index)) {
            result.append(manager.indexToNode(index)).append(" -> ");
            long previousIndex = index;
            index = solution.value(routing.nextVar(index));
            routeDistance += routing.getArcCostForVehicle(previousIndex, index, 0);
        }
        result.append(manager.indexToNode(routing.end(0))).append("\n");
        result.append("Route distance: ").append(routeDistance).append(" miles\n");
        return result.toString();
    }

    @Override
    public String run(String[] args,
                      FirstSolutionStrategy.Value firstSolutionStrategy,
                      LocalSearchMetaheuristic.Value localSearch) {
        Loader.loadNativeLibraries();
        final DataModel data = new DataModel();

        RoutingIndexManager manager =
                new RoutingIndexManager(data.distanceMatrix.length, data.vehicleNumber, data.depot);

        RoutingModel routing = new RoutingModel(manager);

        final int transitCallbackIndex =
                routing.registerTransitCallback((long fromIndex, long toIndex) -> {
                    int fromNode = manager.indexToNode(fromIndex);
                    int toNode = manager.indexToNode(toIndex);
                    return data.distanceMatrix[fromNode][toNode];
                });

        routing.setArcCostEvaluatorOfAllVehicles(transitCallbackIndex);

        RoutingSearchParameters searchParameters;
        if (localSearch != null) {
            searchParameters = main.defaultRoutingSearchParameters()
                    .toBuilder()
                    .setFirstSolutionStrategy(firstSolutionStrategy)
                    .setLocalSearchMetaheuristic(localSearch)
                    .setTimeLimit(Duration.newBuilder().setSeconds(15).build())
                    .build();
        } else {
            searchParameters = main.defaultRoutingSearchParameters()
                    .toBuilder()
                    .setFirstSolutionStrategy(firstSolutionStrategy)
                    .setTimeLimit(Duration.newBuilder().setSeconds(15).build())
                    .build();
        }

        Assignment solution = routing.solveWithParameters(searchParameters);

        if (solution != null) {
            return getSolutionString(routing, manager, solution);
        } else {
            return "No solution found.";
        }
    }

    @Override
    public String getName() {
        return "TSP Cities";
    }
}
