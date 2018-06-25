/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.copper.poc.workflow;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import org.copperengine.core.WorkflowInstanceDescr;
import org.copperengine.core.persistent.PersistentScottyEngine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author jaja0617
 */
public class CopperWorkFlowExecutor {

    private static final String WF_CLASS = "com.copper.poc.workflow.dir.CopperWorkflow";
    private static final Logger logger = LoggerFactory.getLogger(CopperWorkFlowExecutor.class);
    private static final long startTime = System.nanoTime();

    //private CopperCassandraEngineFactory factory;
    private CopperRedisEngineFactory factory;
    private final AtomicInteger counter = new AtomicInteger();
    private final String payload;

    public CopperWorkFlowExecutor(int payloadSize) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < payloadSize; i++) {
            sb.append(i % 10);
        }
        payload = sb.toString();
    }

    public synchronized CopperWorkFlowExecutor start() throws Exception {
        if (factory != null) {
            return this;
        }
      
        //factory = new CopperCassandraEngineFactory();
        factory = new CopperRedisEngineFactory();
        factory.getEngine().startup();
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                factory.destroyEngine();
            }
        });
        return this;
    }

    public CopperWorkFlowExecutor startThread() {
        new Thread() {
            @Override
            public void run() {
                for (;;) {
                    work();
                }
            }
        }.start();
        return this;
    }

    public CopperWorkFlowExecutor startSingleThread() {
        new Thread() {
            @Override
            public void run() {
                singleWork();
            }
        }.start();
        return this;
    }

    public void work() {
        try {
            final PersistentScottyEngine engine = factory.getEngine();
            List<String> cids = new ArrayList<>();
            for (int i = 0; i < 5000; i++) {
                final String cid = engine.createUUID();
                final LoadTestData data = new LoadTestData(cid, payload);
                final WorkflowInstanceDescr<LoadTestData> wfid = new WorkflowInstanceDescr<>(WF_CLASS, data, cid, 1, null);
                engine.run(wfid);
                cids.add(cid);
            }
            for (String cid : cids) {
                factory.getBackchannel().wait(cid, 5, TimeUnit.MINUTES);
                int value = counter.incrementAndGet();
                if (value % 5000 == 0) {
                    long estimatedTime = System.nanoTime() - startTime;
                    System.out.println("-----------------------------------------------------------------");
                    System.out.println(new Date()+ " :Finished performance test with "+value+ " workflow instances in " +  
                            TimeUnit.SECONDS.convert(estimatedTime, TimeUnit.NANOSECONDS) +" secs");
                    System.out.println("ThroughputPerformanceTest [] - statistics :");
                    System.out.println(factory.getLoggingStatisticCollector().print());
                    System.out.println("-----------------------------------------------------------------");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void singleWork() {
        try {
            final PersistentScottyEngine engine = factory.getEngine();
            final String cid = engine.createUUID();
            final LoadTestData data = new LoadTestData(cid, payload);
            final WorkflowInstanceDescr<LoadTestData> wfid = new WorkflowInstanceDescr<>(WF_CLASS, data, cid, 1, null);
            engine.run(wfid);
            factory.getBackchannel().wait(cid, 5, TimeUnit.MINUTES);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    public static void main(String[] args) {
        try {
            CopperWorkFlowExecutor exe = new CopperWorkFlowExecutor(4096);
            exe.start();
            exe.startThread();
            System.out.println("Started!");
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(-1);
        }
    }

}
