/*
 *   Copyright 2018 . AppDynamics LLC and its affiliates.
 *   All Rights Reserved.
 *   This is unpublished proprietary source code of AppDynamics LLC and its affiliates.
 *   The copyright notice above does not evidence any actual or intended publication of such source code.
 *
 */

package com.appdynamics.extensions.aws.elb;

import com.appdynamics.extensions.TaskInputArgs;
import com.appdynamics.extensions.dashboard.CustomDashboardTask;
import com.appdynamics.extensions.dashboard.CustomDashboardUploader;
import com.appdynamics.extensions.xml.Xml;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

import java.io.File;
import java.util.*;

/**
 * Created by bhuvnesh.kumar on 7/5/18.
 */
public class Dashboard {

    private Map config;
    private CustomDashboardUploader dashboardUploader;
    private static final Logger LOGGER = Logger.getLogger(Dashboard.class);
    private String dashboardXML;


    public Dashboard(Map config, String dashboardXML) {
        LOGGER.debug("INTERNAL Setting up DashboardClass");

        this.config = config;
        this.dashboardXML = dashboardXML;

        LOGGER.debug("INTERNAL leaving DashboardClass");

    }

    protected void sendDashboard() {
        try {
//            String pathToDashboard = "monitors/AWSELBMonitor/dashboard.xml";
//            String content = FileUtils.readFileToString(new File(pathToDashboard));

//            LOGGER.debug("#######################");
//            LOGGER.debug("The following is the information under custom dashboard and values of dashboard.xml");
//            LOGGER.debug("Dashboard.xml =" );
//            LOGGER.debug(dashboardXML);
//            LOGGER.debug("#######################");
//            LOGGER.debug("The values of config");
//            LOGGER.debug("Host :" + config.get("host").toString() + "Port : " + config.get("port").toString());
//            LOGGER.debug("#######################");
//            LOGGER.debug("Application Name : " + config.get("applicationName").toString() + " Tier Name : "+ config.get("tierName").toString() +  "Node Name : " + config.get("nodeName").toString());
//            LOGGER.debug("#######################");



            dashboardUploader = new CustomDashboardUploader();
            LOGGER.debug("created CustomDashboardUploader");

            Map<String,? super Object> argsMap = new HashMap<>();

            List<Map<String, ?>> serverList = new ArrayList<>();
            Map<String, ? super Object> serverMap = new HashMap<>();
            serverMap.put(TaskInputArgs.HOST, config.get("host").toString());
            serverMap.put(TaskInputArgs.PORT, config.get("port").toString());
            serverMap.put(TaskInputArgs.USE_SSL, false);
            serverMap.put(TaskInputArgs.USER, config.get("username").toString());
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


//            Map servers = new HashMap();
//            servers.put("servers", config);

            dashboardUploader.uploadDashboard(config.get("namePrefix").toString(), Xml.fromString(dashboardXML), argsMap, false);

        }catch (Exception e){
            LOGGER.debug("Unable to upload dashboard", e);
        }
    }
}
