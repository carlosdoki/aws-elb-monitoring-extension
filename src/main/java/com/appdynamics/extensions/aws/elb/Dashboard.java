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
import static com.appdynamics.extensions.aws.elb.Constants.*;

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
            if (config.get(ENALBED).toString().equals(TRUE)) {
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

        String user = "singularity-agent" + "@" + controllerInfo.getAccount();
        String password = controllerInfo.getPassword().toString();
//        String user = config.get(USERNAME).toString() + "@" + controllerInfo.getAccount();

        LOGGER.debug("dashboard Controller Info given to extension: ");
        LOGGER.debug("dashboard Host : " + controllerInfo.getControllerHost());
        LOGGER.debug("dashboard Port : " + controllerInfo.getControllerPort());
        LOGGER.debug("dashboard User : " + user);
        LOGGER.debug("dashboard Password: " + password);

//        LOGGER.debug("dashboard Password: " + config.get("password").toString());
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
        serverMap.put(TaskInputArgs.PASSWORD, password);

        serverList.add(serverMap);
        argsMap.put(SERVERS, serverList);

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
            dashboardString = dashboardJsons.get(NORMALDASHBOARD).toString();
        } else {
            dashboardString = dashboardJsons.get(SIMDASHBOARD).toString();
        }
    }

    private void uploadDashboard(Map<String, ? super Object> argsMap) {
        LOGGER.debug("Attempting to upload dashboard.");

        loadDashboardBasedOnSim();
        replaceFields();
        customDashboardJsonUploader.uploadDashboard(config.get(NAMEPREFIX).toString(), dashboardString, argsMap, false);

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
        if(dashboardString.contains(REPLACEHOSTNAME)){
            if(controllerInfo.getControllerHost() != null) {
                LOGGER.debug("replacing Host Name: {}", controllerInfo.getControllerHost());
                dashboardString = dashboardString.replace(REPLACEHOSTNAME, controllerInfo.getControllerHost());
            }
        }
    }

    private void replaceSimApplicationName() {
        if(dashboardString.contains(REPLACESIMAPPLICATIONNAME)){
            LOGGER.debug("replacing SimApplicationName: {}", SIMAPPLICATIONNAME);
            dashboardString = dashboardString.replace(REPLACESIMAPPLICATIONNAME, SIMAPPLICATIONNAME);
        }
    }

    private void replaceDashboardName() {
        if(dashboardString.contains(REPLACEDASHBOARDNAME)){
            if(config.get("namePrefix") != null) {
                LOGGER.debug("replacing DashboardName: {}", config.get(NAMEPREFIX).toString());
                dashboardString = dashboardString.replace(REPLACEDASHBOARDNAME, config.get(NAMEPREFIX).toString());
            }
        }
    }

    private void replaceNodeName() {
        if(dashboardString.contains(REPLACENODENAME)){
            if(controllerInfo.getNodeName() != null){
            dashboardString = dashboardString.replace(REPLACENODENAME, controllerInfo.getNodeName());
            LOGGER.debug("replacing NodeName: {}", controllerInfo.getNodeName());

            }
        }
    }

    private void replaceTierName() {
        if(dashboardString.contains(REPLACETIERNAME)){
            if(controllerInfo.getTierName() != null) {
                dashboardString = dashboardString.replace(REPLACETIERNAME, controllerInfo.getTierName());
                LOGGER.debug("replacing TierName: {}", controllerInfo.getTierName());

            }
        }
    }

    private void replaceApplicationName() {
        if(dashboardString.contains(REPLACEAPPLICATIONNAME)){
            if(controllerInfo.getApplicationName() != null){

                dashboardString = dashboardString.replace(REPLACEAPPLICATIONNAME, controllerInfo.getApplicationName());
                LOGGER.debug("replacing ApplicationName : {}", controllerInfo.getApplicationName());

            }
        }
    }

    private void replaceMachinePath() {
        if(dashboardString.contains(REPLACEMACHINEPATH)){

            if(controllerInfo.getNodeName() != null){
                dashboardString = dashboardString.replace(REPLACEMACHINEPATH, controllerInfo.getMachinePath());
                LOGGER.debug("replacing MachinePath: {}", controllerInfo.getMachinePath());

            } else {
                dashboardString = dashboardString.replace(REPLACEMACHINEPATH, ROOT);
                LOGGER.debug("replacing MachinePath: to default Root");


            }
        }
    }


}


