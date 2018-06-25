/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bpmn.poc.workflow.loader;

import com.bpmn.poc.workflow.FlowExecutionException;
import com.bpmn.poc.workflow.Workflow;
import com.bpmn.poc.workflow.common.WorkflowConverter;
import com.bpmn.poc.workflow.common.XmlWorkflowConverter;
import com.bpmn.poc.workflow.node.CustomNode;
import com.bpmn.poc.workflow.node.EndNode;
import com.bpmn.poc.workflow.node.StartNode;
import com.bpmn.poc.workflow.node.StartNodeTypes;
import com.bpmn.poc.workflow.node.Transition;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.Serializable;
import java.net.URL;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class URLWorkflowLoader implements WorkflowLoader, Serializable {

    private static final Logger logger = LoggerFactory.getLogger(URLWorkflowLoader.class);

    protected WorkflowConverter converter;

    public URLWorkflowLoader(final WorkflowConverter converter) {
        this.converter = Optional.ofNullable(converter).orElse(new XmlWorkflowConverter());
    }

    @Override
    public Workflow load(final String name) throws FlowExecutionException {
        /*try {
            URL resource = getResource(name);
            if (resource == null) {
                throw new FlowExecutionException(name + " not found.");
            }
            return content(resource, name);
        } catch (IOException e) {
            throw new FlowExecutionException(name, e);
        }*/

        Workflow workflow = new Workflow("com.bpmn.poc.helloworld", "com.bpmn.poc");

        // Create Start Node
        StartNode startNode = new StartNode("Start", "f79d7dc789f36c41b257");
        startNode.setType(StartNodeTypes.valueOf("PUBLIC"));
        workflow.registerStartNode(startNode);
        workflow.registerNode(startNode);

        // Create End node
        EndNode endNode = new EndNode("END", "f79d7dc789f36c41b258");
        workflow.registerNode(endNode);

        // Create End node
        EndNode endErrNode = new EndNode("ERROR", "f79d7dc789f36c41b259");
        workflow.registerNode(endErrNode);

        //Create Custom node  
        CustomNode customNode1 = new CustomNode("com.bpmn.poc.workflow.example.EmailNode",
                "HelloWorld", "f79d7dc789f36c41b253");
        customNode1.addParameter("name", "EmailNode");
        customNode1.addOutParameter("message", "Hello World!");
        workflow.registerNode(customNode1);

        // Create Transitions
        Transition startTransition = new Transition();
        startTransition.setFromNode(startNode);
        startTransition.setName("NEXT");
        startTransition.setToNode(customNode1);
        startNode.registerExit(startTransition);
        startNode.init();

        // Create Transitions
        Transition endTransition = new Transition();
        endTransition.setFromNode(customNode1);
        endTransition.setName("NEXT");
        endTransition.setToNode(endNode);
        customNode1.registerExit(endTransition);
        customNode1.init();

        // Create Transitions
        Transition endErrTransition = new Transition();
        endErrTransition.setFromNode(customNode1);
        endErrTransition.setName("ERROR");
        endErrTransition.setToNode(endErrNode);
        customNode1.registerExit(endErrTransition);
        customNode1.init();

        return workflow;
    }

    protected Workflow content(URL resource, final String name) throws FlowExecutionException {
        Workflow net = null;
        Reader inputStream = null;
        try {
            inputStream = getReader(resource);
            net = converter.convert(inputStream, name);

        } catch (IOException e) {
            logger.error("Error during loading " + resource, e);
        } finally {
            try {
                if (null != inputStream) {
                    inputStream.close();
                }
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }
        }
        return net;
    }

    protected abstract URL getResource(String location) throws IOException;

    protected String normalize(String path) {
        return path.replace("\\.", File.separator);
    }

    protected Reader getReader(URL resource) throws IOException {
        return new InputStreamReader(resource.openStream(), "UTF-8");
    }

}
