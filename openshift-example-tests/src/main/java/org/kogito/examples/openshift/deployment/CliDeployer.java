/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kogito.examples.openshift.deployment;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.PosixFilePermission;
import java.nio.file.attribute.PosixFilePermissions;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import cz.xtf.core.http.Https;
import cz.xtf.core.waiting.SimpleWaiter;
import org.kogito.examples.openshift.Project;
import org.kogito.examples.openshift.TestConfig;
import org.kogito.examples.process.ProcessExecutor;

public class CliDeployer {

    private static Path kogitoCliFilePath = null;

    private CliDeployer() {}

    /**
     * Deploy KaaS application into the project using S2I and wait until application starts.
     *
     * @param project Project where the application will be deployed to.
     * @param applicationName Name of the deployed application.
     * @param assetsUrl URL pointing to the GIT repo containing Kie assets.
     * @param gitContextDir Context directory of GIT repo containing Kie assets.
     * @param envVariables Environment variables for running application.
     * @param serviceLabels Service labels applied to deployed service.
     * @return Deployment object containing reference to the deployed application URL.
     */
    public static HttpDeployment deployKaasUsingS2iAndWait(Project project, String applicationName, URL assetsUrl, String gitContextDir, Map<String, String> envVariables, Map<String, String> serviceLabels) {
        Path kogitoCliFilePath = getKogitoCliFilePath();
        List<String> cliDeployCommand = new ArrayList<>(Arrays.asList("./" + kogitoCliFilePath.getFileName(), "deploy", applicationName, assetsUrl.toExternalForm(), "--context", gitContextDir, "--app", project.getName()));

        if (!envVariables.isEmpty()) {
            cliDeployCommand.add("--env");
            cliDeployCommand.add(getParameterKeyValueString(envVariables));
        }
        if (!serviceLabels.isEmpty()) {
            cliDeployCommand.add("--svc-labels");
            cliDeployCommand.add(getParameterKeyValueString(serviceLabels));
        }
        // Add Maven mirror URL if defined
        TestConfig.getMavenMirrorUrl().ifPresent(mavenMirrorUrl -> {
            cliDeployCommand.add("--build-env");
            cliDeployCommand.add(getParameterKeyValueString(Collections.singletonMap("MAVEN_MIRROR_URL", mavenMirrorUrl)));
        });

        try (ProcessExecutor executor = new ProcessExecutor()) {
            executor.executeProcessCommand(cliDeployCommand.toArray(new String[0]), kogitoCliFilePath.getParent());
        }

        // Wait until Operator process CR created by CLI 
        project.getMaster().waiters().areExactlyNPodsRunning(1, applicationName).timeout(TimeUnit.MINUTES, 30L).waitFor();

        // Temporary implementation, service name is equal to applicationName
        HttpDeployment kaasDeployment = new HttpDeployment(project, applicationName);

        // Wait until route is available and working
        new SimpleWaiter(() -> Https.getCode(kaasDeployment.getRouteUrl().toExternalForm()) != 503).reason("Waiting for deployment route to connect to pod.").timeout(TimeUnit.SECONDS, 30L).waitFor();

        return kaasDeployment;
    }

    /**
     * Returns String containing parameters prepared for usage with OC client, in for of parameter1=value1,parameter=value2...
     *
     * @param parameters
     * @return
     */
    private static String getParameterKeyValueString(Map<String, String> parameters) {
        return parameters.entrySet().stream().map(e -> e.getKey() + "=" + e.getValue()).collect(Collectors.joining(","));
    }

    /**
     * @return Kogito CLI file path, downloading Kogito CLI if not available yet.
     */
    private static Path getKogitoCliFilePath() {
        if (Objects.isNull(kogitoCliFilePath)) {
            try {
                kogitoCliFilePath = Files.createTempFile("kogito-cli", "");

                Set<PosixFilePermission> ownerExecute = PosixFilePermissions.fromString("rwxr--r--");
                Files.setPosixFilePermissions(kogitoCliFilePath, ownerExecute);

                downloadKogitoCliFile(kogitoCliFilePath.toFile());
            } catch (IOException e) {
                throw new RuntimeException("IO exception while retrieving Kogito CLI.", e);
            }
        }
        return kogitoCliFilePath;
    }

    /**
     * Download Kogito CLI file.
     *
     * @param kogitoCliFile File location where Kogito CLI will be stored to.
     * @throws IOException In case of any error while downloading.
     */
    private static void downloadKogitoCliFile(File kogitoCliFile) throws IOException {
        URL kogitoCliFileUrl = TestConfig.getKogitoCliFileUrl();
        try (ReadableByteChannel readableByteChannel = Channels.newChannel(kogitoCliFileUrl.openStream());
                FileOutputStream fileOutputStream = new FileOutputStream(kogitoCliFile);) {
            fileOutputStream.getChannel().transferFrom(readableByteChannel, 0, Long.MAX_VALUE);
        }
    }
}
