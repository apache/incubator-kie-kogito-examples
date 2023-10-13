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
package org.kie.kogito.examples.quarkus;

import java.io.IOException;
import java.util.Properties;

public class ProjectMetadataProvider {

    private static final Properties props = new Properties();

    private static final String projectVersion;
    private static final String projectArtifactId;

    static {
        String propertyFileName = "project.properties";
        try {
            props.load(GrafanaDockerComposeIT.class.getClassLoader().getResourceAsStream(propertyFileName));
            projectVersion = props.getProperty("project.version");
            projectArtifactId = props.getProperty("project.artifactId");
        } catch (IOException e) {
            throw new IllegalStateException("Impossible to retrieve property file " + propertyFileName, e);
        }
        if (projectVersion == null || projectArtifactId == null || projectVersion.startsWith("${") || projectArtifactId.startsWith("${")) {
            throw new IllegalStateException("The projectVersion and/or the projectArtifactId maven properties are not configured properly.");
        }
    }

    public static String getProjectVersion() {
        return projectVersion;
    }

    public static String getProjectArtifactId() {
        return projectArtifactId;
    }
}
