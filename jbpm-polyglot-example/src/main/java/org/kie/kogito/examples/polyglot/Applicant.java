/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.kogito.examples.polyglot;

public class Applicant {

    private String fname;
    private String lname;
    private int age;
    private boolean valid;
    private long price;

    public String getFname() {
        return fname;
    }

    public void setFname(String fname) {
        this.fname = fname;
    }

    public String getLname() {
        return lname;
    }

    public void setLname(String lname) {
        this.lname = lname;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public void setAge(String age) {
        this.age = Integer.parseInt(age);
    }

    public boolean isValid() {
        return valid;
    }

    public void setValid(boolean valid) {
        this.valid = valid;
    }

    public long getPrice() {
        return price;
    }

    public void setPrice(long price) {
        this.price = price;
    }

    public Applicant(String fname, String lname, int age, boolean valid, long price) {
        this.fname = fname;
        this.lname = lname;
        this.age = age;
        this.valid = valid;
        this.price = price;
    }

    public Applicant() {

    }

    @Override
    public String toString() {
        return "Applicant{" + "fname='" + fname + '\'' + ", lname='" + lname + '\'' + ", age=" + age + ", valid="
                + valid + ", price=" + price + '}';
    }
}