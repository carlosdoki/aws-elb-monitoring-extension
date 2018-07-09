/*
 *   Copyright 2018 . AppDynamics LLC and its affiliates.
 *   All Rights Reserved.
 *   This is unpublished proprietary source code of AppDynamics LLC and its affiliates.
 *   The copyright notice above does not evidence any actual or intended publication of such source code.
 *
 */

package com.appdynamics.extensions.aws.elb;

import com.appdynamics.extensions.dashboard.CustomDashboardTask;
import com.appdynamics.extensions.dashboard.CustomDashboardUploader;
import com.appdynamics.extensions.xml.Xml;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

import java.io.File;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by bhuvnesh.kumar on 7/5/18.
 */
public class Dashboard {

    private Map config;
    private CustomDashboardUploader dashboardUploader;
    private static final Logger LOGGER = Logger.getLogger(Dashboard.class);



    public Dashboard(Map config) {
        this.config = config;
    }

    private void sendDashboard() {
        try {
            String pathToDashboard = "monitors/AWSELBMonitor/dashboard.xml";
//            if(config.get(pathToDashboard))

            String content = FileUtils.readFileToString(new File(pathToDashboard));
            dashboardUploader = new CustomDashboardUploader();
            dashboardUploader.uploadDashboard("Custom Dashboard", Xml.fromString(content), config, false);

        }catch (Exception e){
            LOGGER.debug("Unable to upload dashboard", e);
        }
    }
}
