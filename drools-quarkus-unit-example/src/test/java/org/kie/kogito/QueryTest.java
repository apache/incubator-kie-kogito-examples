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

import java.io.IOException;
import java.util.List;

import org.codehaus.jackson.map.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.kie.kogito.queries.AdultUnit;
import org.kie.kogito.queries.AdultUnitQueryFindAdultsEndpoint;
import org.kie.kogito.queries.AdultUnitRuleUnit;
import org.kie.kogito.queries.Person;

import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class QueryTest {

    @Test
    public void test() {

        org.kie.kogito.examples.Application application = new org.kie.kogito.examples.Application();

        AdultUnit adults = new AdultUnit();

        adults.getPersons().add(new Person( "Mario", 45 ));
        adults.getPersons().add(new Person( "Marilena", 47 ));
        adults.getPersons().add(new Person( "Sofia", 7 ));

        // {"adultAge":18,"persons":[{"name":"Mario","age":45,"adult":false},{"name":"Marilena","age":47,"adult":false},{"name":"Sofia","age":7,"adult":false}]}

        AdultUnitQueryFindAdultsEndpoint query = new AdultUnitQueryFindAdultsEndpoint(new AdultUnitRuleUnit());

        List<String> results = query.executeQuery( adults )
                .stream()
                .map( m -> m.get("$p") )
                .map( Person.class::cast )
                .map( Person::getName )
                .collect( toList() );

        assertEquals( 2, results.size() );
        assertTrue( results.containsAll( asList("Mario", "Marilena") ) );
    }
}
