/*
 *   Copyright 2018 . AppDynamics LLC and its affiliates.
 *   All Rights Reserved.
 *   This is unpublished proprietary source code of AppDynamics LLC and its affiliates.
 *   The copyright notice above does not evidence any actual or intended publication of such source code.
 *
 */

package com.appdynamics.extensions.aws.elb;

import com.appdynamics.extensions.ABaseMonitor;
import com.appdynamics.extensions.TaskInputArgs;
import com.appdynamics.extensions.conf.ControllerInfo;
import com.appdynamics.extensions.dashboard.CustomDashboardJsonUploader;
import com.appdynamics.extensions.logging.ExtensionsLoggerFactory;
//import org.apache.log4j.Logger;
import com.google.common.annotations.VisibleForTesting;
import sun.rmi.runtime.Log;
import org.slf4j.Logger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by bhuvnesh.kumar on 7/5/18.
 */
public class Dashboard {

    private static final Logger LOGGER = ExtensionsLoggerFactory.getLogger(Dashboard.class);

    private Map config;
    private String dashboardString;
    private ControllerInfo controllerInfo;
    private Map dashboardJsons;
    private CustomDashboardJsonUploader customDashboardJsonUploader;

    public Dashboard(Map config,  Map dashboardJsons) {
        LOGGER.debug(" Setting up Dashboard Class");

        this.config = config;
        this.dashboardJsons = dashboardJsons;

        LOGGER.debug("Leaving Dashboard Class");

    }

    protected void sendDashboard() {
        try {
            controllerInfo = new ControllerInfo().getControllerInfo();
            customDashboardJsonUploader = new CustomDashboardJsonUploader();
            LOGGER.debug("Created CustomDashboardUploader object");

            Map<String, ? super Object> argsMap = getControllerInfo();
            if (config.get("enabled").toString().equals("true")) {
                uploadDashboard(argsMap);
            } else {
                LOGGER.debug("Upload dashboard disabled, not uploading dashboard.");
            }

        } catch (Exception e) {
            LOGGER.error("Unable to upload dashboard", e);
        }
    }

    private Map<String, ? super Object> getControllerInfo() {
        Map<String, ? super Object> argsMap = new HashMap<>();

        String user = config.get("username").toString() + "@" + controllerInfo.getAccount();

        LOGGER.debug("dashboard Controller Info given to extension: ");
        LOGGER.debug("dashboard Host : " + controllerInfo.getControllerHost());
        LOGGER.debug("dashboard Port : " + controllerInfo.getControllerPort());
        LOGGER.debug("dashboard User : " + user);
        LOGGER.debug("dashboard Password: " + config.get("password").toString());
        LOGGER.debug("dashboard UseSSL: " + controllerInfo.getControllerSslEnabled());
        LOGGER.debug("dashboard ApplicationName: {}", controllerInfo.getApplicationName());
        LOGGER.debug("dashboard TierName: {}", controllerInfo.getTierName());
        LOGGER.debug("dashboard NodeName: {}", controllerInfo.getNodeName());
        LOGGER.debug("dashboard Sim Enabled: {}", controllerInfo.getSimEnabled());
        LOGGER.debug("dashboard Machine Path: {}", controllerInfo.getMachinePath());

        List<Map<String, ?>> serverList = new ArrayList<>();
        Map<String, ? super Object> serverMap = new HashMap<>();
        serverMap.put(TaskInputArgs.HOST, controllerInfo.getControllerHost());
        serverMap.put(TaskInputArgs.PORT, controllerInfo.getControllerPort().toString());
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

    private void loadDashboardBasedOnSim(){
        LOGGER.debug("Sim Enabled: {}", controllerInfo.getSimEnabled());

        if(controllerInfo.getSimEnabled() == false){
            dashboardString = dashboardJsons.get("normalDashboard").toString();
        } else {
            dashboardString = dashboardJsons.get("simDashboard").toString();
        }
    }

    private void uploadDashboard(Map<String, ? super Object> argsMap) {
        LOGGER.debug("Attempting to upload dashboard.");

        loadDashboardBasedOnSim();
        replaceFields();
        customDashboardJsonUploader.uploadDashboard(config.get("namePrefix").toString(), dashboardString, argsMap, false);

        LOGGER.debug("Dashboard Upload Successful");
    }

    private void replaceFields() {

        replaceApplicationName();
        replaceTierName();
        replaceNodeName();
        replaceDashboardName();
        replaceSimApplicationName();
        replaceHostName();
        replaceMachinePath();
    }

    private void replaceHostName() {
        if(dashboardString.contains("replaceHostName")){
            LOGGER.debug("replaceHostName: {}", controllerInfo.getControllerHost());

            if(controllerInfo.getControllerHost() != null) {
                dashboardString = dashboardString.replace("replaceHostName", controllerInfo.getControllerHost());
            }
        }
    }

    private void replaceSimApplicationName() {
        if(dashboardString.contains("replaceSimApplicationName")){
            LOGGER.debug("replaceSimApplicationName: {}", "Server & Infrastructure Monitoring");

            dashboardString = dashboardString.replace("replaceSimApplicationName", "Server & Infrastructure Monitoring");
        }
    }

    private void replaceDashboardName() {
        if(dashboardString.contains("replaceDashboardName")){
            LOGGER.debug("replaceDashboardName: {}", config.get("namePrefix").toString());

            if(config.get("namePrefix") != null) {
                dashboardString = dashboardString.replace("replaceDashboardName", config.get("namePrefix").toString());
            }
        }
    }

    private void replaceNodeName() {
        if(dashboardString.contains("replaceNodeName")){
            LOGGER.debug("replaceNodeName: {}", controllerInfo.getNodeName());

            if(controllerInfo.getNodeName() != null){
            dashboardString = dashboardString.replace("replaceNodeName", controllerInfo.getNodeName());
            }
        }
    }

    private void replaceTierName() {
        if(dashboardString.contains("replaceTierName")){
            LOGGER.debug("replaceTierName: {}", controllerInfo.getTierName());

            if(controllerInfo.getTierName() != null) {
                dashboardString = dashboardString.replace("replaceTierName", controllerInfo.getTierName());
            }
        }
    }

    private void replaceApplicationName() {
        if(dashboardString.contains("replaceApplicationName")){
            LOGGER.debug("replaceApplicationName : {}", controllerInfo.getApplicationName());
            if(controllerInfo.getApplicationName() != null){
                dashboardString = dashboardString.replace("replaceApplicationName", controllerInfo.getApplicationName());
            }
        }
    }

    private void replaceMachinePath() {
        if(dashboardString.contains("replaceMachinePath")){
            LOGGER.debug("replaceMachinePath: {}", controllerInfo.getMachinePath());

            if(controllerInfo.getNodeName() != null){
                dashboardString = dashboardString.replace("replaceMachinePath", controllerInfo.getMachinePath());
            } else {
                dashboardString = dashboardString.replace("replaceMachinePath", "Root");

            }
        }
    }


}


