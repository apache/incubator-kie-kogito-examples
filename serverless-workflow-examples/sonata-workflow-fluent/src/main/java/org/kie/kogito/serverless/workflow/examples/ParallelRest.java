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
package org.kie.kogito.serverless.workflow.examples;

import java.util.Map;

import org.kie.kogito.process.Process;
import org.kie.kogito.serverless.workflow.actions.WorkflowLogLevel;
import org.kie.kogito.serverless.workflow.executor.StaticWorkflowApplication;
import org.kie.kogito.serverless.workflow.fluent.FunctionBuilder.HttpMethod;
import org.kie.kogito.serverless.workflow.models.JsonNodeModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.node.ObjectNode;

import io.serverlessworkflow.api.Workflow;

import static org.kie.kogito.serverless.workflow.fluent.ActionBuilder.call;
import static org.kie.kogito.serverless.workflow.fluent.ActionBuilder.log;
import static org.kie.kogito.serverless.workflow.fluent.ActionBuilder.subprocess;
import static org.kie.kogito.serverless.workflow.fluent.FunctionBuilder.rest;
import static org.kie.kogito.serverless.workflow.fluent.StateBuilder.operation;
import static org.kie.kogito.serverless.workflow.fluent.StateBuilder.parallel;
import static org.kie.kogito.serverless.workflow.fluent.WorkflowBuilder.jsonObject;
import static org.kie.kogito.serverless.workflow.fluent.WorkflowBuilder.workflow;

public class ParallelRest {

    private static final Logger logger = LoggerFactory.getLogger(ParallelRest.class);

    public static void main(String[] args) {
        try (StaticWorkflowApplication application = StaticWorkflowApplication.create()) {
            // create a reusable process for several executions
            Process<JsonNodeModel> process = application.process(getWorkflow(application));
            // execute it with one person name
            logger.info(application.execute(process, Map.of("name", "Javier")).getWorkflowdata().toPrettyString());
            // execute it with another person name
            logger.info(application.execute(process, Map.of("name", "Alba")).getWorkflowdata().toPrettyString());
        }
    }

    static Workflow getWorkflow(StaticWorkflowApplication application) {
        ObjectNode nameArgs = jsonObject().put("name", ".name");

        // Define a subflow process that retrieve country information from the given name
        Workflow subflow = workflow("GetCountry")
                // subflow consist of just one state with two sequential actions
                .start(operation()
                        // call rest function to retrieve country id 
                        .action(call(rest("getCountryId", HttpMethod.get, "https://api.nationalize.io:/?name={name}"), nameArgs)
                                // extract relevant information from the response using JQ expression
                                .resultFilter(".country[0].country_id").outputFilter(".id"))
                        // call rest function to retrieve country information from country id
                        .action(call(rest("getCountryInfo", HttpMethod.get, "https://restcountries.com/v3.1/alpha/{id}"), jsonObject().put("id", ".id"))
                                // we are only interested in country name, longitude and latitude
                                .resultFilter("{country: {name:.[].name.common, latitude: .[].latlng[0], longitude: .[].latlng[1] }}"))
                        // return only country field to parent flow
                        .outputFilter("{country}"))
                .end().build();

        Process<JsonNodeModel> subprocess = application.process(subflow);
        // This is the main flow, it invokes two services (one for retrieving the age and another to get the gender of the given name )and one subprocess (the country one defined above) in parallel
        // Once the three of them has been executed, if age is greater than 50, it retrieve the weather information for the retrieved country,
        // Else, it gets the list of universities for that country. 
        return workflow("FullExample")
                // Api key to be used in getting weather call
                .constant("apiKey", "2482c1d33308a7cffedff5764e9ef203")
                // Starts performing retrieval of gender, country and age from the given name on parallel 
                .start(parallel()
                        .newBranch().action(call(rest("getAge", HttpMethod.get, "https://api.agify.io/?name={name}"), nameArgs).resultFilter("{age}")).endBranch()
                        .newBranch().action(subprocess(subprocess)).endBranch()
                        .newBranch().action(call(rest("getGender", HttpMethod.get, "https://api.genderize.io/?name={name}"), nameArgs).resultFilter("{gender}")).endBranch())
                // once done, logs the age (using Jq string interpolation)
                .next(operation().action(log(WorkflowLogLevel.INFO, "\"Age is \\(.age)\"")))
                // If age is less that fifty, retrieve the list of universities (the parameters object is built using jq expressions) 
                .when(".age<50").next(operation().action(call(rest("getUniversities", HttpMethod.get, "http://universities.hipolabs.com/search?country={country}"),
                        jsonObject().put("country", ".country.name")).resultFilter(".[].name").outputFilter(".universities")))
                // Else retrieve the weather for that country capital latitude and longitude (note how parameters are build from model info) 
                .end().or()
                .next(operation().action(call(rest("getWeather", HttpMethod.get, "https://api.openweathermap.org/data/2.5/weather?lat={lat}&lon={lon}&appid={appid}"),
                        jsonObject().put("lat", ".country.latitude").put("lon", ".country.longitude").put("appid", "$CONST.apiKey"))
                                .resultFilter("{weather:.main}")))
                .end().build();
    }
}
