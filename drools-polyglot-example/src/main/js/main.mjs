import { declarationOf, Rule, Pattern, on, Model, Session } from './drools'

const Person = Java.type('org.kie.kogito.examples.polyglot.Person');
const markV = declarationOf( Person.class );
const olderV = declarationOf( Person.class );

const r = Rule("X is older than Mark").build(
  Pattern(markV)
        .expr(p => p.getName() ===  "Mark"),
  Pattern(olderV)
        .expr(p => p.getName() !== "Mark")
        .expr(markV, (p1, p2) => p1.getAge() > p2.getAge()),
        on(olderV, markV).execute((p1, p2) => console.log( p1.getName() + " is older than " + p2.getName())));

const m = Model(r);
const s = Session(m);

console.log("start")

s.insert(new Person("Mark", 37));
s.insert(new Person("Edson", 35));
s.insert(new Person("Mario", 40));

s.fireAllRules();

console.log("end")

