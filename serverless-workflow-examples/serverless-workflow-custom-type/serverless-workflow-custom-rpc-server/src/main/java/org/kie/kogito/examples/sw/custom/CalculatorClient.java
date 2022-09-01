package org.kie.kogito.examples.sw.custom;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class CalculatorClient {
    
    public enum OperationId {
        ADD (1),
        SUBTRACTION(2),
        MULTIPLICATION(3),
        DIVISION(4),
        UNKNOWN(-1);
        
        
        private final byte opCode;
        
        private OperationId(int opCode) {
            this.opCode = (byte)opCode;
        }

        public byte getOpCode() {
            return opCode;
        }

    }
    
    public static int invokeOperation (String host, int port, OperationId operationId, int value1, int value2) throws IOException {
        
        try (Socket socket = new Socket(host,port)) 
        {
            DataOutputStream out = new DataOutputStream (socket.getOutputStream());
            out.writeByte(operationId.getOpCode());
            out.writeInt(value1);
            out.writeInt(value2);
        
            DataInputStream in = new DataInputStream(socket.getInputStream());
            switch (in.readByte()) {
                case 0: 
                    return in.readInt();
                case -1:
                    throw new UnsupportedOperationException(operationId.toString());
                case -2: 
                    throw new IllegalStateException("wrong message format");
                default:
                    throw new IllegalStateException("unknown response error code");
                 
            }
        }
    }
 
}
