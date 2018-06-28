/*
 *   Copyright 2018 . AppDynamics LLC and its affiliates.
 *   All Rights Reserved.
 *   This is unpublished proprietary source code of AppDynamics LLC and its affiliates.
 *   The copyright notice above does not evidence any actual or intended publication of such source code.
 *
 */

package com.appdynamics.extensions.aws.elb;

import com.amazonaws.services.cloudwatch.model.Metric;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import org.apache.log4j.Logger;

import java.util.List;

/**
 * Created by bhuvnesh.kumar on 6/25/18.
 */
public class ELBPredicate implements Predicate<Metric> {

    private static final Logger LOGGER = Logger.getLogger(ELBPredicate.class);


    private List<String> includeDimensionValueName;
    private Predicate<CharSequence> patternPredicate;

    public ELBPredicate(List<String> includeDimensionValueName){
        this.includeDimensionValueName = includeDimensionValueName;
        build();
    }

    private void build(){
        if(includeDimensionValueName != null && !includeDimensionValueName.isEmpty()){
            for(String pattern : includeDimensionValueName){
                Predicate<CharSequence> charSequencePredicate = Predicates.containsPattern(pattern);
                if(patternPredicate == null){
                    patternPredicate = charSequencePredicate;
                } else {
                    patternPredicate = Predicates.or(patternPredicate, charSequencePredicate);
                }
            }
        }else {
            LOGGER.warn("includeDimensionValueName in config.yml not configured, hence not monitoring any tableNames");
        }
    }

    public boolean apply(Metric metric){
        if (patternPredicate == null) {
            return false;
        }

        String dimensionValueName = metric.getDimensions().get(0).getValue();

        return patternPredicate.apply(dimensionValueName);
    }

    public Predicate<CharSequence> getPatternPredicate(){
        return patternPredicate;
    }
}
