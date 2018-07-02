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
import com.appdynamics.extensions.aws.config.IncludeMetric;
import com.appdynamics.extensions.aws.dto.AWSMetric;
import com.appdynamics.extensions.aws.metric.NamespaceMetricStatistics;
import com.appdynamics.extensions.aws.metric.StatisticType;
import com.appdynamics.extensions.aws.metric.processors.MetricsProcessor;
import com.appdynamics.extensions.aws.metric.processors.MetricsProcessorHelper;
import org.apache.log4j.Logger;

import java.util.*;
import java.util.concurrent.atomic.LongAdder;

public class ELBMetricsProcessor implements MetricsProcessor {

    private static final Logger LOGGER = Logger.getLogger(ELBMetricsProcessor.class);


    private List<IncludeMetric> includeMetrics;
    private List<String> includeDimensionValueName;
    private String dimension;
    private static final String NAMESPACE = "AWS/ELB";

    public ELBMetricsProcessor(List<IncludeMetric> includeMetrics, List<String> includeDimensionValueName, String dimension) {
        this.includeMetrics = includeMetrics;
        this.includeDimensionValueName = includeDimensionValueName;
        this.dimension = dimension;
    }

    public List<AWSMetric> getMetrics(AmazonCloudWatch awsCloudWatch, String accountName, LongAdder awsRequestsCounter) {
        List<DimensionFilter> dimensions = getDimensionFilters();

        ELBPredicate predicate = new ELBPredicate(includeDimensionValueName);

        return MetricsProcessorHelper.getFilteredMetrics(awsCloudWatch, awsRequestsCounter,
                NAMESPACE,
                includeMetrics,
                dimensions,
                predicate);
    }

    private List<DimensionFilter> getDimensionFilters() {
        List<DimensionFilter> dimensions = new ArrayList<DimensionFilter>();
        DimensionFilter dimensionFilter = new DimensionFilter();
        dimensionFilter.withName(dimension);
        dimensions.add(dimensionFilter);
        return dimensions;
    }

    public StatisticType getStatisticType(AWSMetric metric) {
        return MetricsProcessorHelper.getStatisticType(metric.getIncludeMetric(), includeMetrics);
    }

    public List<com.appdynamics.extensions.metrics.Metric> createMetricStatsMapForUpload(NamespaceMetricStatistics namespaceMetricStats) {
        Map<String, String> dimensionToMetricPathNameDictionary = new HashMap<String, String>();
        dimensionToMetricPathNameDictionary.put(dimension, "Dimension Value");

        return MetricsProcessorHelper.createMetricStatsMapForUpload(namespaceMetricStats,
                dimensionToMetricPathNameDictionary, false);
    }

    public String getNAMESPACE() {
        return NAMESPACE;
    }

}
