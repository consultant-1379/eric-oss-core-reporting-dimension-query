/*******************************************************************************
 * COPYRIGHT Ericsson 2021
 *
 *
 *
 * The copyright to the computer program(s) herein is the property of
 *
 * Ericsson Inc. The programs may be used and/or copied only with written
 *
 * permission from Ericsson Inc. or in accordance with the terms and
 *
 * conditions stipulated in the agreement/contract under which the
 *
 * program(s) have been supplied.
 ******************************************************************************/
package com.ericsson.oss.air.cardq.services;

import java.util.List;
import java.util.Map;

import com.ericsson.oss.air.cardq.cache.Graph;
import com.ericsson.oss.air.cardq.utils.GraphServiceType;

public interface GraphService {

    GraphServiceType getType();

    Graph createGraph(String id);

    List<List<Map<String, String>>> navigateGraph(Map<String, String> attributes, Graph graph, List<String> augmentationFields);
}
