/*******************************************************************************
 * COPYRIGHT Ericsson 2022
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
package com.ericsson.oss.air.cardq.contracts.base;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;

import static com.ericsson.oss.air.cardq.utils.Constants.NSI;
import static com.ericsson.oss.air.cardq.utils.Constants.NSSI;
import static com.ericsson.oss.air.cardq.utils.Constants.QOS;
import static com.ericsson.oss.air.cardq.utils.Constants.TA;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;

import com.ericsson.oss.air.cardq.AbstractTestSetup;
import com.ericsson.oss.air.cardq.CoreApplication;
import com.ericsson.oss.air.cardq.api.model.Augmentation;
import com.ericsson.oss.air.cardq.api.model.AugmentationField;
import com.ericsson.oss.air.cardq.api.model.AugmentationFieldRequest;
import com.ericsson.oss.air.cardq.api.model.AugmentationRequest;
import com.ericsson.oss.air.cardq.api.model.QueryField;
import com.ericsson.oss.air.cardq.controller.augmentation.AugmentationApiController;
import com.ericsson.oss.air.cardq.handler.CtsAugmentationRequestHandler;

import io.restassured.module.mockmvc.RestAssuredMockMvc;

@SpringBootTest(classes = CoreApplication.class)
public class AugmentationPositiveBase extends AbstractTestSetup {

    @SpyBean
    private AugmentationApiController augmentationApiController;

    @SpyBean
    private CtsAugmentationRequestHandler augmentationRequestHandler;

    @BeforeEach
    public void setup() {

        AugmentationRequest requestRanTa = new AugmentationRequest()
                .addInputFieldsItem(new QueryField().name("localDn").value(
                        "SubNetwork=Europe,SubNetwork=Ireland,MeContext=gNodeB1,ManagedElement=gNodeB1"))
                .addInputFieldsItem(new QueryField().name("measObjLdn").value("ManagedElement=gNodeB1,GNBDUFunction=1,NRCellDU=209"))
                .addAugmentationFieldsItem(new AugmentationFieldRequest().name(TA))
                .queryType("ran");

        AugmentationRequest requestRanNoInput5Qi = new AugmentationRequest()
                .addInputFieldsItem(new QueryField().name("localDn").value(
                        "SubNetwork=DO5G,MeContext=TD3B297342,ManagedElement=TD3B297342"))
                .addInputFieldsItem(new QueryField().name("measObjLdn").value("ManagedElement=TD3B297342,GNBDUFunction=1,NRCellDU=CBC_5G_NR_1A"))
                .addAugmentationFieldsItem(new AugmentationFieldRequest().name(QOS))
                .addAugmentationFieldsItem(new AugmentationFieldRequest().name(NSI))
                .addAugmentationFieldsItem(new AugmentationFieldRequest().name(NSSI))
                .addAugmentationFieldsItem(new AugmentationFieldRequest().name(TA))
                .queryType("ran");

        AugmentationRequest requestAllAugmentationFields = new AugmentationRequest()
                .addInputFieldsItem(new QueryField().name("nodeFDN").value("MeContext=PCC00010,ManagedElement=PCC00010"))
                .addInputFieldsItem(new QueryField().name("snssai").value("1-1"))
                .addAugmentationFieldsItem(new AugmentationFieldRequest().name("nssi"))
                .addAugmentationFieldsItem(new AugmentationFieldRequest().name("nsi"))
                .addAugmentationFieldsItem(new AugmentationFieldRequest().name("site"))
                .addAugmentationFieldsItem(new AugmentationFieldRequest().name("plmnId"));

        AugmentationRequest requestAllAugmentationFieldsWithQueryType = new AugmentationRequest()
                .addInputFieldsItem(new QueryField().name("nodeFDN").value("MeContext=PCC00010,ManagedElement=PCC00010"))
                .addInputFieldsItem(new QueryField().name("snssai").value("1-1"))
                .addAugmentationFieldsItem(new AugmentationFieldRequest().name("nssi"))
                .addAugmentationFieldsItem(new AugmentationFieldRequest().name("nsi"))
                .addAugmentationFieldsItem(new AugmentationFieldRequest().name("site"))
                .addAugmentationFieldsItem(new AugmentationFieldRequest().name("plmnId"))
                .queryType("core");

        AugmentationRequest requestAllAugmentationFieldsWithoutSnssai = new AugmentationRequest()
                .addInputFieldsItem(new QueryField().name("nodeFDN").value("MeContext=PCC00010,ManagedElement=PCC00010"))
                .addAugmentationFieldsItem(new AugmentationFieldRequest().name("nssi"))
                .addAugmentationFieldsItem(new AugmentationFieldRequest().name("nsi"))
                .addAugmentationFieldsItem(new AugmentationFieldRequest().name("site"))
                .addAugmentationFieldsItem(new AugmentationFieldRequest().name("plmnId"));

        AugmentationRequest requestOnlyNsi = new AugmentationRequest()
                .addInputFieldsItem(new QueryField().name("nodeFDN").value("MeContext=PCC00010,ManagedElement=PCC00010"))
                .addInputFieldsItem(new QueryField().name("snssai").value("1-1"))
                .addAugmentationFieldsItem(new AugmentationFieldRequest().name("nsi"));

        AugmentationRequest requestOnlyNssi = new AugmentationRequest()
                .addInputFieldsItem(new QueryField().name("nodeFDN").value("MeContext=PCC00010,ManagedElement=PCC00010"))
                .addInputFieldsItem(new QueryField().name("snssai").value("1-1"))
                .addAugmentationFieldsItem(new AugmentationFieldRequest().name("nssi"));

        AugmentationRequest requestOnlySite = new AugmentationRequest()
                .addInputFieldsItem(new QueryField().name("nodeFDN").value("MeContext=PCC00010,ManagedElement=PCC00010"))
                .addInputFieldsItem(new QueryField().name("snssai").value("1-1"))
                .addAugmentationFieldsItem(new AugmentationFieldRequest().name("site"));

        AugmentationRequest requestOnlyPlmnId = new AugmentationRequest()
                .addInputFieldsItem(new QueryField().name("nodeFDN").value("MeContext=PCC00010,ManagedElement=PCC00010"))
                .addInputFieldsItem(new QueryField().name("snssai").value("1-1"))
                .addAugmentationFieldsItem(new AugmentationFieldRequest().name("plmnId"));

        AugmentationRequest requestBothNsiAndNssi = new AugmentationRequest()
                .addInputFieldsItem(new QueryField().name("nodeFDN").value("MeContext=PCC00010,ManagedElement=PCC00010"))
                .addInputFieldsItem(new QueryField().name("snssai").value("1-1"))
                .addAugmentationFieldsItem(new AugmentationFieldRequest().name("nssi"))
                .addAugmentationFieldsItem(new AugmentationFieldRequest().name("nsi"));

        AugmentationRequest requestBothNsiAndPlmnId = new AugmentationRequest()
                .addInputFieldsItem(new QueryField().name("nodeFDN").value("MeContext=PCC00010,ManagedElement=PCC00010"))
                .addInputFieldsItem(new QueryField().name("snssai").value("1-1"))
                .addAugmentationFieldsItem(new AugmentationFieldRequest().name("nsi"))
                .addAugmentationFieldsItem(new AugmentationFieldRequest().name("plmnId"));

        AugmentationRequest requestBothNsiAndSite = new AugmentationRequest()
                .addInputFieldsItem(new QueryField().name("nodeFDN").value("MeContext=PCC00010,ManagedElement=PCC00010"))
                .addInputFieldsItem(new QueryField().name("snssai").value("1-1"))
                .addAugmentationFieldsItem(new AugmentationFieldRequest().name("nsi"))
                .addAugmentationFieldsItem(new AugmentationFieldRequest().name("site"));

        AugmentationRequest requestBothNssiAndPlmnId = new AugmentationRequest()
                .addInputFieldsItem(new QueryField().name("nodeFDN").value("MeContext=PCC00010,ManagedElement=PCC00010"))
                .addInputFieldsItem(new QueryField().name("snssai").value("1-1"))
                .addAugmentationFieldsItem(new AugmentationFieldRequest().name("nssi"))
                .addAugmentationFieldsItem(new AugmentationFieldRequest().name("plmnId"));

        AugmentationRequest requestBothNssiAndSite = new AugmentationRequest()
                .addInputFieldsItem(new QueryField().name("nodeFDN").value("MeContext=PCC00010,ManagedElement=PCC00010"))
                .addInputFieldsItem(new QueryField().name("snssai").value("1-1"))
                .addAugmentationFieldsItem(new AugmentationFieldRequest().name("nssi"))
                .addAugmentationFieldsItem(new AugmentationFieldRequest().name("site"));

        AugmentationRequest requestBothSiteAndPlmnId = new AugmentationRequest()
                .addInputFieldsItem(new QueryField().name("nodeFDN").value("MeContext=PCC00010,ManagedElement=PCC00010"))
                .addInputFieldsItem(new QueryField().name("snssai").value("1-1"))
                .addAugmentationFieldsItem(new AugmentationFieldRequest().name("site"))
                .addAugmentationFieldsItem(new AugmentationFieldRequest().name("plmnId"));

        Augmentation fieldsOnlyTaList = new Augmentation()
                .fields(List.of(
                        List.of(new AugmentationField().name(TA).value("999"))
                ));

        Augmentation fieldsBothNssiAndNsiList5qi = new Augmentation().fields(List.of(
                List.of(new AugmentationField().name(QOS).value("1"),
                        new AugmentationField().name(NSI).value("macNsiDedicated20230728x1543_qosh96mac01_nsi"),
                        new AugmentationField().name(NSSI).value("macNsiDedicated20230728x1543_qosh96mac01_nssi"),
                        new AugmentationField().name(TA).value("20")),
                List.of(new AugmentationField().name(QOS).value("6"),
                        new AugmentationField().name(NSI).value("macNsiDedicated20230728x1543_qosh96mac01_nsi"),
                        new AugmentationField().name(NSSI).value("macNsiDedicated20230728x1543_qosh96mac01_nssi"),
                        new AugmentationField().name(TA).value("20")),
                List.of(new AugmentationField().name(QOS).value("1"),
                        new AugmentationField().name(NSI).value(""),
                        new AugmentationField().name(NSSI).value("nssiQosh96mac02"),
                        new AugmentationField().name(TA).value("20")),
                List.of(new AugmentationField().name(QOS).value("6"),
                        new AugmentationField().name(NSI).value(""),
                        new AugmentationField().name(NSSI).value("nssiQosh96mac02"),
                        new AugmentationField().name(TA).value("20"))
        ));

        Augmentation allAugmentationFieldsList = new Augmentation().fields(List.of(
                List.of(new AugmentationField().name("nssi").value("NetworkSliceSubnet:NSSI-A11"),
                        new AugmentationField().name("nsi").value("NetworkSlice:NSI-A"),
                        new AugmentationField().name("site").value("GeographicSite:DataCenter1"),
                        new AugmentationField().name("plmnId").value("100-101")),
                List.of(new AugmentationField().name("nssi").value("NetworkSliceSubnet:NSSI-A1"),
                        new AugmentationField().name("nsi").value("NetworkSlice:NSI-A"),
                        new AugmentationField().name("site").value("GeographicSite:DataCenter1"),
                        new AugmentationField().name("plmnId").value("100-101"))
        ));

        Augmentation allAugmentationFieldsListWithoutSnssai = new Augmentation().fields(List.of(
                List.of(new AugmentationField().name("nssi").value("NetworkSliceSubnet:NSSI-A11"),
                        new AugmentationField().name("nsi").value("NetworkSlice:NSI-A"),
                        new AugmentationField().name("site").value("GeographicSite:DataCenter1"),
                        new AugmentationField().name("plmnId").value("100-101")),
                List.of(new AugmentationField().name("nssi").value("NetworkSliceSubnet:NSSI-A1"),
                        new AugmentationField().name("nsi").value("NetworkSlice:NSI-A"),
                        new AugmentationField().name("site").value("GeographicSite:DataCenter1"),
                        new AugmentationField().name("plmnId").value("100-101")),
                List.of(new AugmentationField().name("nssi").value("NetworkSliceSubnet:NSSI-B11"),
                        new AugmentationField().name("nsi").value("NetworkSlice:NSI-B"),
                        new AugmentationField().name("site").value("GeographicSite:DataCenter1"),
                        new AugmentationField().name("plmnId").value("100-101")),
                List.of(new AugmentationField().name("nssi").value("NetworkSliceSubnet:NSSI-B1"),
                        new AugmentationField().name("nsi").value("NetworkSlice:NSI-B"),
                        new AugmentationField().name("site").value("GeographicSite:DataCenter1"),
                        new AugmentationField().name("plmnId").value("100-101"))
        ));

        Augmentation fieldsOnlyNsiList = new Augmentation().fields(List.of(
                List.of(new AugmentationField().name("nsi").value("NetworkSlice:NSI-A"))
        ));

        Augmentation fieldsOnlyNssiList = new Augmentation().fields(List.of(
                List.of(new AugmentationField().name("nssi").value("NetworkSliceSubnet:NSSI-A11")),
                List.of(new AugmentationField().name("nssi").value("NetworkSliceSubnet:NSSI-A1"))
        ));

        Augmentation fieldsOnlyPlmnIdList = new Augmentation().fields(List.of(
                List.of(new AugmentationField().name("plmnId").value("100-101"))
        ));

        Augmentation fieldsOnlySiteList = new Augmentation().fields(List.of(
                List.of(new AugmentationField().name("site").value("GeographicSite:DataCenter1"))
        ));

        Augmentation fieldsBothNsiAndNssiList = new Augmentation().fields(List.of(
                List.of(new AugmentationField().name("nssi").value("NetworkSliceSubnet:NSSI-A11"),
                        new AugmentationField().name("nsi").value("NetworkSlice:NSI-A")),
                List.of(new AugmentationField().name("nssi").value("NetworkSliceSubnet:NSSI-A1"),
                        new AugmentationField().name("nsi").value("NetworkSlice:NSI-A"))
        ));

        Augmentation fieldsBothNsiAndPlmnIdList = new Augmentation().fields(List.of(
                List.of(new AugmentationField().name("nsi").value("NetworkSlice:NSI-A"),
                        new AugmentationField().name("plmnId").value("100-101"))
        ));

        Augmentation fieldsBothNsiAndSiteList = new Augmentation().fields(List.of(
                List.of(new AugmentationField().name("nsi").value("NetworkSlice:NSI-A"),
                        new AugmentationField().name("site").value("GeographicSite:DataCenter1"))
        ));

        Augmentation fieldsBothNssiAndPlmnIdList = new Augmentation().fields(List.of(
                List.of(new AugmentationField().name("nssi").value("NetworkSliceSubnet:NSSI-A11"),
                        new AugmentationField().name("plmnId").value("100-101")),
                List.of(new AugmentationField().name("nssi").value("NetworkSliceSubnet:NSSI-A1"),
                        new AugmentationField().name("plmnId").value("100-101"))
        ));

        Augmentation fieldsBothNssiAndSiteList = new Augmentation().fields(List.of(
                List.of(new AugmentationField().name("nssi").value("NetworkSliceSubnet:NSSI-A11"),
                        new AugmentationField().name("site").value("GeographicSite:DataCenter1")),
                List.of(new AugmentationField().name("nssi").value("NetworkSliceSubnet:NSSI-A1"),
                        new AugmentationField().name("site").value("GeographicSite:DataCenter1"))
        ));

        Augmentation fieldsBothSiteAndPlmnIdList = new Augmentation().fields(List.of(
                List.of(new AugmentationField().name("site").value("GeographicSite:DataCenter1"),
                        new AugmentationField().name("plmnId").value("100-101"))
        ));

        doReturn(new Augmentation()).when(augmentationRequestHandler).getAugmentation(any(AugmentationRequest.class));

        doReturn(fieldsOnlyTaList).when(augmentationRequestHandler).getAugmentation(eq(requestRanTa));
        doReturn(fieldsBothNssiAndNsiList5qi).when(augmentationRequestHandler).getAugmentation(eq(requestRanNoInput5Qi));

        doReturn(allAugmentationFieldsList).when(augmentationRequestHandler).getAugmentation(eq(requestAllAugmentationFields));
        doReturn(allAugmentationFieldsList).when(augmentationRequestHandler).getAugmentation(eq(requestAllAugmentationFieldsWithQueryType));
        doReturn(allAugmentationFieldsListWithoutSnssai).when(augmentationRequestHandler)
                .getAugmentation(eq(requestAllAugmentationFieldsWithoutSnssai));
        doReturn(fieldsOnlyNsiList).when(augmentationRequestHandler).getAugmentation(eq(requestOnlyNsi));
        doReturn(fieldsOnlyNssiList).when(augmentationRequestHandler).getAugmentation(eq(requestOnlyNssi));
        doReturn(fieldsOnlySiteList).when(augmentationRequestHandler).getAugmentation(eq(requestOnlySite));
        doReturn(fieldsOnlyPlmnIdList).when(augmentationRequestHandler).getAugmentation(eq(requestOnlyPlmnId));
        doReturn(fieldsBothNsiAndNssiList).when(augmentationRequestHandler).getAugmentation(eq(requestBothNsiAndNssi));
        doReturn(fieldsBothNsiAndPlmnIdList).when(augmentationRequestHandler).getAugmentation(eq(requestBothNsiAndPlmnId));
        doReturn(fieldsBothNsiAndSiteList).when(augmentationRequestHandler).getAugmentation(eq(requestBothNsiAndSite));
        doReturn(fieldsBothNssiAndPlmnIdList).when(augmentationRequestHandler).getAugmentation(eq(requestBothNssiAndPlmnId));
        doReturn(fieldsBothNssiAndSiteList).when(augmentationRequestHandler).getAugmentation(eq(requestBothNssiAndSite));
        doReturn(fieldsBothSiteAndPlmnIdList).when(augmentationRequestHandler).getAugmentation(eq(requestBothSiteAndPlmnId));

        RestAssuredMockMvc.standaloneSetup(augmentationApiController);
    }
}
