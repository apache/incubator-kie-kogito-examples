/*
 * Copyright 2005 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.kogito.queries;

import java.io.Serializable;

import org.kie.kogito.conf.SessionsPool;
import org.kie.kogito.rules.DataSource;
import org.kie.kogito.rules.DataStore;
import org.kie.kogito.rules.RuleUnitData;

@SessionsPool(1)
public class AdultUnit implements RuleUnitData, Serializable {
    private int adultAge;

    private DataStore<Person> persons;

    public AdultUnit( ) {
        this( DataSource.createStore() );
    }

    public AdultUnit( DataStore<Person> persons ) {
        this.persons = persons;
    }

    public DataStore<Person> getPersons() {
        return persons;
    }

    public void setPersons( DataStore<Person> persons ) {
        this.persons = persons;
    }

    public int getAdultAge() {
        return adultAge;
    }

    public void setAdultAge( int adultAge ) {
        this.adultAge = adultAge;
    }
}
