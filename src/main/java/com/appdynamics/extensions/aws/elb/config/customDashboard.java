/*
 *   Copyright 2018 . AppDynamics LLC and its affiliates.
 *   All Rights Reserved.
 *   This is unpublished proprietary source code of AppDynamics LLC and its affiliates.
 *   The copyright notice above does not evidence any actual or intended publication of such source code.
 *
 */

package com.appdynamics.extensions.aws.elb.config;

/**
 * Created by bhuvnesh.kumar on 7/5/18.
 */
public class customDashboard {

    private boolean enabled;
    private String namePrefix;
    private boolean uploadDashboard;
    private int executionFrequencyMinutes;

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getNamePrefix() {
        return namePrefix;
    }

    public void setNamePrefix(String namePrefix) {
        this.namePrefix = namePrefix;
    }

    public boolean isUploadDashboard() {
        return uploadDashboard;
    }

    public void setUploadDashboard(boolean uploadDashboard) {
        this.uploadDashboard = uploadDashboard;
    }

    public int getExecutionFrequencyMinutes() {
        return executionFrequencyMinutes;
    }

    public void setExecutionFrequencyMinutes(int executionFrequencyMinutes) {
        this.executionFrequencyMinutes = executionFrequencyMinutes;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getPasswordEncrypted() {
        return passwordEncrypted;
    }

    public void setPasswordEncrypted(String passwordEncrypted) {
        this.passwordEncrypted = passwordEncrypted;
    }

    public String getEncryptionKey() {
        return encryptionKey;
    }

    public void setEncryptionKey(String encryptionKey) {
        this.encryptionKey = encryptionKey;
    }

    public String getApplicationName() {
        return applicationName;
    }

    public void setApplicationName(String applicationName) {
        this.applicationName = applicationName;
    }

    public String getTierName() {
        return tierName;
    }

    public void setTierName(String tierName) {
        this.tierName = tierName;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    private String username;
    private String password;
    private String account;

    private String passwordEncrypted;
    private String encryptionKey;

    private String applicationName;
    private String tierName;

    private String host;
    private String port;

}
