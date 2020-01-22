/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.kogito;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.kie.kogito.queries.Applicant;
import org.kie.kogito.queries.LoanApplication;
import org.kie.kogito.queries.LoanUnitDTO;
import org.kie.kogito.queries.LoanUnitQueryFindApprovedEndpoint;
import org.kie.kogito.queries.LoanUnitQueryFindNotApprovedIdAndAmountEndpoint;
import org.kie.kogito.queries.LoanUnitRuleUnit;

import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class QueryTest {

    @Test
    public void testApproved() {
        LoanUnitQueryFindApprovedEndpoint query = new LoanUnitQueryFindApprovedEndpoint(new LoanUnitRuleUnit());

        List<String> results = query.executeQuery(createLoanUnitDTO())
                                    .stream()
                                    .map(LoanApplication::getId)
                                    .collect(toList());

        assertEquals(2, results.size());
        assertTrue(results.containsAll(asList("ABC0001", "ABC0005")));
    }

    @Test
    public void testNotApproved() {
        LoanUnitQueryFindNotApprovedIdAndAmountEndpoint query = new LoanUnitQueryFindNotApprovedIdAndAmountEndpoint(new LoanUnitRuleUnit());

        List<LoanUnitQueryFindNotApprovedIdAndAmountEndpoint.Result> results = query.executeQuery(createLoanUnitDTO());

        assertEquals(3, results.size());

        Collections.sort(results, (r1, r2) -> r1.get$id().compareTo(r2.get$id()));

        assertEquals("ABC0002", results.get(0).get$id());
        assertEquals(1500, results.get(0).get$amount());

        assertEquals("ABC0003", results.get(1).get$id());
        assertEquals(4000, results.get(1).get$amount());

        assertEquals("ABC0004", results.get(2).get$id());
        assertEquals(8000, results.get(2).get$amount());
    }

    private LoanUnitDTO createLoanUnitDTO() {
        List<LoanApplication> loanApplications = new ArrayList<>();
        loanApplications.add(new LoanApplication("ABC0001", new Applicant("John", 45), 3000, 1200)); // approved with LargeDepositApprove
        loanApplications.add(new LoanApplication("ABC0002", new Applicant("Paul", 15), 1500, 500)); // rejected with NotAdultApplication
        loanApplications.add(new LoanApplication("ABC0003", new Applicant("Mary", 28), 4000, 300)); // rejected with SmallDepositReject
        loanApplications.add(new LoanApplication("ABC0004", new Applicant("George", 35), 8000, 3000)); // rejected with LargeDepositReject
        loanApplications.add(new LoanApplication("ABC0005", new Applicant("Lucy", 22), 1600, 200)); // approved with SmallDepositApprove

        LoanUnitDTO loanUnitDto = new LoanUnitDTO();
        loanUnitDto.setMaxAmount(5000);
        loanUnitDto.setLoanApplications(loanApplications);

        return loanUnitDto;
    }
}
