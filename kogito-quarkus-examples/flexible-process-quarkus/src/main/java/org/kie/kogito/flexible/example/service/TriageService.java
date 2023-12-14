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
package org.kie.kogito.flexible.example.service;

import java.util.Random;

import org.kie.kogito.flexible.example.model.State;
import org.kie.kogito.flexible.example.model.SupportCase;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class TriageService {

    public static final String[] MIDDLEWARE_ENGINEERS = new String[] { "martin", "manuel" };
    public static final String[] STORAGE_ENGINEERS = new String[] { "samantha", "sarah" };
    public static final String[] CLOUD_ENGINEERS = new String[] { "candy", "chris" };
    public static final String[] KOGITO_ENGINEERS = new String[] { "ken", "kelly" };
    public static final String[] CEPH_ENGINEERS = new String[] { "connor", "candy" };

    private final Random random = new Random();

    public SupportCase assignEngineer(SupportCase supportCase, String supportGroup) {
        SupportCase result = new SupportCase(supportCase);
        if (supportCase.getEngineer() == null) {
            if (supportGroup == null) {
                return supportCase;
            }
            String[] engineers = getEngineers(supportGroup);
            if (engineers == null) {
                return supportCase;
            }
            String engineer = engineers[random.nextInt(engineers.length)];
            result.setEngineer(engineer);
        }
        if (State.NEW.equals(supportCase.getState())) {
            result.setState(State.WAITING_FOR_OWNER);
        }
        return result;
    }

    private String[] getEngineers(String family) {
        switch (family) {
            case "Middleware":
                return MIDDLEWARE_ENGINEERS;
            case "Storage":
                return STORAGE_ENGINEERS;
            case "Cloud":
                return CLOUD_ENGINEERS;
            case "Kogito":
                return KOGITO_ENGINEERS;
            case "Ceph":
                return CEPH_ENGINEERS;
            default:
                return null;
        }
    }
}
