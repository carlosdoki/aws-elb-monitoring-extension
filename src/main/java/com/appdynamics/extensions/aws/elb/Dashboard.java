/*
 *   Copyright 2018 . AppDynamics LLC and its affiliates.
 *   All Rights Reserved.
 *   This is unpublished proprietary source code of AppDynamics LLC and its affiliates.
 *   The copyright notice above does not evidence any actual or intended publication of such source code.
 *
 */

package com.appdynamics.extensions.aws.elb;

import com.appdynamics.extensions.dashboard.CustomDashboardTask;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by bhuvnesh.kumar on 7/5/18.
 */
public class Dashboard {

    private Map config;
    private CustomDashboardTask dashboardTask;

    //TODO try to get metrics in the form of Collection or list, .keyset() usually works with a map
    //TODO make a call to the run method with that list

    //TODO implement the updateConfig section for the dashboard and add the onFileChange section for changes in config.yml
    //TODO implement the postConfigReload method


    public Dashboard(Map config) {
        this.config = config;
    }

    private void sendDashboard(){
        dashboardTask = new CustomDashboardTask();
    }

//    private void postConfigReload() {
//        if (configuration != null && configuration.getConfigYml() != null) {
//            Map<String, ?> config = configuration.getConfigYml();
//            Set<String> instanceNames = getInstanceNames(config);
//            String metricPrefix = configuration.getMetricPrefix();
//            dashboardTask.updateConfig(instanceNames, metricPrefix, (Map) config.get("customDashboard"));
//        }
//    }


    private static Set<String> getInstanceNames(Map<String, ?> config) {
        Map instances = (Map) config.get("accounts");
        Set<String> names = new HashSet<String>();
        if (instances != null) {
            String name = (String) instances.get("displayAccountName");
            if (name != null) {
                names.add(name);
            } else {
                names.add("");
            }
        }
        return names;

    }



}
