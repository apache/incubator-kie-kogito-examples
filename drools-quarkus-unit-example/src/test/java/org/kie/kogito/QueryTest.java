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

package org.kie.kogito;

import java.util.List;
import java.util.Map;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;

@QuarkusTest
public class QueryTest {

    @Test
    public void testFindAdults() {
        String personsPayload = "{\"adultAge\":18,\"persons\":[{\"name\":\"Mario\",\"age\":45,\"adult\":false},{\"name\":\"Sofia\",\"age\":7,\"adult\":false}]}";
        Map result = (Map) given().contentType( ContentType.JSON).accept(ContentType.JSON).body(personsPayload).when()
                .post("/find-adults").then().statusCode(200).extract().as( List.class ).get( 0 );

        assertEquals("Mario", result.get("name"));
    }

    @Test
    public void testNames() {
        String personsPayload = "{\"adultAge\":18,\"persons\":[{\"name\":\"Mario\",\"age\":45,\"adult\":false},{\"name\":\"Sofia\",\"age\":7,\"adult\":false}]}";
        String result = given().contentType( ContentType.JSON).accept(ContentType.JSON).body(personsPayload).when()
                .post("/find-adult-names").then().statusCode(200).extract().as( List.class ).get( 0 ).toString();

        assertEquals("Mario", result);
    }

}
