/*******************************************************************************
 * COPYRIGHT Ericsson 2024
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
package com.ericsson.oss.air.cardq.repository;

import static com.ericsson.oss.air.cardq.utils.Constants.PLMNID;
import static com.ericsson.oss.air.cardq.utils.Constants.QOS;
import static com.ericsson.oss.air.cardq.utils.Constants.SNSSAI;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.function.TriFunction;
import org.springframework.stereotype.Component;

import com.ericsson.oss.air.cardq.config.neo4j.AthQueryConfiguration;
import com.ericsson.oss.air.cardq.handler.util.mofdn.MoFdnFactory;
import com.ericsson.oss.air.cardq.utils.GraphServiceType;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/**
 * Ath query builder class to build a compiled CORE or RAN ATH query based on pre-defined query template
 */
@Slf4j
@Component
@AllArgsConstructor
public class AthQueryBuilder {

    @Getter
    private final MoFdnFactory moFdnFactory;

    /**
     * Return the property name with value in the Neo4j database for fdn.
     *
     * <ul>
     *     <li>For the CORE graph, the property name with value is 'ExternalRef_nodeFDN: 'fdnValue''</li>
     *     <li>For the RAN graph, the property name with value is either 'ExternalRef_nrCellCuFDN: 'fdnValue''
     *     or 'ExternalRef_nrCellDuFDN: 'fdnValue'' depending on the nRCell value in moFdn</li>
     * </ul>
     */
    final TriFunction<String, Map<String, String>, GraphServiceType, String> fdnBuilder = (final String queryTemplate,
                                                                                           final Map<String, String> parameters,
                                                                                           final GraphServiceType graphServiceType) -> {
        final String fdn = this.getMoFdnFactory().getMoFdn(graphServiceType, parameters).get();
        final String externalRefName = this.getExternalRefName(graphServiceType, fdn);
        return queryTemplate.replace("$fdn", String.format("%s: '%s'", externalRefName, fdn));
    };

    /**
     * Return the property name with value in the Neo4j database for plmn.
     *
     * <ul>
     *     <li>if plmnId not present, the property name with value is 'sNSSAI_SST: 'sstValue', sNSSAI_SD: 'ssdValue''</li>
     *     <li>if plmnId present, the property name with value is
     *     'plmn_mcc: 'plmnMccValue', plmn_mnc: 'plmnMncValue', sNSSAI_SST: 'sstValue', sNSSAI_SD: 'ssdValue''</li>
     * </ul>
     */
    final TriFunction<String, Map<String, String>, GraphServiceType, String> plmnBuilder = (final String queryTemplate,
                                                                                            final Map<String, String> parameters,
                                                                                            final GraphServiceType graphServiceType) -> {
        String query = "";

        final String plmnId = parameters.get(PLMNID);
        if (ObjectUtils.isNotEmpty(plmnId) && plmnId.contains("-")) {
            final String plmnMcc = plmnId.split("-")[0];
            final String plmnMnc = plmnId.split("-")[1];
            query = String.format("plmn_mcc: '%s', plmn_mnc: '%s'", plmnMcc, plmnMnc);
        }

        final String snssai = parameters.get(SNSSAI);
        if (ObjectUtils.isNotEmpty(snssai) && snssai.contains("-")) {
            final String snssaiSst = snssai.split("-")[0];
            final String snssaiSd = snssai.split("-")[1];

            if (query.isEmpty()) {
                query = String.format("sNSSAI_SST: '%s', sNSSAI_SD: '%s'", snssaiSst, snssaiSd);
            } else {
                query += String.format(", sNSSAI_SST: '%s', sNSSAI_SD: '%s'", snssaiSst, snssaiSd);
            }
        }

        return queryTemplate.replace("$plmn", query);
    };

    /**
     * Return the property name with value in the Neo4j database for qos.
     *
     * <ul>
     *     <li>if qos not present, the property name with value is ''</li>
     *     <li>if qos present, the property name with value is 'fiveQIValue: 'qosValue''</li>
     * </ul>
     */
    final TriFunction<String, Map<String, String>, GraphServiceType, String> qosBuilder = (final String queryTemplate,
                                                                                           final Map<String, String> parameters,
                                                                                           final GraphServiceType graphServiceType) -> {
        String query = "";

        final String qos = parameters.get(QOS);
        if (ObjectUtils.isNotEmpty(qos)) {
            query = String.format("fiveQIValue: '%s'", qos);
        }

        return queryTemplate.replace("$qos", query);
    };

    /**
     * Pre-defined tri-function builder list
     */
    private final List<TriFunction<String, Map<String, String>, GraphServiceType, String>> queryBuilderFns = List.of(
            this.fdnBuilder,
            this.plmnBuilder,
            this.qosBuilder);

    /**
     * Return the compiled query string sent to Neo4j
     *
     * @param queryTemplate    predefined query template reading from {@link AthQueryConfiguration}
     * @param parameters       key value pair map for augmentation input fields
     * @param graphServiceType service type CORE or RAN
     * @return compiled query string
     */
    public String build(final String queryTemplate, final Map<String, String> parameters, final GraphServiceType graphServiceType) {
        String result = queryTemplate;

        for (final TriFunction<String, Map<String, String>, GraphServiceType, String> function : this.queryBuilderFns) {
            result = function.apply(result, parameters, graphServiceType);
        }

        return result;
    }

    /**
     * Return the property name in the Neo4j database for moFdn.
     *
     * <ul>
     *     <li>For the CORE graph, the property name is 'ExternalRef_nodeFDN'</li>
     *     <li>For the RAN graph, the property name is either 'ExternalRef_nrCellCuFDN'
     *     or 'ExternalRef_nrCellDuFDN' depending on the nRCell value in moFdn</li>
     * </ul>
     *
     * @param graphServiceType service type CORE or RAN
     * @param moFdn            FDN value
     * @return compiled FDN string
     */
    private String getExternalRefName(final GraphServiceType graphServiceType, final String moFdn) {
        if (graphServiceType.equals(GraphServiceType.CORE)) {
            return "ExternalRef_nodeFDN";
        }

        final String nRCell = moFdn.substring(moFdn.lastIndexOf(',') + 1).split("=")[0];

        if ("NRCellCU".equals(nRCell)) {
            return "ExternalRef_nrCellCuFDN";
        }

        return "ExternalRef_nrCellDuFDN";
    }
}

