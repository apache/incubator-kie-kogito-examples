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
package org.kie.kogito.examples.polyglot;

import org.drools.model.Declaration;
import org.drools.model.Rule;
import org.drools.model.impl.ModelImpl;
import org.drools.modelcompiler.builder.KieBaseBuilder;
import org.kie.api.runtime.KieSession;

import static org.drools.model.DSL.on;
import static org.drools.model.PatternDSL.declarationOf;
import static org.drools.model.PatternDSL.pattern;
import static org.drools.model.PatternDSL.rule;

public class Main {

    public static void main(String[] args) {
        Declaration<Person> markV = declarationOf(Person.class);
        Declaration<Person> olderV = declarationOf(Person.class);

        Rule r = rule("X is older than Mark").build(
                pattern(markV)
                        .expr(p -> p.getName().equals("Mark")),
                pattern(olderV)
                        .expr(p -> !p.getName().equals("Mark"))
                        .expr(markV, (p1, p2) -> p1.getAge() > p2.getAge()),
                on(olderV, markV)
                        .execute((p1, p2) ->
                                         System.out.println(
                                                 p1.getName() + " is older than " + p2.getName() )));

        ModelImpl m = new ModelImpl().addRule(r);
        KieSession s = KieBaseBuilder.createKieBaseFromModel(m).newKieSession();
        s.insert(new Person("Mark", 37));
        s.insert(new Person("Edson", 35));
        s.insert(new Person("Mario", 40));

        s.fireAllRules();
    }
}
