/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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

import org.springframework.boot.SpringApplication;
import org.springframework.boot.actuate.autoconfigure.metrics.cache.CacheMetricsAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.kubernetes.fabric8.discovery.KubernetesCatalogWatchAutoConfiguration;
import org.springframework.cloud.kubernetes.fabric8.discovery.KubernetesDiscoveryClientAutoConfiguration;

// Disabling the cache metrics for now, see: https://github.com/infinispan/infinispan-spring-boot/issues/168
@SpringBootApplication(scanBasePackages = { "org.kie.kogito.**" },
        exclude = { CacheMetricsAutoConfiguration.class,
                KubernetesDiscoveryClientAutoConfiguration.class,
                KubernetesCatalogWatchAutoConfiguration.class })
public class KogitoOnboardingApplication {

    public static void main(String[] args) {
        SpringApplication.run(KogitoOnboardingApplication.class, args);
    }
}
