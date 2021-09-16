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

import org.kie.kogito.examples.division.a.LoanDivisionAUnit;
import org.kie.kogito.examples.division.b.LoanDivisionBUnit;
import org.kie.kogito.examples.model.LoanApplication;
import org.kie.kogito.rules.DataStore;
import org.kie.kogito.rules.RuleUnit;
import org.kie.kogito.rules.RuleUnitInstance;

@Path("/custom")
public class CustomQueryFindApprovedEndpoint {

    @javax.inject.Inject
    RuleUnit<org.kie.kogito.examples.LoanPreprocessUnit> preprocessRuleUnit;

    @javax.inject.Inject
    RuleUnit<org.kie.kogito.examples.division.a.LoanDivisionAUnit> divisionARuleUnit;

    @javax.inject.Inject
    RuleUnit<org.kie.kogito.examples.division.b.LoanDivisionBUnit> divisionBRuleUnit;

    @javax.inject.Inject
    RuleUnit<org.kie.kogito.examples.LoanQueryUnit> queryRuleUnit;

    //    @javax.inject.Inject
    //    Application application;

    public CustomQueryFindApprovedEndpoint() {
    }

    public CustomQueryFindApprovedEndpoint(RuleUnit<org.kie.kogito.examples.LoanPreprocessUnit> preprocessRuleUnit) {
        this.preprocessRuleUnit = preprocessRuleUnit;
    }

    @POST()
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public List<org.kie.kogito.examples.model.LoanApplication> executeQuery(org.kie.kogito.examples.LoanPreprocessUnit unitDTO) {
        // Loan preprocess
        RuleUnitInstance<org.kie.kogito.examples.LoanPreprocessUnit> preprocessInstance = preprocessRuleUnit.createInstance(unitDTO);
        preprocessInstance.fire();
        preprocessInstance.dispose();
        DataStore<LoanApplication> loanApplications = unitDTO.getLoanApplications();

        // custom logic to choose the next RuleUnit
        String division = unitDTO.getDivision();
        if (division.equals("DivisionA")) {
            // get LoanDivisionAUnit instance
            System.out.println("DivisionA");
            LoanDivisionAUnit unitData = new LoanDivisionAUnit(loanApplications);
            RuleUnitInstance<LoanDivisionAUnit> divisionAInstance = divisionARuleUnit.createInstance(unitData);
            divisionAInstance.fire();
            divisionAInstance.dispose();
        } else if (division.equals("DivisionB")) {
            // get LoanDivisionBUnit instance
            System.out.println("DivisionB");
            LoanDivisionBUnit unitData = new LoanDivisionBUnit(loanApplications);
            RuleUnitInstance<LoanDivisionBUnit> divisionBInstance = divisionBRuleUnit.createInstance(unitData);
            divisionBInstance.fire();
            divisionBInstance.dispose();
        }

        // query finally
        LoanQueryUnit unitData = new LoanQueryUnit(loanApplications);
        RuleUnitInstance<LoanQueryUnit> queryInstance = queryRuleUnit.createInstance(unitData);
        List<org.kie.kogito.examples.model.LoanApplication> response = queryInstance.executeQuery("FindApproved").stream().map(this::toResult).collect(Collectors.toList());
        queryInstance.dispose();

        return response;
    }

    private org.kie.kogito.examples.model.LoanApplication toResult(Map<String, Object> tuple) {
        return (org.kie.kogito.examples.model.LoanApplication) tuple.get("$l");
    }
}
