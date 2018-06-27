/*
 *   Copyright 2018 . AppDynamics LLC and its affiliates.
 *   All Rights Reserved.
 *   This is unpublished proprietary source code of AppDynamics LLC and its affiliates.
 *   The copyright notice above does not evidence any actual or intended publication of such source code.
 *
 */

package com.appdynamics.extensions.aws.elb;

import com.amazonaws.services.cloudwatch.model.Dimension;
import com.amazonaws.services.cloudwatch.model.Metric;
import com.google.common.collect.Lists;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.when;

/**
 * Created by bhuvnesh.kumar on 6/27/18.
 */

@RunWith(PowerMockRunner.class)

public class ELBPredicateTest {
    @Mock
    private Metric metric;

    @Mock
    private Dimension dimension;

    @Test
    public void testNullIncludeDimensionNamesShouldReturnFalse() {
        List<String> includeTableNames = null;
        ELBPredicate classUnderTest = new ELBPredicate(includeTableNames);
        Assert.assertFalse(classUnderTest.apply(metric));
    }

    @Test
    public void testEmptyIncludeDimensionNamesShouldReturnFalse() {
        List<String> includeTableNames = new ArrayList();
        ELBPredicate classUnderTest = new ELBPredicate(includeTableNames);
        Assert.assertFalse(classUnderTest.apply(metric));
    }

    @Test
    public void testWildCardIncludeDimensionNamesShouldReturnTrue() {
        List<String> includeTableNames = Lists.newArrayList(".*");
        ELBPredicate classUnderTest = new ELBPredicate(includeTableNames);
        when(metric.getDimensions()).thenReturn(Lists.newArrayList(dimension));
        when(dimension.getValue()).thenReturn("Dimension");
        Assert.assertTrue(classUnderTest.apply(metric));
    }

    @Test
    public void testIncludeDimensionNamesMatching1ShouldReturnTrue() {
        List<String> includeTableNames = Lists.newArrayList("^Dimension$");
        ELBPredicate classUnderTest = new ELBPredicate(includeTableNames);
        when(metric.getDimensions()).thenReturn(Lists.newArrayList(dimension));
        when(dimension.getValue()).thenReturn("Dimension");
        Assert.assertTrue(classUnderTest.apply(metric));
    }

    @Test
    public void testIncludeDimensionNamesMatching2ShouldReturnTrue() {
        List<String> includeTableNames = Lists.newArrayList("Dimension", "test", "");
        ELBPredicate classUnderTest = new ELBPredicate(includeTableNames);
        when(metric.getDimensions()).thenReturn(Lists.newArrayList(dimension));
        when(dimension.getValue()).thenReturn("Dimension");
        Assert.assertTrue(classUnderTest.apply(metric));
    }

    @Test
    public void testIncludeDimensionNamesContainsShouldReturnTrue() {
        List<String> includeTableNames = Lists.newArrayList("Dim.*");
        ELBPredicate classUnderTest = new ELBPredicate(includeTableNames);
        when(metric.getDimensions()).thenReturn(Lists.newArrayList(dimension));
        when(dimension.getValue()).thenReturn("Dimensions");
        Assert.assertTrue(classUnderTest.apply(metric));
    }


    @Test
    public void testIncludeTableNamesNotMatchingShouldReturnFalse() {
        List<String> includeTableNames = Lists.newArrayList("Dimension", "test");
        ELBPredicate classUnderTest = new ELBPredicate(includeTableNames);
        when(metric.getDimensions()).thenReturn(Lists.newArrayList(dimension));
        when(dimension.getValue()).thenReturn("Hammer");
        Assert.assertFalse(classUnderTest.apply(metric));
    }

}
