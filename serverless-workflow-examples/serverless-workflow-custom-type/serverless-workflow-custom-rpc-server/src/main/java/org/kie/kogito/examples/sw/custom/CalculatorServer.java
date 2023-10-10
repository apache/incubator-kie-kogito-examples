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

import java.io.Closeable;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class CalculatorServer implements Runnable, Closeable {
    
    private static final Logger logger = LoggerFactory.getLogger(CalculatorServer.class);

    public static void main(String[] args) throws IOException  {
       int port = args.length >= 1 ? Integer.parseInt(args[0]) : 8082;
       try (CalculatorServer server = new CalculatorServer(port)) {
         server.run();
       } 
    }
           
    private final ServerSocket socket;
    
    public CalculatorServer (int port) throws IOException  {
        this.socket = new ServerSocket(port);
        new Thread(this).start();
    }
    
    @Override
    public void run() {  
         // old single thread server
        while (!socket.isClosed()) {
            try (Socket conn = socket.accept()) {
                DataInputStream in = new DataInputStream(conn.getInputStream());
                byte opCode = in.readByte();
                int result = 0;
                int errorCode = 0;
                try {
                    switch (opCode) {
                        case 1: // add
                            result = in.readInt() + in.readInt();
                            break;
                        case 2: // subtraction
                            result = in.readInt() - in.readInt();
                            break;
                        case 3: // multiplication
                            result = in.readInt() * in.readInt();
                            break;
                        case 4: //division 
                            result = in.readInt() / in.readInt();
                            break;
                        default:
                            //unrecognized
                            errorCode = -1;
                    }
                } catch (IOException io) {
                    errorCode = -2;
                }
                DataOutputStream out = new DataOutputStream(conn.getOutputStream());
                out.writeByte(errorCode);
                if (errorCode == 0) {
                    out.writeInt(result);
                }
            }
            catch (IOException io) {
                logger.error("Error writing to client",io);
            }
        }
    }
    
    @Override
    public void close () throws IOException {
        socket.close();
    }
  }

