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

import static com.ericsson.oss.air.cardq.utils.Constants.LOCAL_DN;
import static com.ericsson.oss.air.cardq.utils.Constants.MEAS_OBJ_LDN;
import static com.ericsson.oss.air.cardq.utils.Constants.NODE_FDN;
import static com.ericsson.oss.air.cardq.utils.Constants.PLMNID;
import static com.ericsson.oss.air.cardq.utils.Constants.QOS;
import static com.ericsson.oss.air.cardq.utils.Constants.SNSSAI;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ericsson.oss.air.cardq.handler.util.mofdn.MoFdnFactory;
import com.ericsson.oss.air.cardq.utils.GraphServiceType;
import org.apache.commons.lang3.function.TriFunction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class AthQueryBuilderTest {
    private static final String CORE_QUERY_TEMPLATE = """
            MATCH (wnf:wirelessNetworkFunction {$fdn}) <-[:networkSliceSubnets_to_wirelessNetFunction]- (nssi1:networkslicesubnet),
            (nssi1) <-[:networkSliceSubnet_to_networkSliceSubnet*]- (nssi2:networkslicesubnet),
            (nssi2) <-[:networkSlice_to_networkSliceSubnet]- (nsi:networkslice),
            (nsi) -[:networkSlice_to_requirement_serviceProfiles]-> (servicep:serviceprofile),
            (servicep) -[:serviceProfile_to_plmnInfo]-> (plmn:plmninfo {$plmn}),
            (wnf) <-[:wirelessNetFunction_to_site]- (site:geographicsite)
            OPTIONAL MATCH (nssi1) -[:networkSliceSubnet_to_requirement_sliceProfile]-> (slicep:sliceprofile) -[:SliceProfile_to_plmnInfo]-> (plmn)
            RETURN DISTINCT nssi1.name AS nssi1,
            nssi2.name AS nssi2,
            site.name AS site,
            wnf.ExternalRef_nodeFDN AS nodeFDN,
            plmn.sNSSAI_SST + '-' + plmn.sNSSAI_SD AS snssai
            """;

    private static final String COMPILED_CORE_QUERY_TEMPLATE = """
            MATCH (wnf:wirelessNetworkFunction {ExternalRef_nodeFDN: 'MeContext=PCG00032,ManagedElement=PCC00032'}) <-[:networkSliceSubnets_to_wirelessNetFunction]- (nssi1:networkslicesubnet),
            (nssi1) <-[:networkSliceSubnet_to_networkSliceSubnet*]- (nssi2:networkslicesubnet),
            (nssi2) <-[:networkSlice_to_networkSliceSubnet]- (nsi:networkslice),
            (nsi) -[:networkSlice_to_requirement_serviceProfiles]-> (servicep:serviceprofile),
            (servicep) -[:serviceProfile_to_plmnInfo]-> (plmn:plmninfo {sNSSAI_SST: '83', sNSSAI_SD: '5'}),
            (wnf) <-[:wirelessNetFunction_to_site]- (site:geographicsite)
            OPTIONAL MATCH (nssi1) -[:networkSliceSubnet_to_requirement_sliceProfile]-> (slicep:sliceprofile) -[:SliceProfile_to_plmnInfo]-> (plmn)
            RETURN DISTINCT nssi1.name AS nssi1,
            nssi2.name AS nssi2,
            site.name AS site,
            wnf.ExternalRef_nodeFDN AS nodeFDN,
            plmn.sNSSAI_SST + '-' + plmn.sNSSAI_SD AS snssai
            """;
    private static final String RAN_QUERY_TEMPLATE = """
            MATCH (nrcell:nrcell {$fdn})
            OPTIONAL MATCH (nrcell) -[:nrCell_to_plmnInfo]-> (plmn:plmninfo {$plmn}),
            (plmn) <-[:SliceProfile_to_plmnInfo]- (sliceProf:sliceprofile),
            (sliceProf) <-[:networkSliceSubnet_to_requirement_sliceProfile]- (nssi:networkslicesubnet)
            OPTIONAL MATCH (nrcell) <-[:nrCells_to_gnbdu]- (gnbdu:gnbdu),
            (gnbdu) -[:wirelessNetworkFunctionHas5QISets]-> (fiveqiset:fiveqiset),
            (fiveqiset) -[:fiveQISetContainsFiveQIFlows]-> (fiveqiflow:fiveqiflow {$qos})
            RETURN nssi.name AS nssi,
            nrcell.trackingAreaCode AS tac,
            nrcell.localCellIdNci AS cellId,
            plmn.plmn_mcc + '-' + plmn.plmn_mnc AS plmnId,
            plmn.sNSSAI_SST + '-' + plmn.sNSSAI_SD AS snssai,
            nrcell.ExternalRef_nrCellDuFDN AS moFDN
            """;

    private static final String COMPILED_RAN_QUERY_TEMPLATE = """
            MATCH (nrcell:nrcell {ExternalRef_nrCellDuFDN: 'SubNetwork=RAN,MeContext=gNBh96Mac02,ManagedElement=gNBh96Mac02,GNBDUFunction=1,NRCellDU=1'})
            OPTIONAL MATCH (nrcell) -[:nrCell_to_plmnInfo]-> (plmn:plmninfo {plmn_mcc: '808', plmn_mnc: '81', sNSSAI_SST: '1', sNSSAI_SD: '9'}),
            (plmn) <-[:SliceProfile_to_plmnInfo]- (sliceProf:sliceprofile),
            (sliceProf) <-[:networkSliceSubnet_to_requirement_sliceProfile]- (nssi:networkslicesubnet)
            OPTIONAL MATCH (nrcell) <-[:nrCells_to_gnbdu]- (gnbdu:gnbdu),
            (gnbdu) -[:wirelessNetworkFunctionHas5QISets]-> (fiveqiset:fiveqiset),
            (fiveqiset) -[:fiveQISetContainsFiveQIFlows]-> (fiveqiflow:fiveqiflow {fiveQIValue: '1'})
            RETURN nssi.name AS nssi,
            nrcell.trackingAreaCode AS tac,
            nrcell.localCellIdNci AS cellId,
            plmn.plmn_mcc + '-' + plmn.plmn_mnc AS plmnId,
            plmn.sNSSAI_SST + '-' + plmn.sNSSAI_SD AS snssai,
            nrcell.ExternalRef_nrCellDuFDN AS moFDN
            """;

    private static final String COMPILED_RAN_QUERY_TEMPLATE_MISSING_PLMNID_MISSING_QOS = """
            MATCH (nrcell:nrcell {ExternalRef_nrCellDuFDN: 'SubNetwork=RAN,MeContext=gNBh96Mac02,ManagedElement=gNBh96Mac02,GNBDUFunction=1,NRCellDU=1'})
            OPTIONAL MATCH (nrcell) -[:nrCell_to_plmnInfo]-> (plmn:plmninfo {sNSSAI_SST: '1', sNSSAI_SD: '9'}),
            (plmn) <-[:SliceProfile_to_plmnInfo]- (sliceProf:sliceprofile),
            (sliceProf) <-[:networkSliceSubnet_to_requirement_sliceProfile]- (nssi:networkslicesubnet)
            OPTIONAL MATCH (nrcell) <-[:nrCells_to_gnbdu]- (gnbdu:gnbdu),
            (gnbdu) -[:wirelessNetworkFunctionHas5QISets]-> (fiveqiset:fiveqiset),
            (fiveqiset) -[:fiveQISetContainsFiveQIFlows]-> (fiveqiflow:fiveqiflow {})
            RETURN nssi.name AS nssi,
            nrcell.trackingAreaCode AS tac,
            nrcell.localCellIdNci AS cellId,
            plmn.plmn_mcc + '-' + plmn.plmn_mnc AS plmnId,
            plmn.sNSSAI_SST + '-' + plmn.sNSSAI_SD AS snssai,
            nrcell.ExternalRef_nrCellDuFDN AS moFDN
            """;

    private static final boolean WITH_PLMNID = true;
    private MoFdnFactory moFdnFactory = new MoFdnFactory();

    private AthQueryBuilder athQueryBuilder;

    private TriFunction<String, Map<String, String>, GraphServiceType, String> fdnFunction;

    private TriFunction<String, Map<String, String>, GraphServiceType, String> plmnFunction;

    private TriFunction<String, Map<String, String>, GraphServiceType, String> qosFunction;

    private List<TriFunction<String, Map<String, String>, GraphServiceType, String>> queryBuilderFns;

    @BeforeEach
    public void setup() {
        this.athQueryBuilder = new AthQueryBuilder(this.moFdnFactory);
        this.fdnFunction = this.athQueryBuilder.fdnBuilder;
        this.plmnFunction = this.athQueryBuilder.plmnBuilder;
        this.qosFunction = this.athQueryBuilder.qosBuilder;
    }

    @Test
    void testFdnBuilder_core_with_plmnId() {
        final Map<String, String> parameters = createCoreParameters(WITH_PLMNID);

        String result = "(wnf:wirelessNetworkFunction {$fdn})";
        result = this.fdnFunction.apply(result, parameters, GraphServiceType.CORE);

        assertEquals("(wnf:wirelessNetworkFunction {ExternalRef_nodeFDN: 'MeContext=PCG00032,ManagedElement=PCC00032'})", result);
    }

    @Test
    void testFdnBuilder_ran_with_plmnId() {
        final Map<String, String> parameters = createRanParameters_NRCellDU(WITH_PLMNID);

        String result = "(wnf:wirelessNetworkFunction {$fdn})";
        result = this.fdnFunction.apply(result, parameters, GraphServiceType.RAN);

        assertEquals("(wnf:wirelessNetworkFunction {ExternalRef_nrCellDuFDN: 'SubNetwork=RAN,MeContext=gNBh96Mac02,ManagedElement=gNBh96Mac02,GNBDUFunction=1,NRCellDU=1'})", result);
    }

    @Test
    void testPlmnBuilder_core_with_plmnId() {
        final Map<String, String> parameters = createCoreParameters(WITH_PLMNID);

        String result = "(plmn:plmninfo {$plmn})";
        result = this.plmnFunction.apply(result, parameters, GraphServiceType.CORE);

        assertEquals("(plmn:plmninfo {plmn_mcc: '808', plmn_mnc: '81', sNSSAI_SST: '83', sNSSAI_SD: '5'})", result);
    }

    @Test
    void testPlmnBuilder_core_missing_plmnId() {
        final Map<String, String> parameters = createCoreParameters(!WITH_PLMNID);

        String result = "(plmn:plmninfo {$plmn})";
        result = this.plmnFunction.apply(result, parameters, GraphServiceType.CORE);

        assertEquals("(plmn:plmninfo {sNSSAI_SST: '83', sNSSAI_SD: '5'})", result);
    }

    @Test
    void testPlmnBuilder_ran_with_plmnId() {
        final Map<String, String> parameters = createRanParameters_NRCellDU(WITH_PLMNID);

        String result = "(plmn:plmninfo {$plmn})";
        result = this.plmnFunction.apply(result, parameters, GraphServiceType.RAN);

        assertEquals("(plmn:plmninfo {plmn_mcc: '808', plmn_mnc: '81', sNSSAI_SST: '1', sNSSAI_SD: '9'})", result);
    }

    @Test
    void testPlmnBuilder_ran_missing_plmnId() {
        final Map<String, String> parameters = createCoreParameters(!WITH_PLMNID);

        String result = "(fiveqiflow:fiveqiflow {$qos})";
        result = this.qosFunction.apply(result, parameters, GraphServiceType.RAN);

        assertEquals("(fiveqiflow:fiveqiflow {})", result);
    }

    @Test
    void testQosBuilder() {
        final Map<String, String> parameters = createRanParameters_NRCellDU(WITH_PLMNID);

        String result = "(fiveqiflow:fiveqiflow {$qos})";
        result = this.qosFunction.apply(result, parameters, GraphServiceType.RAN);

        assertEquals("(fiveqiflow:fiveqiflow {fiveQIValue: '1'})", result);
    }

    @Test
    void testQosBuilder_missing_qos() {
        final Map<String, String> parameters = createCoreParameters(!WITH_PLMNID);

        String result = "(fiveqiflow:fiveqiflow {$qos})";
        result = this.qosFunction.apply(result, parameters, GraphServiceType.RAN);

        assertEquals("(fiveqiflow:fiveqiflow {})", result);
    }

    @Test
    void testBuild_core() {
        final Map<String, String> parameters = createCoreParameters(!WITH_PLMNID);

        assertEquals(COMPILED_CORE_QUERY_TEMPLATE, this.athQueryBuilder.build(CORE_QUERY_TEMPLATE, parameters, GraphServiceType.CORE));
    }

    @Test
    void testBuild_ran () {
        final Map<String, String> parameters = createRanParameters_NRCellDU(WITH_PLMNID);

        assertEquals(COMPILED_RAN_QUERY_TEMPLATE, this.athQueryBuilder.build(RAN_QUERY_TEMPLATE, parameters, GraphServiceType.RAN));
    }

    @Test
    void testBuild_ran_missingPlmnId_missingQos () {
        final Map<String, String> parameters = new HashMap<>();
        parameters.put(LOCAL_DN, "SubNetwork=RAN,MeContext=gNBh96Mac02,ManagedElement=gNBh96Mac02");
        parameters.put(MEAS_OBJ_LDN, "ManagedElement=gNBh96Mac02,GNBDUFunction=1,NRCellDU=1");
        parameters.put(SNSSAI, "1-9");

        assertEquals(COMPILED_RAN_QUERY_TEMPLATE_MISSING_PLMNID_MISSING_QOS, this.athQueryBuilder.build(RAN_QUERY_TEMPLATE, parameters, GraphServiceType.RAN));
    }

    private Map<String, String> createCoreParameters(final boolean plmnIdExisted) {
        final Map<String, String> parameters = new HashMap<>();
        parameters.put(NODE_FDN, "MeContext=PCG00032,ManagedElement=PCC00032");
        parameters.put(SNSSAI, "83-5");

        if (plmnIdExisted) {
            parameters.put(PLMNID, "808-81");
        }
        return parameters;
    }

    private Map<String, String> createRanParameters_NRCellDU(final boolean plmnIdExisted) {
        final Map<String, String> parameters = new HashMap<>();
        parameters.put(LOCAL_DN, "SubNetwork=RAN,MeContext=gNBh96Mac02,ManagedElement=gNBh96Mac02");
        parameters.put(MEAS_OBJ_LDN, "ManagedElement=gNBh96Mac02,GNBDUFunction=1,NRCellDU=1");
        parameters.put(SNSSAI, "1-9");
        parameters.put(QOS, "1");

        if (plmnIdExisted) {
            parameters.put(PLMNID, "808-81");
        }
        return parameters;
    }

}