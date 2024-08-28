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

package com.ericsson.oss.air.cardq.controller.augmentation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;

import static com.ericsson.oss.air.cardq.utils.Constants.TA;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.data.util.Pair;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.ericsson.oss.air.cardq.CoreApplication;
import com.ericsson.oss.air.cardq.StubRunnerTestSetup;
import com.ericsson.oss.air.cardq.api.model.Augmentation;
import com.ericsson.oss.air.cardq.api.model.AugmentationField;
import com.ericsson.oss.air.cardq.api.model.AugmentationFieldRequest;
import com.ericsson.oss.air.cardq.api.model.AugmentationRequest;
import com.ericsson.oss.air.cardq.api.model.ProblemDetails;
import com.ericsson.oss.air.cardq.api.model.QueryField;
import com.ericsson.oss.air.cardq.cache.Graph;
import com.ericsson.oss.air.cardq.error.MandatoryParameterException;
import com.ericsson.oss.air.cardq.handler.CtsAugmentationRequestHandler;
import com.ericsson.oss.air.cardq.services.CacheService;
import com.ericsson.oss.air.cardq.services.CoreGraphService;
import com.ericsson.oss.air.cardq.services.RanGraphService;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tag;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = { CoreApplication.class, AugmentationApiController.class })
public class AugmentationApiControllerTest extends StubRunnerTestSetup {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private MeterRegistry meterRegistry;

    @SpyBean
    private AugmentationApiController augmentationApiController;

    private MockMvc mvc;

    @SpyBean
    private CacheService cacheService;

    @SpyBean
    private CoreGraphService coreGraphService;

    @SpyBean
    private RanGraphService ranGraphService;

    @SpyBean
    private CtsAugmentationRequestHandler augmentationRequestHandler;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    public void setUp() {
        mvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        cacheService.initialiseCache();
    }

    /**
     * This method provides test data for the parameterized test.
     *
     * @return Stream of Arguments representing different scenarios of AugmentationRequest.
     */
    static Stream<Arguments> validJsonData() {

        List<List<AugmentationField>> augmentationFieldList1 = List.of(
                List.of(new AugmentationField().name("nsi").value("NSI-B"),
                        new AugmentationField().name("plmnId").value("entity-1")),
                List.of(new AugmentationField().name("nsi").value("NSI-B"),
                        new AugmentationField().name("plmnId").value("entity-2")));
        Augmentation augmentationForSnssai = new Augmentation().fields(augmentationFieldList1);

        List<List<AugmentationField>> augmentationFieldList2 = List.of(
                List.of(new AugmentationField().name("nsi").value("NSI-A"),
                        new AugmentationField().name("plmnId").value("entity-1")),
                List.of(new AugmentationField().name("nsi").value("NSI-B"),
                        new AugmentationField().name("plmnId").value("entity-2")),
                List.of(new AugmentationField().name("nsi").value("NSI-B"),
                        new AugmentationField().name("plmnId").value("entity-3")));
        Augmentation augmentationForWithoutSnssai = new Augmentation().fields(augmentationFieldList2);

        return Stream.of(
                //  These arguments will represent the scenario of having valid JSON request with Snssai
                Arguments.of(
                        augmentationForSnssai,
                        getDummyRequestData(),
                        2
                ),
                //  These arguments will represent the scenario of having valid JSON request without Snssai
                Arguments.of(
                        augmentationForWithoutSnssai,
                        getDummyRequestDataWithNoSnssai(),
                        3
                )
        );
    }

