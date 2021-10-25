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

import javax.enterprise.context.ApplicationScoped;

import org.kie.kogito.rules.DataStore;
import org.kie.kogito.rules.RuleUnit;
import org.kie.kogito.rules.RuleUnitInstance;

import io.quarkus.vertx.ConsumeEvent;

@ApplicationScoped
public class TwoUnitsService {

    @javax.inject.Inject
    RuleUnit<ApplicantValidationUnit> applicantValidationUnit;

    @javax.inject.Inject
    RuleUnit<LoanUnit> loanUnit;

    @ConsumeEvent(value = "applicant-validation")
    public LoanApplication applicantValidation(LoanApplication loanApplication) {
        System.out.println("Bus : applicant-validation");
        DataStore<LoanApplication> loanApplications = Util.toDataStore(loanApplication);
        ApplicantValidationUnit applicantValidationUnitData = new ApplicantValidationUnit(loanApplications);
        RuleUnitInstance<ApplicantValidationUnit> applicantValidationUnitInstance = applicantValidationUnit.createInstance(applicantValidationUnitData);
        applicantValidationUnitInstance.fire();
        applicantValidationUnitInstance.dispose();
        return loanApplication;
    }

    @ConsumeEvent(value = "loan")
    public LoanApplication findApproved(LoanApplication loanApplication) {
        System.out.println("Bus : loan");
        DataStore<LoanApplication> loanApplications = Util.toDataStore(loanApplication);
        LoanUnit loanUnitData = new LoanUnit(loanApplications);
        RuleUnitInstance<LoanUnit> loanUnitInstance = loanUnit.createInstance(loanUnitData);
        List<LoanApplication> response = loanUnitInstance.executeQuery("FindApproved").stream().map(this::toResult).collect(Collectors.toList());
        loanUnitInstance.dispose();
        return response.isEmpty() ? null : response.get(0);
    }

    private LoanApplication toResult(Map<String, Object> tuple) {
        return (LoanApplication) tuple.get("$l");
    }
}
