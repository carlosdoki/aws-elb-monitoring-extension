/*
 *   Copyright 2018 . AppDynamics LLC and its affiliates.
 *   All Rights Reserved.
 *   This is unpublished proprietary source code of AppDynamics LLC and its affiliates.
 *   The copyright notice above does not evidence any actual or intended publication of such source code.
 *
 */

package com.appdynamics.extensions.aws.elb;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Test;
import com.appdynamics.extensions.dashboard.CustomDashboardJsonUploader;
import org.mockito.Mockito;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by bhuvnesh.kumar on 7/17/18.
 */
public class DashboardTest {

    private static final Logger logger = Logger.getLogger(DashboardTest.class);

    private Map config;
    private String dashboardJson ;
    private Map dashboardMap;

    private void valueMap(){
        config = new HashMap();
        config.put("enabled","true");
        config.put("namePrefix","Prefix");
        config.put("uploadDashboard","true");
        config.put("executionFrequencyMinutes","5");
        config.put("host","localhost");
        config.put("port","8090");
        config.put("account","customer");
        config.put("username","user");
        config.put("password","pass");
        config.put("applicationName","app");
        config.put("tierName","tier");
        config.put("nodeName","node");
    }

    private void getJsonAsString()  {
        try {
            dashboardJson = FileUtils.readFileToString(new File("src/test/resources/conf/dashboard.json"));

        } catch (Exception e){
            logger.error("Error in file reading");
        }
    }

    @Test
    public void testDashboard(){
        valueMap();
        getJsonAsString();
        Dashboard dashboard = new Dashboard(config, dashboardJson, dashboardMap);
        CustomDashboardJsonUploader customDashboardJsonUploader = new CustomDashboardJsonUploader();
        CustomDashboardJsonUploader customSpy = Mockito.spy(customDashboardJsonUploader);
        Mockito.doNothing().when(customSpy).uploadDashboard(config.get("namePrefix").toString(), dashboardJson, config, false);
        try {
            dashboard.sendDashboard();
        } catch (Exception e){
            // error encountered
            Assert.assertTrue(false);
        }
    }
}