    /**
     * This Parameterized test for all valid json requests
     *
     * @param augmentation the augmentation
     * @param request      Augmentation request
     * @param fieldSize    Expected size of the fields.
     * @throws Exception exception thrown from ObjectMapper
     */
    @ParameterizedTest
    @MethodSource("validJsonData")
    public void validJsonRequest(final Augmentation augmentation, final AugmentationRequest request,
                                 final int fieldSize) throws Exception {

        doReturn(augmentation).when(augmentationRequestHandler).getAugmentation(request);

        mvc.perform(MockMvcRequestBuilders.post(URI.create("/v1/augmentation-info/augmentation"))
                            .accept(MediaType.APPLICATION_JSON)
                            .characterEncoding(StandardCharsets.UTF_8)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(new ObjectMapper().writeValueAsString(request)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.content().json(objectMapper.writeValueAsString(augmentation)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.fields").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.fields").isNotEmpty())
                .andExpect(MockMvcResultMatchers.jsonPath("$.fields").isArray())
                .andExpect(MockMvcResultMatchers.jsonPath("$.fields[*]", Matchers.hasSize(fieldSize)));

        verify(augmentationRequestHandler).getAugmentation(eq(request));
    }

    /**
     * Test for handling bad JSON requests.
     *
     * @throws Exception If an error occurs during the test.
     */
    @Test
    public void badJsonRequest() throws Exception {
        mvc.perform(MockMvcRequestBuilders.post(URI.create("/v1/augmentation-info/augmentation"))
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString("badJson")))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    /**
     * Test for handling method-not-allowed requests.
     *
     * @throws Exception If an error occurs during the test.
     */
    @Test
    public void methodNotAllowedRequest() throws Exception {
        mvc.perform(MockMvcRequestBuilders.get(URI.create("/v1/augmentation-info/augmentation"))
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(getDummyRequestData())))
                .andExpect(MockMvcResultMatchers.status().isMethodNotAllowed());
    }

