/**
 
 */
package com.bpmn.poc.workflow;

import java.io.Serializable;

/**
 * Class holds information regarding execution result.
 *
 */
public class ExecutionResult implements Serializable {

    private static final long serialVersionUID = 1L;
    int errorCode = -1;

    /**
     * Keeps exception if flow finished with error.
     */
    FlowExecutionException exception = null;

    /**
     * Keeps context after execution.
     */
    FlowContext flowContext = null;

    /**
     * Keeps name of last node which was executed.
     */
    String lastSuccessfulNodeName = null;

    public ExecutionResult(FlowContext logicContext) {
        this.flowContext = logicContext;
    }

    public void setExecutionExeption(FlowExecutionException ex) {

        this.exception = ex;
    }

    public FlowExecutionException getException() {
        return exception;
    }

    /**
     * Returns flowContext with output parameters.
     *
     * @return flow context.
     */
    public FlowContext getFlowContext() {
        return flowContext;
    }

    public void print() {
        if (exception != null) {
            exception.printStackTrace();
        }

    }

    /**
     * Returns last executed node.
     *
     * @return node name
     */
    public String getLastSuccessfulNodeName() {
        return lastSuccessfulNodeName;
    }

    public void setLastSuccessfulNodeName(String lastSuccessfulNodeName) {
        this.lastSuccessfulNodeName = lastSuccessfulNodeName;
    }

}
