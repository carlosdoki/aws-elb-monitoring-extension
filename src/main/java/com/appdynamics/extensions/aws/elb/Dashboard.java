/*
 *   Copyright 2018 . AppDynamics LLC and its affiliates.
 *   All Rights Reserved.
 *   This is unpublished proprietary source code of AppDynamics LLC and its affiliates.
 *   The copyright notice above does not evidence any actual or intended publication of such source code.
 *
 */

package com.appdynamics.extensions.aws.elb;

import com.appdynamics.extensions.TaskInputArgs;
import com.appdynamics.extensions.dashboard.CustomDashboardJsonUploader;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by bhuvnesh.kumar on 7/5/18.
 */
public class Dashboard {

    private Map config;
    private static final Logger LOGGER = Logger.getLogger(Dashboard.class);
    private String dashboardString;

    private CustomDashboardJsonUploader customDashboardJsonUploader;

    public Dashboard(Map config, String dashboardXML) {
        LOGGER.debug("INTERNAL Setting up DashboardClass");

        this.config = config;
        this.dashboardString = dashboardXML;

        LOGGER.debug("INTERNAL leaving DashboardClass");

    }

    protected void sendDashboard() {
        try {

            customDashboardJsonUploader = new CustomDashboardJsonUploader();

            LOGGER.debug("INTERNAL created CustomDashboardUploader");
            Map<String, ? super Object> argsMap = getControllerInfo();
            uploadDashboard(argsMap);


        } catch (Exception e) {
            LOGGER.debug("Unable to upload dashboard", e);
        }
    }

    private Map<String, ? super Object> getControllerInfo() {
        Map<String, ? super Object> argsMap = new HashMap<>();

        String user = config.get("username").toString() + "@" + config.get("account");

        List<Map<String, ?>> serverList = new ArrayList<>();
        Map<String, ? super Object> serverMap = new HashMap<>();
        serverMap.put(TaskInputArgs.HOST, config.get("host").toString());
        serverMap.put(TaskInputArgs.PORT, config.get("port").toString());
        serverMap.put(TaskInputArgs.USE_SSL, false);
        serverMap.put(TaskInputArgs.USER, user);
        serverMap.put(TaskInputArgs.PASSWORD, config.get("password").toString());
        serverList.add(serverMap);
        argsMap.put("servers", serverList);

        Map<String, ? super Object> connectionMap = new HashMap<>();
        String[] sslProtocols = {"TLSv1.2"};
        connectionMap.put(TaskInputArgs.SSL_PROTOCOL, sslProtocols);
        connectionMap.put("sslCertCheckEnabled", false);
        connectionMap.put("connectTimeout", 10000);
        connectionMap.put("socketTimeout", 15000);
        argsMap.put("connection", connectionMap);
        return argsMap;
    }

    private void uploadDashboard(Map<String, ? super Object> argsMap) {
        LOGGER.debug("INTERNAL going to upload dashboard now");

        replaceAppTierNode();
        customDashboardJsonUploader.uploadDashboard(config.get("namePrefix").toString(), dashboardString, argsMap, false);

        LOGGER.debug("INTERNAL back from upload dashboard now");
    }

    private void replaceAppTierNode() {

        dashboardString = dashboardString.replace("replaceApplicationName", config.get("applicationName").toString());
        dashboardString = dashboardString.replace("replaceTierName", config.get("tierName").toString());
        dashboardString = dashboardString.replace("replaceNodeName", config.get("nodeName").toString());
        dashboardString = dashboardString.replace("replaceDashboardName", config.get("namePrefix").toString());

    }

}


