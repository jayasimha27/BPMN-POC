/**
 
 */
package com.bpmn.poc.workflow;

import java.io.Serializable;

public class FlowExecutionException extends Exception implements Serializable {

    private static final long serialVersionUID = 1L;

    public FlowExecutionException(String msg) {
        super(msg);
    }

    public FlowExecutionException(String string, Exception e1) {
        super(string, e1);
    }

    public FlowExecutionException(Throwable cause) {
        super(cause);
    }
}
