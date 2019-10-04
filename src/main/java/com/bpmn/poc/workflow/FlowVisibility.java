/**
 
 */
package com.bpmn.poc.workflow;

import java.io.Serializable;

/**
 * Flow can be public or private. Public flow can be called by from outside.
 * Private flow can be called just by other flow.
 */
public enum FlowVisibility implements Serializable {
    Public,
    Private;

    private static final long serialVersionUID = 1L;
    private static String[] opNames = new String[]{Public.name(), Private.name()};

    public static FlowVisibility getDefault() {
        return Public;
    }

    public static String[] types() {
        return opNames;
    }

    public static FlowVisibility getByName(String name) {
        for (FlowVisibility d : values()) {
            if (d.name().equalsIgnoreCase(name)) {
                return d;
            }
        }
        return null;
    }

}
