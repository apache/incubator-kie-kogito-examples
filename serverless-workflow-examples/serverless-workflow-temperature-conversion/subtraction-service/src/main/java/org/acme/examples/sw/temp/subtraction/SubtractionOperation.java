/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.acme.examples.sw.temp.subtraction;

import java.util.Objects;

import io.quarkus.runtime.annotations.RegisterForReflection;

/**
 * See: <a href="https://en.wikipedia.org/wiki/Subtraction">Subtraction</a>
 */
@RegisterForReflection
public class SubtractionOperation {

    private float leftElement;
    private float rightElement;

    public SubtractionOperation() {
    }

    public SubtractionOperation(final float leftElement, final float rightElement) {
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
        SubtractionOperation subtractionOperation = (SubtractionOperation) o;
        return Float.compare(subtractionOperation.leftElement, leftElement) == 0 && Float.compare(subtractionOperation.rightElement, rightElement) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(leftElement, rightElement);
    }
}
