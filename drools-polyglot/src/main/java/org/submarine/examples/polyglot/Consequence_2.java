package org.submarine.examples.polyglot;

import org.drools.model.Variable;
import org.drools.model.consequences.ConsequenceBuilder.AbstractValidBuilder;
import org.drools.model.functions.Block2;

public class Consequence_2<A, B> extends AbstractValidBuilder<Consequence_2<A, B>> {

    public Consequence_2(Variable<A> decl1, Variable<B> decl2) {
        super(decl1, decl2);
    }

    public Consequence_2<A, B> execute(final Block2<A, B> block) {
        this.block = new Block2.Impl(block);
        return this;
    }
}
