/*
 * Copyright 2018. AppDynamics LLC and its affiliates.
 * All Rights Reserved.
 * This is unpublished proprietary source code of AppDynamics LLC and its affiliates.
 * The copyright notice above does not evidence any actual or intended publication of such source code.
 *
 */

package com.appdynamics.extensions.aws.elb;

import com.appdynamics.extensions.aws.SingleNamespaceCloudwatchMonitor;
import com.appdynamics.extensions.aws.collectors.NamespaceMetricStatisticsCollector;
import com.appdynamics.extensions.aws.elb.config.ELBConfiguration;
import com.appdynamics.extensions.aws.metric.processors.MetricsProcessor;
import com.appdynamics.extensions.dashboard.CustomDashboardJsonUploader;
import com.singularity.ee.agent.systemagent.api.exception.TaskExecutionException;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;

import java.io.File;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.Map;

import static com.appdynamics.extensions.aws.elb.Constants.*;

//import org.slf4j.Logger;

/**
 * @author Bhuvnesh Kumar
 */
public class ELBMonitor extends SingleNamespaceCloudwatchMonitor<ELBConfiguration> {

//    private static final Logger logger = Logger.getLogger(ELBMonitor.class);

    private static final Logger LOGGER = Logger.getLogger(ELBMonitor.class);
//    private static final org.slf4j.Logger LOGGER = ExtensionsLoggerFactory.getLogger(ELBMonitor.class);

    private static final String DEFAULT_METRIC_PREFIX = String.format("%s%s%s%s",
            CUSTOM_METRICS, METRICS_SEPARATOR, AMAZON_SERVICE, METRICS_SEPARATOR);

    private Map dashboardValueMap;
    private Dashboard dashboard;
    private Map dashboardJsons;

    public ELBMonitor() {
        super(ELBConfiguration.class);
    }

    @Override
    public String getDefaultMetricPrefix() {
        return DEFAULT_METRIC_PREFIX;
    }

    @Override
    public String getMonitorName() {
        return MONITOR_NAME;
    }

    @Override
    protected int getTaskCount() {
        return 3;
    }

    @Override
    protected void initialize(ELBConfiguration config) {
        super.initialize(config);
    }


    @Override
    protected NamespaceMetricStatisticsCollector getNamespaceMetricsCollector(
            ELBConfiguration config) {

        dashboardValueMap = config.getCustomDashboard();
        dashboard = new Dashboard(dashboardValueMap, dashboardJsons, new CustomDashboardJsonUploader());
        LOGGER.debug("Dashboard.class object Successfully Created");

        MetricsProcessor metricsProcessor = createMetricsProcessor(config);


        return new NamespaceMetricStatisticsCollector
                .Builder(config.getAccounts(),
                config.getConcurrencyConfig(),
                config.getMetricsConfig(),
                metricsProcessor,
                config.getMetricPrefix())
                .withCredentialsDecryptionConfig(config.getCredentialsDecryptionConfig())
                .withProxyConfig(config.getProxyConfig())
                .build();
    }

    @Override
    protected void initializeMoreStuff(Map<String, String> args) {
        LOGGER.debug("Getting dashboard args in initializeMoreStuff");
        try {
            dashboardJsons = new HashMap();
            dashboardJsons.put(NORMAL_DASHBOARD, FileUtils.readFileToString(new File(args.get(NORMAL_DASHBOARD))));
            dashboardJsons.put(SIM_DASHBOARD, FileUtils.readFileToString(new File(args.get(SIM_DASHBOARD))));

        } catch (Exception e) {
            LOGGER.error("Unable to get files for dashboard", e);
        }

        LOGGER.debug("Done with initializeMoreStuff");

    }

    @Override
    protected Logger getLogger() {

        return LOGGER;
    }

    private MetricsProcessor createMetricsProcessor(ELBConfiguration config) {
        return new ELBMetricsProcessor(
                config.getMetricsConfig().getIncludeMetrics(),
                config.getDimensions(), dashboard);
    }


    public static void main(String[] args) throws TaskExecutionException {

        ConsoleAppender ca = new ConsoleAppender();
        ca.setWriter(new OutputStreamWriter(System.out));
        ca.setLayout(new PatternLayout("%-5p [%t]: %m%n"));
        ca.setThreshold(Level.DEBUG);


//        logger.getRootLogger().addAppender(ca);

        ELBMonitor monitor = new ELBMonitor();


        Map<String, String> taskArgs = new HashMap<String, String>();

        taskArgs.put("config-file", "//Applications/AppDynamics/ma43/monitors/AWSELBMonitor_dash/config.yml");
        taskArgs.put("normalDashboard", "//Applications/AppDynamics/ma43/monitors/AWSELBMonitor_dash/normalDashboard.json");
        taskArgs.put("simDashboard", "//Applications/AppDynamics/ma43/monitors/AWSELBMonitor_dash/simDashboard.json");


        monitor.execute(taskArgs, null);

    }


}
