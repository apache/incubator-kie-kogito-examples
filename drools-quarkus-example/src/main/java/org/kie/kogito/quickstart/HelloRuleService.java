/**
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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
package org.kie.kogito.quickstart;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Named;

import org.kie.kogito.rules.units.SessionData;
import org.kie.kogito.rules.units.SessionUnit;

@ApplicationScoped
public class HelloRuleService {

    @Named("simpleKS")
    SessionUnit ruleUnit;

    public String run() {

        Result result = new Result();
        Person mark = new Person("Mark", 37);
        Person edson = new Person("Edson", 35);
        Person mario = new Person("Mario", 40);

        SessionData data = new SessionData();
        data.add(result);
        data.add(mark);
        data.add(edson);
        data.add(mario);

        ruleUnit.evaluate(data);

        return result.toString();
    }
}