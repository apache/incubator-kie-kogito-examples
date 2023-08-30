/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
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
package org.acme.examples.sw.custom;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class CalculatorClient {
    
    public enum OperationId {
        ADD (1),
        SUBTRACTION(2),
        MULTIPLICATION(3),
        DIVISION(4),
        UNKNOWN(-1);
        
        
        private final byte opCode;
        
        private OperationId(int opCode) {
            this.opCode = (byte)opCode;
        }

        public byte getOpCode() {
            return opCode;
        }

    }
    
    public static int invokeOperation (String host, int port, OperationId operationId, int value1, int value2) throws IOException {
        
        try (Socket socket = new Socket(host,port)) 
        {
            DataOutputStream out = new DataOutputStream (socket.getOutputStream());
            out.writeByte(operationId.getOpCode());
            out.writeInt(value1);
            out.writeInt(value2);
        
            DataInputStream in = new DataInputStream(socket.getInputStream());
            switch (in.readByte()) {
                case 0: 
                    return in.readInt();
                case -1:
                    throw new UnsupportedOperationException(operationId.toString());
                case -2: 
                    throw new IllegalStateException("wrong message format");
                default:
                    throw new IllegalStateException("unknown response error code");
                 
            }
        }
    }
 
}
