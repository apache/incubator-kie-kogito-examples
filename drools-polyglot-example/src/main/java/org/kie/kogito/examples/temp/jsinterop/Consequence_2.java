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
package org.kie.kogito.examples.temp.jsinterop;

import org.drools.model.Variable;
import org.drools.model.consequences.ConsequenceBuilder.AbstractValidBuilder;
import org.drools.model.functions.Block2;

// we temporarily address an interop issue with this shim
public class Consequence_2<A, B> extends AbstractValidBuilder<Consequence_2<A, B>> {

    public Consequence_2(Variable<A> decl1, Variable<B> decl2) {
        super(decl1, decl2);
    }

    public Consequence_2<A, B> execute(final Block2<A, B> block) {
        this.block = new Block2.Impl(block);
        return this;
    }
}
