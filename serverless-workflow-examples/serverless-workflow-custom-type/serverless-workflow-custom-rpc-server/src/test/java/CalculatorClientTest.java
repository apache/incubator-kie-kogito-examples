import java.io.IOException;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.kie.kogito.examples.sw.custom.CalculatorClient;
import org.kie.kogito.examples.sw.custom.CalculatorServer;
import org.kie.kogito.examples.sw.custom.CalculatorClient.OperationId;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CalculatorClientTest {
    
    
    private static CalculatorServer server; 
    
    @BeforeAll 
    static void init() throws IOException
    {
        server = new CalculatorServer(8082);
    }
    
    
    @AfterAll static void cleanup () throws IOException{
        server.close();
    }
    
    @Test
    void testCalculator() throws IOException { 
        assertEquals(7,CalculatorClient.invokeOperation("localhost", 8082, OperationId.ADD, 4, 3));
        assertEquals(1,CalculatorClient.invokeOperation("localhost", 8082, OperationId.SUBTRACTION, 4, 3));
        assertEquals(12,CalculatorClient.invokeOperation("localhost", 8082, OperationId.MULTIPLICATION, 4, 3));
        assertEquals(1,CalculatorClient.invokeOperation("localhost", 8082, OperationId.DIVISION, 4, 3));
    }

}
