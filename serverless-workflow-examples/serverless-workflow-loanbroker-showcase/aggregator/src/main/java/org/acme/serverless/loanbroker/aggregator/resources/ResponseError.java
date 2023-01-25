package org.acme.serverless.loanbroker.aggregator.resources;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ResponseError implements Serializable {

    public static final ResponseError NO_DATA_EVENT_ERROR = new ResponseError("Event data not present");
    public static final ResponseError NO_KOGITO_EXT_EVENT_ERROR = new ResponseError("Kogito Instance Id not found");
    public static final ResponseError SERVER_ERROR = new ResponseError("Internal Server Error");

    private final String message;
    private String cause;

    public ResponseError(final String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public String getCause() {
        return cause;
    }

    public void setCause(String cause) {
        this.cause = cause;
    }

    public ResponseError withCause(final Throwable innerCause) {
        this.cause = innerCause.getMessage();
        return this;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((cause == null) ? 0 : cause.hashCode());
        result = prime * result + ((message == null) ? 0 : message.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        ResponseError other = (ResponseError) obj;
        if (cause == null) {
            if (other.cause != null) {
                return false;
            }
        } else if (!cause.equals(other.cause)) {
            return false;
        }
        if (message == null) {
            if (other.message != null) {
                return false;
            }
        } else if (!message.equals(other.message)) {
            return false;
        }
        return true;
    }

}
