/*
 * Copyright 2018. AppDynamics LLC and its affiliates.
 * All Rights Reserved.
 * This is unpublished proprietary source code of AppDynamics LLC and its affiliates.
 * The copyright notice above does not evidence any actual or intended publication of such source code.
 *
 */

package com.appdynamics.extensions.aws.elb;

import com.amazonaws.services.cloudwatch.AmazonCloudWatch;
import com.amazonaws.services.cloudwatch.model.DimensionFilter;
import com.appdynamics.extensions.aws.config.Dimension;
import com.appdynamics.extensions.aws.config.IncludeMetric;
import com.appdynamics.extensions.aws.dto.AWSMetric;
import com.appdynamics.extensions.aws.metric.NamespaceMetricStatistics;
import com.appdynamics.extensions.aws.metric.StatisticType;
import com.appdynamics.extensions.aws.metric.processors.MetricsProcessor;
import com.appdynamics.extensions.aws.metric.processors.MetricsProcessorHelper;
import com.appdynamics.extensions.aws.predicate.MultiDimensionPredicate;
import com.appdynamics.extensions.logging.ExtensionsLoggerFactory;
import org.apache.log4j.Logger;

import java.util.*;
import java.util.concurrent.atomic.LongAdder;
import static com.appdynamics.extensions.aws.elb.Constants.*;


public class ELBMetricsProcessor implements MetricsProcessor {

    private static final org.slf4j.Logger LOGGER = ExtensionsLoggerFactory.getLogger(ELBMetricsProcessor.class);


    private List<IncludeMetric> includeMetrics;
    private List<Dimension> dimensions;
    private static final String NAMESPACE = AWSELB;
    private Dashboard dashboard;


    public ELBMetricsProcessor(List<IncludeMetric> includeMetrics, List<Dimension> dimensions, Dashboard dashboard) {
        this.includeMetrics = includeMetrics;
        this.dimensions = dimensions;
        this.dashboard = dashboard;
    }

    public List<AWSMetric> getMetrics(AmazonCloudWatch awsCloudWatch, String accountName, LongAdder awsRequestsCounter) {
        List<DimensionFilter> dimensionFilters = getDimensionFilters();

        MultiDimensionPredicate predicate = new MultiDimensionPredicate(dimensions);

        return MetricsProcessorHelper.getFilteredMetrics(awsCloudWatch, awsRequestsCounter,
                NAMESPACE,
                includeMetrics,
                dimensionFilters,
                predicate);
    }

    private List<DimensionFilter> getDimensionFilters() {
        List<DimensionFilter> dimensionFilters = new ArrayList<DimensionFilter>();
        for (Dimension dimension : dimensions) {
            DimensionFilter dimensionFilter = new DimensionFilter();
            dimensionFilter.withName(dimension.getName());
            dimensionFilters.add(dimensionFilter);
        }
        return dimensionFilters;
    }

    public StatisticType getStatisticType(AWSMetric metric) {
        return MetricsProcessorHelper.getStatisticType(metric.getIncludeMetric(), includeMetrics);
    }

    public List<com.appdynamics.extensions.metrics.Metric> createMetricStatsMapForUpload(NamespaceMetricStatistics namespaceMetricStats) {
        Map<String, String> dimensionToMetricPathNameDictionary = new HashMap<String, String>();

        for (Dimension dimension : dimensions) {
            dimensionToMetricPathNameDictionary.put(dimension.getName(), dimension.getDisplayName());
        }
        LOGGER.debug("In Metric Processor going to upload dashboard");

        Thread dashboardThread = new Thread(new Runnable() {
            public void run() {
                LOGGER.debug("Creating a new thread to send the dashboard");
                dashboard.sendDashboard();
            }
        });
        dashboardThread.start();

        LOGGER.debug("Created Thread for Dashboard Upload");


        return MetricsProcessorHelper.createMetricStatsMapForUpload(namespaceMetricStats,
                dimensionToMetricPathNameDictionary, false);
    }

    public String getNamespace() {
        return NAMESPACE;
    }


}
