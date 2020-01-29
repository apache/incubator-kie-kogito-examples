package org.kie.kogito.examples.service;

import java.util.concurrent.ExecutionException;

import org.kie.kogito.examples.domain.Flight;
import org.optaplanner.core.api.solver.SolverJob;
import org.optaplanner.core.api.solver.SolverManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FlightSeatingSolveService {

    @Autowired
    private SolverManager<Flight, Long> solverManager;

    public Flight assignSeats(Flight problem) {
        SolverJob<Flight, Long> solverJob = solverManager.solve(1L, problem);
        try {
            return solverJob.getFinalBestSolution();
        } catch (InterruptedException | ExecutionException e) {
            throw new IllegalStateException("Solving failed.", e);
        }
    }

}
