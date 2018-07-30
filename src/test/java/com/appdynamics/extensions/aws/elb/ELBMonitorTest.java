/*
 * Copyright 2018. AppDynamics LLC and its affiliates.
 * All Rights Reserved.
 * This is unpublished proprietary source code of AppDynamics LLC and its affiliates.
 * The copyright notice above does not evidence any actual or intended publication of such source code.
 *
 */

package com.appdynamics.extensions.aws.elb;

import com.google.common.collect.Maps;
import com.singularity.ee.agent.systemagent.api.TaskOutput;
import org.junit.Test;

import java.util.Map;

import static org.junit.Assert.assertTrue;


public class ELBMonitorTest {

    private ELBMonitor classUnderTest = new ELBMonitor();
//
//    @Test
//    public void testMetricsCollectionCredentialsEncrypted() throws Exception {
//        Map<String, String> args = Maps.newHashMap();
//        args.put("config-file", "src/main/resources/conf/config.yml");
//
////        args.put("config-file", "src/test/resources/conf/itest-encrypted-config.yml");
//        args.put("simDashboard", "src/test/resources/conf/simDashboard.json");
//        args.put("normalDashboard", "src/test/resources/conf/normalDashboard.json");
//
//        TaskOutput result = classUnderTest.execute(args, null);
//        assertTrue(result.getStatusMessage().contains("Monitor ELBMonitor completes."));
//
//    }

//    @Test
//    public void testMetricsCoyllectionWithProxy() throws Exception {
//        Map<String, String> args = Maps.newHashMap();
//        args.put("config-file", "src/test/resources/conf/itest-proxy-config.yml");
//        args.put("simDashboard", "src/test/resources/conf/simDashboard.json");
//        args.put("normalDashboard", "src/test/resources/conf/normalDashboard.json");
//
//        TaskOutput result = classUnderTest.execute(args, null);
//        assertTrue(result.getStatusMessage().contains("Monitor ELBMonitor completes."));
//    }
//

}
