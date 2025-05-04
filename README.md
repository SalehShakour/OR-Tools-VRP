# Vehicle Routing Problem Solver with Google OR-Tools (Java)

## Overview

This repository is part of a Bachelor's thesis project that focuses on solving a variety of **Vehicle Routing Problems (VRPs)** using **Google OR-Tools** in Java. The goal is to systematically test different strategies across multiple routing problem types and log their performance.

The project uses a centralized runner class (`RunExperiments.java`) to automate the evaluation of combinations of **First Solution Strategies** and **Local Search Metaheuristics** for a broad range of classical and constrained VRP variants.

## Problem Types Implemented

The following VRP problem types are modeled and evaluated in this project:

1. **Traveling Salesman Problem (TSP)**
2. **Vehicle Routing Problem (VRP)** – minimizing Global Span
3. **Capacitated VRP (CVRP)**
4. **VRP with Pickup and Delivery (PDP)**
5. **VRP with Time Windows (VRPTW)**

Each problem type implements the shared interface `ProblemRunner`, allowing for a modular and extensible experiment structure.

## Core File: `RunExperiments.java`

This class automates the execution of all defined problems using all combinations of the following strategies:

### First Solution Strategies:

- `AUTOMATIC`
- `PATH_CHEAPEST_ARC`
- `PATH_MOST_CONSTRAINED_ARC`
- `EVALUATOR_STRATEGY`
- `SAVINGS`
- `SWEEP`
- `CHRISTOFIDES`
- `ALL_UNPERFORMED`
- `BEST_INSERTION`
- `PARALLEL_CHEAPEST_INSERTION`
- `LOCAL_CHEAPEST_INSERTION`
- `GLOBAL_CHEAPEST_ARC`
- `LOCAL_CHEAPEST_ARC`
- `FIRST_UNBOUND_MIN_VALUE`

### Local Search Metaheuristics:

- `AUTOMATIC`
- `GREEDY_DESCENT`
- `GUIDED_LOCAL_SEARCH`
- `SIMULATED_ANNEALING`
- `TABU_SEARCH`
- `GENERIC_TABU_SEARCH`

### Output:

Each experiment result is automatically saved to a file named after the problem (e.g., `TspCities.txt`, `VrpCapacity.txt`), and includes:

- The selected strategies
- Execution time (ms)
- Solution summary returned by the `run()` method


> **Requirements:**
> - Java 21
> - Maven
> - Google OR-Tools for Java


