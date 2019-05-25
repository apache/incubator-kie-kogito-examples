const ModelImpl = Java.type('org.drools.model.impl.ModelImpl');
const DSL = Java.type('org.drools.model.DSL');
const declarationOf = DSL.declarationOf;
const PatternDSL = Java.type('org.drools.model.PatternDSL');
const Rule = PatternDSL.rule;
const Pattern = PatternDSL.pattern;
const on = (v1, v2) => new (Java.type('org.kie.kogito.examples.temp.jsinterop.Consequence_2'))(v1,v2);
const KieBaseBuilder = Java.type('org.drools.modelcompiler.builder.KieBaseBuilder');

function Session(model) {
    return KieBaseBuilder.createKieBaseFromModel( model ).newKieSession()
}

function Model(...ms) {
    const model = new ModelImpl();
    for (const m of ms) {
        model.addRule(m)
    }
    return model
}

export {
    Model, Session, Rule, Pattern, on, declarationOf
}