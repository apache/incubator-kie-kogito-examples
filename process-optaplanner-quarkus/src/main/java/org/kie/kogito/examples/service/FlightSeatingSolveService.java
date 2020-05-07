/**
 *  Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
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
