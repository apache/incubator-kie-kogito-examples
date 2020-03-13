package org.acme.travels;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.kie.kogito.Model;
import org.kie.kogito.process.Process;
import org.kie.kogito.process.ProcessInstance;

import io.quarkus.test.junit.QuarkusTest;

@Disabled("depends on https://petstore.swagger.io existing users")
@QuarkusTest
public class UsersProcessTest {


    @Named("users")
    @Inject
    Process<? extends Model> usersProcess;

    @Test
    public void testExistingUser() {

        assertNotNull(usersProcess);

        Model m = usersProcess.createModel();
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("username", "test");
        m.fromMap(parameters);

        ProcessInstance<?> processInstance = usersProcess.createInstance(m);
        processInstance.start();
        assertEquals(org.kie.api.runtime.process.ProcessInstance.STATE_COMPLETED, processInstance.status());
        Model result = (Model)processInstance.variables();
        assertEquals(2, result.toMap().size());

        User user = (User) result.toMap().get("traveller");
        assertNotNull(user);

        assertEquals("test", user.getUsername());
        assertEquals("Test", user.getFirstName());
        assertEquals("Test", user.getLastName());
        assertEquals("test@test.com", user.getEmail());
    }

    @Test
    public void testNotExistingUser() {

        assertNotNull(usersProcess);

        Model m = usersProcess.createModel();
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("username", "notexisting");
        m.fromMap(parameters);

        ProcessInstance<?> processInstance = usersProcess.createInstance(m);
        processInstance.start();
        assertEquals(org.kie.api.runtime.process.ProcessInstance.STATE_COMPLETED, processInstance.status());
        Model result = (Model)processInstance.variables();
        assertEquals(2, result.toMap().size());

        User user = (User) result.toMap().get("traveller");
        assertNull(user);

    }
}
