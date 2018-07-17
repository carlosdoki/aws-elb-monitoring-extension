/*
 *   Copyright 2018 . AppDynamics LLC and its affiliates.
 *   All Rights Reserved.
 *   This is unpublished proprietary source code of AppDynamics LLC and its affiliates.
 *   The copyright notice above does not evidence any actual or intended publication of such source code.
 *
 */

package com.appdynamics.extensions.aws.elb.config;

import com.appdynamics.extensions.aws.config.Configuration;

import java.util.Map;

public class ELBConfiguration extends Configuration {

    private Map customDashboard;

    public Map getCustomDashboard() {
        return customDashboard;
    }

    public void setCustomDashboard(Map customDashboard) {
        this.customDashboard = customDashboard;
    }


}
