/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.kie.kogito.examples.sw.temp.multiplication;

import java.util.Objects;

import io.quarkus.runtime.annotations.RegisterForReflection;

/**
 * See: <a href="https://en.wikipedia.org/wiki/Multiplication">Multiplication</a>
 */
@RegisterForReflection
public class MultiplicationOperation {

    private float leftElement;
    private float rightElement;

    public MultiplicationOperation() {
    }

    public MultiplicationOperation(final float leftElement, final float rightElement) {
        this.leftElement = leftElement;
        this.rightElement = rightElement;
    }

    public float getLeftElement() {
        return leftElement;
    }

    public void setLeftElement(float leftElement) {
        this.leftElement = leftElement;
    }

    public float getRightElement() {
        return rightElement;
    }

    public void setRightElement(float rightElement) {
        this.rightElement = rightElement;
    }

    @Override
    public String toString() {
        return "Operation{" +
                "leftElement=" + leftElement +
                ", rightElement=" + rightElement +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        MultiplicationOperation operation = (MultiplicationOperation) o;
        return Float.compare(operation.leftElement, leftElement) == 0 && Float.compare(operation.rightElement, rightElement) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(leftElement, rightElement);
    }
}
