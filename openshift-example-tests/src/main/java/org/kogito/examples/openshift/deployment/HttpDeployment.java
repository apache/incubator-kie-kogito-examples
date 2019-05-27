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

import java.net.MalformedURLException;
import java.net.URL;

import io.fabric8.openshift.api.model.Route;
import org.kogito.examples.openshift.Project;

public class HttpDeployment {

    private Project project;
    private String serviceName;

    public HttpDeployment(Project project, String serviceName) {
        this.project = project;
        this.serviceName = serviceName;
    }

    public URL getRouteUrl() {
        Route httpRoute = project.getMaster().getRoutes().stream()
                                 .filter(route -> route.getSpec().getTo().getName().equals(serviceName))
                                 .filter(route -> route.getSpec().getTls() == null)
                                 .findAny()
                                 .orElseThrow(() -> new RuntimeException("No HTTP route found for service " + serviceName));
        String routeUrl = "http://" + httpRoute.getSpec().getHost() + ":80";
        try {
            return new URL(routeUrl);
        } catch (MalformedURLException e) {
            throw new RuntimeException("Error while converting route URL " + routeUrl + " to URL object.", e);
        }
    }
}
