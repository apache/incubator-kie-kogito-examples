/**
 *  Copyright 2020 Red Hat, Inc. and/or its affiliates.
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
package org.kie.kogito.resources.process;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.URL;
import java.security.SecureRandom;
import java.util.Optional;

import javax.net.ServerSocketFactory;

import org.kie.kogito.resources.TestResource;

public class LocalQuarkusProcessTestResource implements TestResource {

    private static final int PORT_RANGE_MIN = 1024;
    private static final int PORT_RANGE_MAX = 65535;
    private static final SecureRandom RND = new SecureRandom();
    private static final String QUARKUS_HTTP_PORT = "quarkus.http.port";

    private final String name;
    private final URL resource;

    private int port;
    private Process process;

    public LocalQuarkusProcessTestResource(String name, String path) {
        this.name = name;
        this.resource = this.getClass().getClassLoader().getResource(path);
    }

    @Override
    public void start() {
        this.port = findAvailablePort();
        String separator = System.getProperty("file.separator");
        String path = System.getProperty("java.home") + separator + "bin" + separator + "java";
        ProcessBuilder processBuilder = new ProcessBuilder(path,
                                                           String.format("-D%s=%s", QUARKUS_HTTP_PORT, getMappedPort()),
                                                           "-jar", resource.getPath());
        try {
            this.process = processBuilder.start();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void stop() {
        Optional.ofNullable(process).ifPresent(Process::destroy);
    }

    @Override
    public String getResourceName() {
        return name;
    }

    @Override
    public int getMappedPort() {
        return port;
    }

    protected int findAvailablePort() {
        int portRange = PORT_RANGE_MAX - PORT_RANGE_MIN;
        int candidatePort;
        int searchCounter = 0;
        do {
            if (searchCounter > portRange) {
                throw new IllegalStateException(String.format(
                                                              "Could not find an available %s port in the range [%d, %d] after %d attempts",
                                                              name, PORT_RANGE_MIN, PORT_RANGE_MAX, searchCounter));
            }
            candidatePort = findRandomPort(PORT_RANGE_MIN, PORT_RANGE_MAX);
            searchCounter++;
        } while (!isPortAvailable(candidatePort));

        return candidatePort;
    }

    private int findRandomPort(int minPort, int maxPort) {
        int portRange = maxPort - minPort;
        return minPort + RND.nextInt(portRange + 1);
    }

    private boolean isPortAvailable(int port) {
        try {
            ServerSocket serverSocket = ServerSocketFactory.getDefault().createServerSocket(port, 1, InetAddress.getByName("localhost"));
            serverSocket.close();
            return true;
        } catch (Exception ex) {
            return false;
        }
    }
}