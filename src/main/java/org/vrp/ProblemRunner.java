package org.vrp;

import com.google.ortools.constraintsolver.FirstSolutionStrategy;
import com.google.ortools.constraintsolver.LocalSearchMetaheuristic;

public interface ProblemRunner {
    String run(String[] args, FirstSolutionStrategy.Value first, LocalSearchMetaheuristic.Value local);
    String getName();
}

