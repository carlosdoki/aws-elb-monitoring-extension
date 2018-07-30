/*
 *   Copyright 2018 . AppDynamics LLC and its affiliates.
 *   All Rights Reserved.
 *   This is unpublished proprietary source code of AppDynamics LLC and its affiliates.
 *   The copyright notice above does not evidence any actual or intended publication of such source code.
 *
 */

package com.appdynamics.extensions.aws.elb;

import com.appdynamics.extensions.TaskInputArgs;
import com.appdynamics.extensions.conf.ControllerInfo;
import com.appdynamics.extensions.crypto.CryptoUtil;
import com.appdynamics.extensions.dashboard.CustomDashboardJsonUploader;
import com.appdynamics.extensions.logging.ExtensionsLoggerFactory;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.appdynamics.extensions.aws.elb.Constants.*;

//import org.apache.log4j.Logger;

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

    public Dashboard(Map config, Map dashboardJsons, CustomDashboardJsonUploader customDashboardJsonUploader, ControllerInfo controllerInfo) {
        LOGGER.debug(" Setting up Dashboard Class");

        this.config = config;
        this.dashboardJsons = dashboardJsons;
        this.customDashboardJsonUploader = customDashboardJsonUploader;
        this.controllerInfo = controllerInfo;
        LOGGER.debug("Leaving Dashboard Class");

    }

    protected void sendDashboard() {
        try {
            controllerInfo = controllerInfo.getControllerInfo();
            Map<String, ? super Object> argsMap = getArgumentMap();
            if (config.get(ENALBED).toString().equals(TRUE)) {
                uploadDashboard(argsMap);
            } else {
                LOGGER.debug("Upload dashboard disabled, not uploading dashboard.");
            }

        } catch (Exception e) {
            LOGGER.error("Unable to upload dashboard", e);
        }
    }

    private Map<String, ? super Object> getArgumentMap() {
        Map<String, ? super Object> argsMap = new HashMap<>();

        String user = getUsername();
        String password = getPassword();

        logDashboardProperties(user, password);

        Map<String, ? super Object> serverMap = getServerMap(user, password);

        List<Map<String, ?>> serverList = new ArrayList<>();
        serverList.add(serverMap);
        argsMap.put(SERVERS, serverList);

        Map<String, ? super Object> connectionMap = getConnectionMap();
        argsMap.put(CONNECTION, connectionMap);
        return argsMap;
    }

    private void logDashboardProperties(String user, String password) {
        LOGGER.debug("dashboard Controller Info given to extension: ");
        LOGGER.debug("dashboard Host : " + controllerInfo.getControllerHost());
        LOGGER.debug("dashboard Port : " + controllerInfo.getControllerPort());
        LOGGER.debug("dashboard User : " + user);
        LOGGER.debug("dashboard Password: " + password);
        LOGGER.debug("dashboard UseSSL: " + controllerInfo.getControllerSslEnabled());
        LOGGER.debug("dashboard ApplicationName: {}", controllerInfo.getApplicationName());
        LOGGER.debug("dashboard TierName: {}", controllerInfo.getTierName());
        LOGGER.debug("dashboard NodeName: {}", controllerInfo.getNodeName());
        LOGGER.debug("dashboard Sim Enabled: {}", controllerInfo.getSimEnabled());
        LOGGER.debug("dashboard Machine Path: {}", controllerInfo.getMachinePath());
    }

    private Map<String, ? super Object> getServerMap(String user, String password) {
        Map<String, ? super Object> serverMap = new HashMap<>();
        serverMap.put(TaskInputArgs.HOST, controllerInfo.getControllerHost());
        serverMap.put(TaskInputArgs.PORT, controllerInfo.getControllerPort().toString());
        serverMap.put(TaskInputArgs.USE_SSL, false);
        serverMap.put(TaskInputArgs.USER, user);
        serverMap.put(TaskInputArgs.PASSWORD, password);
        return serverMap;
    }

    private Map<String, ? super Object> getConnectionMap() {
        Map<String, ? super Object> connectionMap = new HashMap<>();
        String[] sslProtocols = {TLSV_12};
        connectionMap.put(TaskInputArgs.SSL_PROTOCOL, sslProtocols);
        connectionMap.put(SSL_CERT_CHECK_ENABLED, false);
        connectionMap.put(CONNECT_TIMEOUT, 10000);
        connectionMap.put(SOCKET_TIMEOUT, 15000);
        return connectionMap;
    }

    private String getPassword() {

        // Password from startup script
        Map<String, String> taskArgs = new HashMap<>();
        if (controllerInfo.getPassword() != null) {
            taskArgs.put(ACCOUNT_ACCESS_KEY, controllerInfo.getPassword().toString());

        }
        if (controllerInfo.getEncryptedPassword() != null) {
            taskArgs.put(ENCRYPTED_PASSWORD, controllerInfo.getEncryptedPassword().toString());

        }
        if (controllerInfo.getEncryptedKey() != null) {
            taskArgs.put(ENCRYPTION_KEY, controllerInfo.getEncryptedKey().toString());
        }

        String password = CryptoUtil.getPassword(taskArgs);

        // password in the extension config
        if (password != "") {
            return password;
        } else if (config.get(PASSWORD) != null) {
            return config.get(PASSWORD).toString();
        }

        // singularity user key
        return controllerInfo.getAccountAccessKey().toString();
    }

    private String getUsername() {
        // username from startup script
        if (controllerInfo.getUsername() != null && controllerInfo.getAccount() != null) {
            return controllerInfo.getUsername() + AT + controllerInfo.getAccount();
        } else if (config.get(USERNAME) != null && controllerInfo.getAccount() != null) {
            // username from extension config
            return config.get(USERNAME).toString() + AT + controllerInfo.getAccount();
        }
        // singularity user
        return SINGULARITY_AGENT + AT + controllerInfo.getAccount();
    }

    private void loadDashboardBasedOnSim() {
        LOGGER.debug("Sim Enabled: {}", controllerInfo.getSimEnabled());

        if (controllerInfo.getSimEnabled() == false) {
            dashboardString = dashboardJsons.get(NORMAL_DASHBOARD).toString();
        } else {
            dashboardString = dashboardJsons.get(SIM_DASHBOARD).toString();
        }
    }

    private void uploadDashboard(Map<String, ? super Object> argsMap) {
        LOGGER.debug("Attempting to upload dashboard.");

        loadDashboardBasedOnSim();
        replaceFields();
        customDashboardJsonUploader.uploadDashboard(config.get(NAME_PREFIX).toString(), dashboardString, argsMap, false);

        LOGGER.debug("done with uploadDashboard()");
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
        if (dashboardString.contains(REPLACE_HOST_NAME)) {
            if (controllerInfo.getControllerHost() != null) {
                LOGGER.debug("replacing Host Name: {}", controllerInfo.getControllerHost());
                dashboardString = dashboardString.replace(REPLACE_HOST_NAME, controllerInfo.getControllerHost());
            }
        }
    }

    private void replaceSimApplicationName() {
        if (dashboardString.contains(REPLACE_SIM_APPLICATION_NAME)) {
            LOGGER.debug("replacing SimApplicationName: {}", SIM_APPLICATION_NAME);
            dashboardString = dashboardString.replace(REPLACE_SIM_APPLICATION_NAME, SIM_APPLICATION_NAME);
        }
    }

    private void replaceDashboardName() {
        if (dashboardString.contains(REPLACE_DASHBOARD_NAME)) {
            if (config.get("namePrefix") != null) {
                LOGGER.debug("replacing DashboardName: {}", config.get(NAME_PREFIX).toString());
                dashboardString = dashboardString.replace(REPLACE_DASHBOARD_NAME, config.get(NAME_PREFIX).toString());
            }
        }
    }

    private void replaceNodeName() {
        if (dashboardString.contains(REPLACE_NODE_NAME)) {
            if (controllerInfo.getNodeName() != null) {
                dashboardString = dashboardString.replace(REPLACE_NODE_NAME, controllerInfo.getNodeName());
                LOGGER.debug("replacing NodeName: {}", controllerInfo.getNodeName());

            }
        }
    }

    private void replaceTierName() {
        if (dashboardString.contains(REPLACE_TIER_NAME)) {
            if (controllerInfo.getTierName() != null) {
                dashboardString = dashboardString.replace(REPLACE_TIER_NAME, controllerInfo.getTierName());
                LOGGER.debug("replacing TierName: {}", controllerInfo.getTierName());

            }
        }
    }

    private void replaceApplicationName() {
        if (dashboardString.contains(REPLACE_APPLICATION_NAME)) {
            if (controllerInfo.getApplicationName() != null) {

                dashboardString = dashboardString.replace(REPLACE_APPLICATION_NAME, controllerInfo.getApplicationName());
                LOGGER.debug("replacing ApplicationName : {}", controllerInfo.getApplicationName());

            }
        }
    }

    private void replaceMachinePath() {
        if (dashboardString.contains(REPLACE_MACHINE_PATH)) {

            if (controllerInfo.getMachinePath() != null) {
                String machinePath = ROOT + METRICS_SEPARATOR + controllerInfo.getMachinePath();
                machinePath = machinePath.substring(0, machinePath.lastIndexOf(METRICS_SEPARATOR));

                dashboardString = dashboardString.replace(REPLACE_MACHINE_PATH, machinePath);
                LOGGER.debug("replacing MachinePath: {}", machinePath);

            } else {
                dashboardString = dashboardString.replace(REPLACE_MACHINE_PATH, ROOT);
                LOGGER.debug("replacing MachinePath: to default Root");

            }
        }
    }
}


