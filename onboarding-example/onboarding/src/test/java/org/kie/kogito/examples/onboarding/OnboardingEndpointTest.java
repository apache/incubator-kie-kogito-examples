package org.kie.kogito.examples.onboarding;

import static io.restassured.RestAssured.given;
import static org.hamcrest.core.Is.is;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import javax.inject.Inject;

import org.junit.jupiter.api.Test;
import org.kie.api.runtime.process.WorkItem;
import org.kie.api.runtime.process.WorkItemHandler;
import org.kie.kogito.Application;
import org.kie.kogito.examples.test.RecordedOutputWorkItemHandler;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;

@QuarkusTest
public class OnboardingEndpointTest {
    
    @Inject
    Application application;

    
    @Test
    public void testOnboardingProcessUserAlreadyExists() {
        
        registerHandler("ValidateEmployee", (workitem) -> {
            
            Map<String, Object> results = new HashMap<>();
            results.put("status", "exists");
            results.put("message", "user already exists");
            return results;
        });
        
        given()
               .body("{\"employee\" : {\"firstName\" : \"Mark\", \"lastName\" : \"Test\", \"personalId\" : \"xxx-yy-zzz\", \"birthDate\" : \"1995-12-10T14:50:12.123+02:00\", \"address\" : {\"country\" : \"US\", \"city\" : \"Boston\", \"street\" : \"any street 3\", \"zipCode\" : \"10001\"}}}")
               .contentType(ContentType.JSON)
          .when()
               .post("/onboarding")
          .then()
             .statusCode(200)
             .body("status", is("exists"))
             .body("message", is("user already exists"));
    }
    
    @Test
    public void testOnboardingProcessNewUserUS() {
        
        registerHandler("ValidateEmployee", (workitem) -> {
            
            Map<String, Object> results = new HashMap<>();
            results.put("status", "new");
            results.put("message", "user needs to be onboarded");
            return results;
        });
        registerHandler("AssignIdAndEmail", (workitem) -> {
            
            Map<String, Object> results = new HashMap<>();
            results.put("email", "test@company.com");
            results.put("employeeId", "acb123");
            return results;
        });
        registerHandler("AssignDepartmentAndManager", (workitem) -> {
            
            Map<String, Object> results = new HashMap<>();
            results.put("manager", "mary frog");
            results.put("department", "US00099");
            return results;
        });
        registerHandler("CalculatePaymentDate", (workitem) -> {
            
            Map<String, Object> results = new HashMap<>();
            results.put("paymentDate", "2019-05-01T23:59:00.123Z[UTC]");
            return results;
        });
        registerHandler("CalculateVacationDays", (workitem) -> {
            
            Map<String, Object> results = new HashMap<>();
            results.put("vacationDays", 25);
            return results;
        });
        registerHandler("CalculateTaxRate", (workitem) -> {
            
            Map<String, Object> results = new HashMap<>();
            results.put("taxRate", 22.0);
            return results;
        });
        
        given()
           .body("{\"employee\" : {\"firstName\" : \"Mark\", \"lastName\" : \"Test\", \"personalId\" : \"xxx-yy-zzz\", \"birthDate\" : \"1995-12-10T14:50:12.123+02:00\", \"address\" : {\"country\" : \"US\", \"city\" : \"Boston\", \"street\" : \"any street 3\", \"zipCode\" : \"10001\"}}}")
           .contentType(ContentType.JSON)
       .when()
           .post("/onboarding")
       .then()
           .statusCode(200)
           .body("status", is("new"))
           .body("message", is("user needs to be onboarded"))
           .body("email", is("test@company.com"))
           .body("employeeId", is("acb123"))
           .body("manager", is("mary frog"))
           .body("department", is("US00099"))
           .body("payroll.paymentDate", is("2019-05-01T23:59:00.123Z[UTC]"))
           .body("payroll.vacationDays", is(25))
           .body("payroll.taxRate", is(new Float(22.0)));
}
    
    /*
     * Helper methods
     */        
    protected void registerHandler(String name, Function<WorkItem, Map<String, Object>> item) {
        WorkItemHandler handler = application.config().process().workItemHandlers().forName(name);
        ((RecordedOutputWorkItemHandler) handler).record(name, item);
    }
}
