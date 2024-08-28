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

package com.ericsson.oss.air.cardq.client.observability;

import com.ericsson.oss.air.cardq.CoreApplication;
import com.ericsson.oss.air.cardq.StubRunnerTestSetup;
import com.ericsson.oss.air.cardq.api.model.Augmentation;
import com.ericsson.oss.air.cardq.api.model.AugmentationFieldRequest;
import com.ericsson.oss.air.cardq.api.model.AugmentationRequest;
import com.ericsson.oss.air.cardq.api.model.QueryField;
import com.ericsson.oss.air.cardq.controller.augmentation.AugmentationApiController;
import com.ericsson.oss.air.security.utils.exceptions.InternalRuntimeException;
import io.micrometer.common.KeyValue;
import io.micrometer.common.KeyValues;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpRequest;
import org.springframework.http.client.observation.ClientRequestObservationContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.net.URI;
import java.util.List;
import java.util.function.Predicate;

import static com.ericsson.oss.air.cardq.utils.Constants.TA;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = {CoreApplication.class, AugmentationApiController.class})
@TestPropertySource(properties = "observability.client.add-query=true")
public class CustomClientRequestObservationConventionTest extends StubRunnerTestSetup {

    @SpyBean
    private AugmentationApiController augmentationApiController;

    @Autowired
    private MeterRegistry meterRegistry;

    @SpyBean
    private CustomClientRequestObservationConvention customClientRequestObservationConvention;

    @Test
    public void shouldAddLowCardinalityValue() {
        ClientHttpRequest request = mock(ClientHttpRequest.class);
        when(request.getURI()).thenReturn(URI.create("https://cardq.tls.hahn1242.rnd.gic.ericsson.se/test"));
        when(request.getMethod()).thenReturn(HttpMethod.GET);
        ClientRequestObservationContext clientRequestObservationContext = new ClientRequestObservationContext(request);
        KeyValues lowCardinalityKeyValues = customClientRequestObservationConvention.getLowCardinalityKeyValues(clientRequestObservationContext);
        assertThat(lowCardinalityKeyValues).contains(KeyValue.of("uri", "/test"));
    }

    @Test
    public void shouldAddLowCardinalityValueWithQuery() {
        ClientHttpRequest request = mock(ClientHttpRequest.class);
        when(request.getURI()).thenReturn(URI.create("https://cardq.tls.hahn1242.rnd.gic.ericsson.se/test?id=2"));
        when(request.getMethod()).thenReturn(HttpMethod.GET);
        ClientRequestObservationContext clientRequestObservationContext = new ClientRequestObservationContext(request);
        KeyValues lowCardinalityKeyValues = customClientRequestObservationConvention.getLowCardinalityKeyValues(clientRequestObservationContext);
        assertThat(lowCardinalityKeyValues).contains(KeyValue.of("uri", "/test?id=2"));
    }

    @Test
    public void shouldThrowWhenInvalidUrl() {
        ClientHttpRequest request = mock(ClientHttpRequest.class);
        when(request.getURI()).thenReturn(URI.create("invalid.url"));
        when(request.getMethod()).thenReturn(HttpMethod.GET);
        ClientRequestObservationContext clientRequestObservationContext = new ClientRequestObservationContext(request);
        assertThatThrownBy(() -> customClientRequestObservationConvention.getLowCardinalityKeyValues(clientRequestObservationContext))
                .isInstanceOf(InternalRuntimeException.class)
                .hasMessageContaining("Invalid URL :: invalid.url");
    }

    @Test
    public void shouldAddCompleteUriTagsAfterSuccessfulRequests() {
        List<QueryField> inputFieldsCore = List.of(new QueryField("nodeFDN", "MeContext=PCC00011,ManagedElement=PCC00011"), new QueryField("snssai", "83-5"));
        List<AugmentationFieldRequest> augmentationFieldRequests = List.of(new AugmentationFieldRequest("nsi"), new AugmentationFieldRequest("plmnId"));
        AugmentationRequest augmentationRequestCore = new AugmentationRequest(inputFieldsCore, augmentationFieldRequests);
        ResponseEntity<Augmentation> responseEntityCore = augmentationApiController.recordAugmentationRequest(augmentationRequestCore);
        assertEquals(HttpStatus.OK, responseEntityCore.getStatusCode());

        QueryField localDn = new QueryField("localDn", "SubNetwork=Europe,SubNetwork=Ireland,MeContext=gNodeB1,ManagedElement=gNodeB1");
        QueryField measObjLdn = new QueryField("measObjLdn", "ManagedElement=gNodeB1,GNBDUFunction=1,NRCellDU=209");
        List<QueryField> inputFieldsRan = List.of(localDn, measObjLdn);
        List<AugmentationFieldRequest> augmentationFieldRequestsRan = List.of(new AugmentationFieldRequest(TA));
        AugmentationRequest augmentationRequestRan = new AugmentationRequest(inputFieldsRan, augmentationFieldRequestsRan).queryType("ran");
        ResponseEntity<Augmentation> responseEntityRan = augmentationApiController.recordAugmentationRequest(augmentationRequestRan);
        assertEquals(HttpStatus.OK, responseEntityRan.getStatusCode());

        assertThat(meterRegistry.get("http.client.requests").meters())
                .flatExtracting(meter -> meter.getId().getTags())
                .filteredOn(tag -> tag.getKey().equals("uri"))
                .extracting(Tag::getValue)
                .hasSize(5)
                .matches(partialMatch("ctw/nrcell?ExternalRef::nrCellDuFDN=SubNetwork%3DEurope,SubNetwork%3DIreland,MeContext%3DgNodeB1"))
                .matches(partialMatch("ManagedElement%3DgNodeB1,GNBDUFunction%3D1,NRCellDU%3D209&fs=dynAttrs&fs.gnbcucp=dynAttrs"))
                .matches(partialMatch("fs.gnbcucp=dynAttrs&fs.gnbdu=attrs&fs.gnbdu.fiveQISets=attrs&fs.gnbdu.fiveQISets.qosProfile=attrs"))
                .matches(partialMatch("fs.gnbdu.fiveQISets.fiveQIFlows=attrs&fs.gnbdu.fiveQISets.resourcePartitions=key"))
                .matches(partialMatch("ctw/networkslice/226?fs.requirementServiceProfiles=&fs.requirementServiceProfiles.plmnInfoList="))
                .matches(partialMatch("ctw/networkslicesubnet/229?fs.requirementSliceProfiles=&fs.requirementSliceProfiles.plmnInfoList=" +
                        "&fs.networkSlice=&fs.supportingNetworkSliceSubnets="))
                .matches(partialMatch("ctw/networkslicesubnet/234?fs.requirementSliceProfiles=&fs.requirementSliceProfiles.plmnInfoList=" +
                        "&fs.networkSlice=&fs.supportingNetworkSliceSubnets="));
    }

    private static Predicate<List<? extends String>> partialMatch(String tagValue) {
        return tags -> tags.stream().anyMatch(tag -> tag.contains(tagValue));
    }
}
