/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kie.kogito.examples;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.kie.kogito.rules.DataStore;
import org.kie.kogito.rules.RuleUnit;
import org.kie.kogito.rules.RuleUnitInstance;

@Path("/2units")
public class TwoUnitsEndpoint {

    @javax.inject.Inject
    RuleUnit<ApplicantValidationUnit> applicantValidationUnit;

    @javax.inject.Inject
    RuleUnit<LoanUnit> loanUnit;

    public TwoUnitsEndpoint() {
    }

    @POST()
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public List<org.kie.kogito.examples.LoanApplication> executeQuery(org.kie.kogito.examples.ApplicantValidationUnit unitDTO) {
        // 1. Execute ApplicantValidationUnit
        RuleUnitInstance<ApplicantValidationUnit> applicantValidationUnitInstance = applicantValidationUnit.createInstance(unitDTO);
        applicantValidationUnitInstance.fire();
        applicantValidationUnitInstance.dispose();

        // 2. Execute LoanUnit
        DataStore<LoanApplication> loanApplications = unitDTO.getLoanApplications();
        LoanUnit loanUnitData = new LoanUnit(loanApplications);
        RuleUnitInstance<LoanUnit> loanUnitInstance = loanUnit.createInstance(loanUnitData);
        List<LoanApplication> response = loanUnitInstance.executeQuery("FindApproved").stream().map(this::toResult).collect(Collectors.toList());
        loanUnitInstance.dispose();

        return response;
    }

    private LoanApplication toResult(Map<String, Object> tuple) {
        return (LoanApplication) tuple.get("$l");
    }
}
