/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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
package org.acme.flexible.example.model;

public class Product {

    private String name;
    private String family;

    public Product() {
    }

    public Product(Product product) {
        this.name = product.name;
        this.family = product.family;
    }

    @Override
    public String toString() {
        return "Product{" + "name='" + name + '\'' + ", family='" + family + '\'' + '}';
    }

    public String getName() {
        return name;
    }

    public Product setName(String name) {
        this.name = name;
        return this;
    }

    public String getFamily() {
        return family;
    }

    public Product setFamily(String family) {
        this.family = family;
        return this;
    }
}