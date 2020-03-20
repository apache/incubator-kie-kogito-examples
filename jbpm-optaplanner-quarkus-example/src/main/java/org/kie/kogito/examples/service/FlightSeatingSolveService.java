package org.kie.kogito.examples.service;

import java.util.concurrent.ExecutionException;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Qualifier;

import org.kie.kogito.examples.domain.Flight;
import org.kie.kogito.process.Process;
import org.kie.kogito.process.impl.Sig;
import org.optaplanner.core.api.solver.SolverJob;
import org.optaplanner.core.api.solver.SolverManager;

@ApplicationScoped
public class FlightSeatingSolveService {

    @Inject
    SolverManager<Flight, String> solverManager;

    @Inject
    @Named("flights")
    Process<?> process;

    public void assignSeats(String id, Flight problem) {
        SolverJob<Flight, String> solverJob = solverManager.solveAndListen(id, (problemId) -> problem, (bestSolution) -> {
            process.instances().findById(id).ifPresent(pi -> {
                pi.send(Sig.of("newSolution", bestSolution));
            });
        });

        // TODO: Workaround for https://issues.redhat.com/browse/PLANNER-1868
        // Wait for solving to finish in new thread (so we don't block the return)
        new Thread(() -> {
            try {
                Flight finalBestSolution = solverJob.getFinalBestSolution();
                process.instances().findById(id).ifPresent(pi -> {
                    pi.send(Sig.of("solvingTerminated", finalBestSolution));
                });
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }).start();
    }
}