    /**
     * Test for handling unsupported media type requests.
     *
     * @throws Exception If an error occurs during the test.
     */
    @Test
    public void unsupportedMediaTypeRequest() throws Exception {
        mvc.perform(MockMvcRequestBuilders.post(URI.create("/v1/augmentation-info/augmentation"))
                        .accept(MediaType.TEXT_PLAIN)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.TEXT_PLAIN)
                        .content(new ObjectMapper().writeValueAsString(getDummyRequestData())))
                .andExpect(MockMvcResultMatchers.status().isUnsupportedMediaType());
    }

    /**
     * Test case for handling a request with duplicate 'nodeFDN' input fields, expecting a 'Bad Request' response.
     *
     * @throws Exception If an error occurs during the test.
     */
    @Test
    public void duplicateNodeFdnInputFieldsInRequest() throws Exception {
        AugmentationRequest request = new AugmentationRequest()
                .addAugmentationFieldsItem(new AugmentationFieldRequest())
                .addInputFieldsItem(new QueryField().name("nodeFDN").value("pcc-amf1"))
                .addInputFieldsItem(new QueryField().name("nodeFDN").value("pcc-amf2"));
        mvc.perform(MockMvcRequestBuilders.post(URI.create("/v1/augmentation-info/augmentation"))
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(request)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    /**
     * Test case for handling a request with duplicate 'snssai' input fields, expecting a 'Bad Request' response.
     *
     * @throws Exception If an error occurs during the test.
     */
    @Test
    public void duplicateSnssaiInputFieldsInRequest() throws Exception {
        AugmentationRequest request = new AugmentationRequest()
                .addAugmentationFieldsItem(new AugmentationFieldRequest())
                .addInputFieldsItem(new QueryField().name("snssai").value("11-1"))
                .addInputFieldsItem(new QueryField().name("snssai").value("12-1"));
        mvc.perform(MockMvcRequestBuilders.post(URI.create("/v1/augmentation-info/augmentation"))
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(request)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    /**
     * Test case for handling a request with both duplicate 'nodeFDN' and 'snssai' input fields, expecting a 'Bad Request' response.
     *
     * @throws Exception If an error occurs during the test.
     */
    @Test
    public void duplicateNodeFdnAndSnssaiInputFieldsInRequest() throws Exception {
        AugmentationRequest request = new AugmentationRequest()
                .addAugmentationFieldsItem(new AugmentationFieldRequest())
                .addInputFieldsItem(new QueryField().name("snssai").value("11-1"))
                .addInputFieldsItem(new QueryField().name("snssai").value("12-1"))
                .addInputFieldsItem(new QueryField().name("nodeFDN").value("pcc-amf1"))
                .addInputFieldsItem(new QueryField().name("nodeFDN").value("pcc-amf2"));
        mvc.perform(MockMvcRequestBuilders.post(URI.create("/v1/augmentation-info/augmentation"))
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(request)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    /**
     * Test case for handling a request when data is not found in the cache, expecting an empty response.
     *
     * @throws Exception If an error occurs during the test.
     */
    @Test
    public void ifDataNotFoundInCtsReturnEmptyResponse() throws Exception {
        mvc.perform(MockMvcRequestBuilders.post(URI.create("/v1/augmentation-info/augmentation"))
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(getDataNotFoundRequest())))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.fields").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.fields").isArray())
                .andExpect(MockMvcResultMatchers.jsonPath("$.fields[*]").isEmpty());
    }

    /**
     * Test case for handling a request with missing mandatory parameter 'nodeFDN', expecting a 'Bad Request' response with details.
     *
     * @throws Exception If an error occurs during the test.
     */
    @Test
    public void missingParameterJsonRequest() throws Exception {
        final String message = "Missing Mandatory Parameter in inputsFields with format 'name': <value>. The missing parameter(s): [nodeFDN]";
        ProblemDetails problemDetails = new ProblemDetails()
                .title("Error occurred " + MandatoryParameterException.class.getSimpleName())
                .status(HttpStatus.BAD_REQUEST.value())
                .detail(message);
        mvc.perform(MockMvcRequestBuilders.post(URI.create("/v1/augmentation-info/augmentation"))
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(getMissingNodeFDNDummyRequestData())))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json(new ObjectMapper().writeValueAsString(problemDetails)));
    }

    /**
     * Test case for tracking the histogram bucket for an uncached augmentation response.
     *
     * @throws Exception If an error occurs during the test.
     */
    @Test
    void testAugmentationUncachedResponseHistogramBucket() throws Exception {
        doReturn(false).when(augmentationRequestHandler).getIsCached(any(AugmentationRequest.class));
        doReturn(new Augmentation()).when(augmentationRequestHandler).getAugmentation(any(AugmentationRequest.class));
        double firstBucketValue =
                meterRegistry.get("cardq.augmentation.response").tag("response_type", "uncached").timer().takeSnapshot().histogramCounts()[1].count();
        mvc.perform(MockMvcRequestBuilders.post(URI.create("/v1/augmentation-info/augmentation"))
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(getDummyRequestData())))
                .andExpect(MockMvcResultMatchers.status().isOk());

        assertThat(meterRegistry
                .get("cardq.augmentation.response").tag("response_type", "uncached")
                .timer()
                .takeSnapshot()
                .histogramCounts()[1].count()).isEqualTo(firstBucketValue + 1);
    }

    /**
     * Test case for tracking the histogram bucket for a cached augmentation response.
     *
     * @throws Exception If an error occurs during the test.
     */
    @Test
    void testAugmentationCachedResponseHistogramBucket() throws Exception {
        doReturn(Pair.of(new Graph(), LocalDateTime.now())).when(cacheService).get(anyString());

        double firstBucketValue =
                meterRegistry.get("cardq.augmentation.response").tag("response_type", "cached").timer().takeSnapshot().histogramCounts()[1].count();

        mvc.perform(MockMvcRequestBuilders.post(URI.create("/v1/augmentation-info/augmentation"))
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(getDummyRequestData())))
                .andExpect(MockMvcResultMatchers.status().isOk());

        assertThat(meterRegistry
                           .get("cardq.augmentation.response").tag("response_type", "cached")
                           .timer()
                           .takeSnapshot()
                           .histogramCounts()[1].count()).isEqualTo(firstBucketValue + 1);
    }

    /**
     * Test case for handling a request with missing augmentation query fields in the request body, expecting a 'Bad Request' response.
     *
     * @throws Exception If an error occurs during the test.
     */
    @Test
    public void missingAugmentationQueryFieldsInRequestBody() throws Exception {
        mvc.perform(MockMvcRequestBuilders.post(URI.create("/v1/augmentation-info/augmentation"))
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(new AugmentationRequest().addInputFieldsItem(new QueryField()))))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    /**
     * Test case for handling a request with missing input query fields in the request body, expecting a 'Bad Request' response.
     *
     * @throws Exception If an error occurs during the test.
     */
    @Test
    public void missingInputQueryFieldsInRequestBody() throws Exception {
        mvc.perform(MockMvcRequestBuilders.post(URI.create("/v1/augmentation-info/augmentation"))
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(new AugmentationRequest()
                                .addAugmentationFieldsItem(new AugmentationFieldRequest()))))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    /**
     * Test case for handling a request with missing both query fields in the request body, expecting a 'Bad Request' response.
     *
     * @throws Exception If an error occurs during the test.
     */
    @Test
    public void missingBothQueryFieldsInRequestBody() throws Exception {
        mvc.perform(MockMvcRequestBuilders.post(URI.create("/v1/augmentation-info/augmentation"))
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(new AugmentationRequest())))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    /**
     * Test case for handling a request with invalid augmentation fields in the request body, expecting a 'Bad Request' response with details.
     *
     * @throws Exception If an error occurs during the test.
     */
    @Test
    public void invalidAugmentationFieldsInRequestBody() throws Exception {

        AugmentationRequest augmentationRequest = new AugmentationRequest()
                .addInputFieldsItem(new QueryField().name("nodeFDN").value("pcc-amf2"))
                .addAugmentationFieldsItem(new AugmentationFieldRequest().name("plmnId"))
                .addAugmentationFieldsItem(new AugmentationFieldRequest().name("unknown"));

        final String message = "Malformed Request, Reason: Invalid augmentationFields value, supported values are: [nsi, nssi, site, plmnId], but found: "
                + "[unknown]";
        ProblemDetails problemDetails = new ProblemDetails()
                .title("Malformed Request, IllegalArgumentException")
                .status(HttpStatus.BAD_REQUEST.value())
                .detail(message);
        mvc.perform(MockMvcRequestBuilders.post(URI.create("/v1/augmentation-info/augmentation"))
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(augmentationRequest)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json(new ObjectMapper().writeValueAsString(problemDetails)));
    }

    /**
     * Test case for handling a request with an invalid augmentation query type field in the request body, expecting a 'Bad Request' response with details.
     *
     * @throws Exception If an error occurs during the test.
     */
    @Test
    public void invalidAugmentationQueryTypeFieldInRequestBody() throws Exception {

        AugmentationRequest augmentationRequest = new AugmentationRequest()
                .addInputFieldsItem(new QueryField().name("nodeFDN").value("pcc-amf1"))
                .addInputFieldsItem(new QueryField().name("snssai").value("12-1"))
                .addAugmentationFieldsItem(new AugmentationFieldRequest().name("nsi"))
                .addAugmentationFieldsItem(new AugmentationFieldRequest().name("plmnId"))
                .queryType("invalidType");
        final String message = "Malformed Request, Reason: Invalid query type, invalidType";
        ProblemDetails problemDetails = new ProblemDetails()
                .title("Malformed Request, IllegalArgumentException")
                .status(HttpStatus.BAD_REQUEST.value())
                .detail(message);
        mvc.perform(MockMvcRequestBuilders.post(URI.create("/v1/augmentation-info/augmentation"))
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(augmentationRequest)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json(new ObjectMapper().writeValueAsString(problemDetails)));
    }

    public static Stream<Arguments> invalidSnssai() {
        return Stream.of(Arguments.of("123"),
                Arguments.of("83-5a"),
                Arguments.of("-5")
        );
    }

    @ParameterizedTest
    @MethodSource("invalidSnssai")
    public void invalidSnssaiValuesInRequestBody(String snssai) throws Exception {
        List<QueryField> queryFields = List.of(new QueryField("nodeFDN", "pcc-amf1"), new QueryField("snssai", snssai));
        List<AugmentationFieldRequest> augmentationFields = List.of(new AugmentationFieldRequest("nssi"));
        AugmentationRequest augmentationRequest = new AugmentationRequest(queryFields, augmentationFields);
        final String message = "Malformed Request, Reason: Invalid inputFields value: snssai format should be '<SNSSAI_SST>-<SNSSAI_SD>'. ie. 70-2.";
        ProblemDetails problemDetails = new ProblemDetails(HttpStatus.BAD_REQUEST.value(), message).title("Malformed Request, IllegalArgumentException");
        mvc.perform(MockMvcRequestBuilders.post(URI.create("/v1/augmentation-info/augmentation"))
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(augmentationRequest)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json(new ObjectMapper().writeValueAsString(problemDetails)));
    }

    /**
     * Test case for retrieving query types, expecting a successful response with valid query types.
     *
     * @throws Exception If an error occurs during the test.
     */
    @Test
    public void getQueryTypes() throws Exception {
        mvc.perform(MockMvcRequestBuilders.get(URI.create("/v1/augmentation-info/augmentation/query/types")))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$").isNotEmpty())
                .andExpect(MockMvcResultMatchers.jsonPath("$").isArray())
                .andExpect(MockMvcResultMatchers.jsonPath("$[*]", Matchers.hasSize(2)))
                .andExpect(MockMvcResultMatchers.jsonPath("$[*].queryType", Matchers.containsInAnyOrder("core", "ran")));
    }

    /**
     * Test case for retrieving query types with a trailing slash, expecting a successful response.
     *
     * @throws Exception If an error occurs during the test.
     */
    @Test
    public void getQueryTypesWithTrailingSlash() throws Exception {
        mvc.perform(MockMvcRequestBuilders.get(URI.create("/v1/augmentation-info/augmentation/query/types/")))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    /**
     * Test case for augmentation info with a trailing slash, expecting a successful response with non-empty fields.
     *
     * @throws Exception If an error occurs during the test.
     */
    @Test
    public void augmentationInfoWithTrailingSlash() throws Exception {
        mvc.perform(MockMvcRequestBuilders.post(URI.create("/v1/augmentation-info/augmentation/"))
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(getDummyRequestData())))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.fields").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.fields").isNotEmpty());
    }

    /**
     * Test case for adding partial URI tags after successful requests, expecting specific URI patterns in the meter registry.
     */
    @Test
    public void shouldAddPartialUriTagsAfterSuccessfulRequests() {
        List<QueryField> inputFieldsCore = List.of(new QueryField("nodeFDN", "MeContext=PCC00011,ManagedElement=PCC00011"), new QueryField("snssai"
                , "83-5"));
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
                .containsExactlyInAnyOrder(
                        "/oss-core-ws/rest/ctw/wirelessnetfunction",
                        "/oss-core-ws/rest/ctw/nrcell",
                        "/oss-core-ws/rest/ctw/networkslice/226",
                        "/oss-core-ws/rest/ctw/networkslicesubnet/234",
                        "/oss-core-ws/rest/ctw/networkslicesubnet/229");
    }

    /**
     * Utility method to provide dummy augmentation request data for testing purposes.
     *
     * @return AugmentationRequest object with dummy data.
     */
    private static AugmentationRequest getDummyRequestData() {
        return new AugmentationRequest()
                .inputFields(new ArrayList<>())
                .augmentationFields(new ArrayList<>())
                .addInputFieldsItem(new QueryField().name("nodeFDN").value("MeContext=PCC00011,ManagedElement=PCC00011"))
                .addInputFieldsItem(new QueryField().name("snssai").value("83-5"))
                .addAugmentationFieldsItem(new AugmentationFieldRequest().name("nsi"))
                .addAugmentationFieldsItem(new AugmentationFieldRequest().name("plmnId"));
    }

    /**
     * Utility method to provide dummy augmentation request data with no snssai for testing purposes.
     *
     * @return AugmentationRequest object with dummy data and no snssai.
     */
    public static AugmentationRequest getDummyRequestDataWithNoSnssai() {
        return new AugmentationRequest()
                .inputFields(new ArrayList<>())
                .augmentationFields(new ArrayList<>())
                .addInputFieldsItem(new QueryField().name("nodeFDN").value("MeContext=PCC00011,ManagedElement=PCC00011"))
                .addAugmentationFieldsItem(new AugmentationFieldRequest().name("nsi"))
                .addAugmentationFieldsItem(new AugmentationFieldRequest().name("plmnId"));
    }

    /**
     * Utility method to provide dummy augmentation request data with missing 'nodeFDN' for testing purposes.
     *
     * @return AugmentationRequest object with dummy data and missing 'nodeFDN'.
     */
    private static AugmentationRequest getMissingNodeFDNDummyRequestData() {
        return new AugmentationRequest()
                .inputFields(new ArrayList<>())
                .augmentationFields(new ArrayList<>())
                .addInputFieldsItem(new QueryField().name("snssai").value("2-1888"))
                .addAugmentationFieldsItem(new AugmentationFieldRequest().name("nsi"))
                .addAugmentationFieldsItem(new AugmentationFieldRequest().name("plmnId"));
    }

    /**
     * Utility method to provide an AugmentationRequest object with data intended to use for a scenario where the requested data is not found.
     *
     * @return AugmentationRequest object for simulating data not found scenario.
     */
    private static AugmentationRequest getDataNotFoundRequest() {
        return new AugmentationRequest()
                .inputFields(new ArrayList<>())
                .augmentationFields(new ArrayList<>())
                .addInputFieldsItem(new QueryField().name("nodeFDN").value("Notfound"))
                .addInputFieldsItem(new QueryField().name("snssai").value("2-1888"))
                .addAugmentationFieldsItem(new AugmentationFieldRequest().name("nsi"))
                .addAugmentationFieldsItem(new AugmentationFieldRequest().name("plmnId"));
    }
}
