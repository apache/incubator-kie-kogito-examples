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
import java.util.List;

import org.junit.jupiter.api.Test;
import org.kie.kogito.queries.AdultUnitDTO;
import org.kie.kogito.queries.AdultUnitQueryFindAdultsEndpoint;
import org.kie.kogito.queries.AdultUnitQueryFindAdultNamesEndpoint;
import org.kie.kogito.queries.AdultUnitQueryFindNotAdultNamesAndAgeEndpoint;
import org.kie.kogito.queries.AdultUnitRuleUnit;
import org.kie.kogito.queries.Person;

import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class QueryTest {

    @Test
    public void testPersons() {
        AdultUnitQueryFindAdultsEndpoint query = new AdultUnitQueryFindAdultsEndpoint(new AdultUnitRuleUnit());

        List<String> results = query.executeQuery( createAdultUnitDTO() )
                .stream()
                .map( Person::getName )
                .collect( toList() );

        assertEquals( 2, results.size() );
        assertTrue( results.containsAll( asList("Mario", "Marilena") ) );
    }

    @Test
    public void testNames() {
        AdultUnitQueryFindAdultNamesEndpoint query = new AdultUnitQueryFindAdultNamesEndpoint(new AdultUnitRuleUnit());

        List<String> results = query.executeQuery( createAdultUnitDTO() );

        assertEquals( 2, results.size() );
        assertTrue( results.containsAll( asList("Mario", "Marilena") ) );
    }

    @Test
    public void testResult() {
        AdultUnitQueryFindNotAdultNamesAndAgeEndpoint query = new AdultUnitQueryFindNotAdultNamesAndAgeEndpoint(new AdultUnitRuleUnit());

        List<AdultUnitQueryFindNotAdultNamesAndAgeEndpoint.Result> results = query.executeQuery( createAdultUnitDTO() );

        assertEquals( 1, results.size() );
        AdultUnitQueryFindNotAdultNamesAndAgeEndpoint.Result result = results.get(0);
        assertEquals( "Sofia", result.get$name() );
        assertEquals( 7, result.get$age() );
    }

    private AdultUnitDTO createAdultUnitDTO() {
        List<Person> persons = new ArrayList<>();
        persons.add( new Person( "Mario", 45 ) );
        persons.add( new Person( "Marilena", 47 ) );
        persons.add( new Person( "Sofia", 7 ) );

        AdultUnitDTO adultsDTO = new AdultUnitDTO();
        adultsDTO.setAdultAge(18);
        adultsDTO.setPersons(persons);

        return adultsDTO;
    }
}
