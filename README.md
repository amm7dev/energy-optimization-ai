# Energy Optimization Using Local Search

Artificial Intelligence university project developed at the **Facultat d'Informàtica de Barcelona (FIB), Universitat Politècnica de Catalunya (UPC)**.

## Overview

This project addresses an energy distribution optimization problem involving power plants and consumers.

The objective is to find efficient assignments of consumers to power plants while considering:

- Power plant capacity constraints.
- Energy transmission losses based on distance.
- Operating costs.
- Guaranteed and non-guaranteed consumers.

## Algorithms

The project explores local search techniques using the AIMA framework:

- **Hill Climbing**
- **Simulated Annealing**

The implementation includes state representation, successor generation, valid operators and heuristic evaluation.

## Technologies

- Java
- AIMA (Artificial Intelligence: A Modern Approach)

## Project Structure

- `src/` — Java source code.
- `lib/` — External libraries (not distributed).

## Compilation and Execution

**Requirements:**

- Java JDK
- `AIMA.jar`
- `CentralEnergia.jar`

Place both libraries inside the `lib/` directory.

Run the following commands from `src/` on Linux or macOS.

**Compile:**

```bash
javac -cp ".:../lib/*" *.java
```

**Execute:**

```bash
java -cp ".:../lib/*" Main
```

**Clean compiled files:**

```bash
rm -f *.class
```

## Configuration

The simulation can be configured directly from the `Main.java` file, allowing users to experiment with different problem instances and search strategies.

The following parameters can be customized:

| Parameter | Description |
|---|---|
| **Number of clients** | Total number of energy consumers in the simulation. |
| **Number of power plants** | Total number of power plants available. |
| **Proportions of client types** | Proportion between 0 and 1 for each type of client. |
| **Proportions of plant types** | Proportion between 0 and 1 for each type of power plant. |
| **Proportion of guaranteed clients** | Proportion between 0 and 1 for guaranteed clients. |
| **Random seed** | Seed used for generating reproducible problem instances. |
| **Initial solution strategy** | Strategy used to generate the initial state before optimization. -> Modify the EstrategiaInicial value in the estadoInicial construction. |
| **Search algorithm** | Choice between Hill Climbing and Simulated Annealing. -> Modify cercaHC. |

To modify these parameters, open `src/Main.java`, adjust the desired values, and recompile the project before execution.

This configuration allows users to compare different initial solutions, evaluate search algorithm performance, and analyze how problem size affects optimization results.

## Dependencies

The energy simulation library is provided through the university course materials and is not included in this repository.

## Academic Context

This repository showcases the implementation of search algorithms and optimization techniques in Java.
Developed as part of the Artificial Intelligence course at FIB-UPC.

This repository showcases the implementation of search algorithms and optimization techniques in Java.
