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
package org.acme.travels;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class Traveller {

    @NotBlank(message = "First name must be provided")
    @Size(min = 2, max = 30)
    private String firstName;

    @NotBlank(message = "Last name must be provided.")
    @Size(min = 2, max = 30)
    private String lastName;

    @NotNull(message = "Email address must be provided.")
    @Email
    private String email;

    @NotBlank(message = "Nationality can not be blank.")
    private String nationality;

    @NotNull(message = "Address can not be null.")
    private Address address;

    @NotBlank(message = "Passport Number can not be blank.")
    private String passportNumber;

    public Traveller() {

    }

    public Traveller(String firstName, String lastName, String email, String nationality, String passportNumber, Address address) {
        super();
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.nationality = nationality;
        this.passportNumber = passportNumber;
        this.address = address;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNationality() {
        return nationality;
    }

    public void setNationality(String nationality) {
        this.nationality = nationality;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public String getPassportNumber() {
        return passportNumber;
    }

    public void setPassportNumber(String passportNumber) {
        this.passportNumber = passportNumber;
    }

    @Override
    public String toString() {
        return "Traveller [firstName=" + firstName + ", lastName=" + lastName + ", email=" + email + ", nationality="
                + nationality + ", address=" + address + ", passportNumber=" + passportNumber + "]";
    }

}
