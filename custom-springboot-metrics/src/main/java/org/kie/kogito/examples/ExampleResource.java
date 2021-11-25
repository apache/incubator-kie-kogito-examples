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
package org.kie.kogito.examples;

import java.util.LinkedList;
import java.util.NoSuchElementException;

import org.kie.kogito.monitoring.MonitoringRegistryManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tags;
import io.micrometer.core.instrument.Timer;

@RestController()
public class ExampleResource {

    private MeterRegistry registry;

    LinkedList<Long> list = new LinkedList<>();

    // Update the constructor to create the gauge
    @Autowired
    public void init(MonitoringRegistryManager monitoringRegistryManager) {
        registry = monitoringRegistryManager.getDefaultMeterRegistry();
        registry.gaugeCollectionSize("example.list.size", Tags.empty(), list);
    }

    @GetMapping("example/gauge/{number}")
    public Long checkListSize(@PathVariable("number") long number) {
        if (number == 2 || number % 2 == 0) {
            // add even numbers to the list
            list.add(number);
        } else {
            // remove items from the list for odd numbers
            try {
                number = list.removeFirst();
            } catch (NoSuchElementException nse) {
                number = 0;
            }
        }
        return number;
    }

    @GetMapping("example/prime/{number}")
    public String checkIfPrime(@PathVariable("number") long number) {
        if (number < 1) {
            return "Only natural numbers can be prime numbers.";
        }
        if (number == 1 || number == 2 || number % 2 == 0) {
            return number + " is not prime.";
        }

        if (testPrimeNumber(number)) {
            return number + " is prime.";
        } else {
            return number + " is not prime.";
        }
    }

    @GetMapping("example/taggedprime/{number}")
    public String checkIfTaggedPrime(@PathVariable("number") long number) {
        if (number < 1) {
            registry.counter("example.prime.number", "type", "not-natural").increment();
            return "Only natural numbers can be prime numbers.";
        }
        if (number == 1) {
            registry.counter("example.prime.number", "type", "one").increment();
            return number + " is not prime.";
        }
        if (number == 2 || number % 2 == 0) {
            registry.counter("example.prime.number", "type", "even").increment();
            return number + " is not prime.";
        }

        if (testPrimeNumber(number)) {
            registry.counter("example.prime.number", "type", "prime").increment();
            return number + " is prime.";
        } else {
            registry.counter("example.prime.number", "type", "not-prime").increment();
            return number + " is not prime.";
        }
    }

    protected boolean testPrimeNumber(long number) {
        // Count the number of times we test for a prime number
        registry.counter("example.prime.number.tested").increment();
        Timer timer = registry.timer("example.prime.number.test");
        return timer.record(() -> {
            for (int i = 3; i < Math.floor(Math.sqrt(number)) + 1; i = i + 2) {
                if (number % i == 0) {
                    return false;
                }
            }
            return true;
        });
    }
}
