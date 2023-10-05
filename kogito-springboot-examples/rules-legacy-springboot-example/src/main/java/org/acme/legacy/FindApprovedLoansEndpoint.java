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
package org.acme.legacy;

import java.util.ArrayList;
import java.util.List;

import org.kie.api.runtime.KieRuntimeBuilder;
import org.kie.api.runtime.KieSession;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/find-approved")
public class FindApprovedLoansEndpoint {

    private final KieRuntimeBuilder kieRuntimeBuilder;

    public FindApprovedLoansEndpoint(KieRuntimeBuilder kieRuntimeBuilder) {
        this.kieRuntimeBuilder = kieRuntimeBuilder;
    }

    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public List<LoanApplication> executeQuery(@RequestBody(required = true) LoanDto loanDto) {
        KieSession session = kieRuntimeBuilder.newKieSession();

        List<LoanApplication> approvedApplications = new ArrayList<>();
        session.setGlobal("approvedApplications", approvedApplications);
        session.setGlobal("maxAmount", loanDto.getMaxAmount());

        loanDto.getLoanApplications().forEach(session::insert);
        session.fireAllRules();

        return approvedApplications;
    }
}
