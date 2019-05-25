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
