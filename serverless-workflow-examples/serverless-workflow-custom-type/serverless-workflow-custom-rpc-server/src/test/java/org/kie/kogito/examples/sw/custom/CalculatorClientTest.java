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
package org.kie.kogito.examples.sw.custom;

import java.io.IOException;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.kie.kogito.examples.sw.custom.CalculatorClient.OperationId;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CalculatorClientTest {
    
    
    private static CalculatorServer server; 
    
    @BeforeAll 
    static void init() throws IOException
    {
        server = new CalculatorServer(8082);
    }
    
    
    @AfterAll static void cleanup () throws IOException{
        server.close();
    }
    
    @Test
    void testCalculator() throws IOException { 
        assertEquals(7,CalculatorClient.invokeOperation("localhost", 8082, OperationId.ADD, 4, 3));
        assertEquals(1,CalculatorClient.invokeOperation("localhost", 8082, OperationId.SUBTRACTION, 4, 3));
        assertEquals(12,CalculatorClient.invokeOperation("localhost", 8082, OperationId.MULTIPLICATION, 4, 3));
        assertEquals(1,CalculatorClient.invokeOperation("localhost", 8082, OperationId.DIVISION, 4, 3));
    }

}
