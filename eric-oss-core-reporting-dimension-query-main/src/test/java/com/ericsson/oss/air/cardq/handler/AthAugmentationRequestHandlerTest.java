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

package com.ericsson.oss.air.cardq.handler;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.ericsson.oss.air.cardq.api.model.Augmentation;
import com.ericsson.oss.air.cardq.api.model.AugmentationField;
import com.ericsson.oss.air.cardq.api.model.AugmentationFieldRequest;
import com.ericsson.oss.air.cardq.api.model.AugmentationRequest;
import com.ericsson.oss.air.cardq.api.model.QueryField;
import com.ericsson.oss.air.cardq.repository.TopologyAugmentationDao;
import com.ericsson.oss.air.cardq.utils.GraphServiceType;

@ExtendWith(MockitoExtension.class)
class AthAugmentationRequestHandlerTest {

    @Mock
    TopologyAugmentationDao topologyAugmentationDao;

    @InjectMocks
    AthAugmentationRequestHandler augmentationRequestHandler;

    @Test
    void test_getAugmentation() {
        final AugmentationRequest request = new AugmentationRequest(new ArrayList<>(), new ArrayList<>())
                .addInputFieldsItem(new QueryField("inputField1", "value1"))
                .addInputFieldsItem(new QueryField("inputField2", "value2"))
                .addAugmentationFieldsItem(new AugmentationFieldRequest("nsi"))
                .queryType("ran");

        final AugmentationField augmentationField = new AugmentationField().name("nsi").value("nsi1");
        final List<List<AugmentationField>> augmentationFieldsList = List.of(List.of(augmentationField));
        when(this.topologyAugmentationDao.findByQueryService(any(GraphServiceType.class), anyMap())).thenReturn(augmentationFieldsList);

        final Augmentation expected = new Augmentation().fields(augmentationFieldsList);
        final GraphServiceType graphServiceType = GraphServiceType.getByString(request.getQueryType());
        final Map<String, String> parameters = Map.of("inputField1", "value1", "inputField2", "value2");
        assertEquals(expected, this.augmentationRequestHandler.getAugmentation(request));
        verify(this.topologyAugmentationDao).findByQueryService(eq(graphServiceType), eq(parameters));
    }

    @Test
    void test_getAugmentation_augmentationFieldIsEmptyList() {
        final AugmentationRequest request = new AugmentationRequest(new ArrayList<>(), new ArrayList<>())
                .addInputFieldsItem(new QueryField("inputField1", "value1"))
                .addInputFieldsItem(new QueryField("inputField2", "value2"))
                .addAugmentationFieldsItem(new AugmentationFieldRequest("nsi"))
                .queryType("ran");

        final AugmentationField augmentationField = new AugmentationField().name("nsi").value("nsi1");
        final List<List<AugmentationField>> augmentationFieldsList = List.of(List.of(augmentationField));
        when(this.topologyAugmentationDao.findByQueryService(any(GraphServiceType.class), anyMap())).thenReturn(new ArrayList<>());

        final Augmentation augmentations = this.augmentationRequestHandler.getAugmentation(request);
        assertEquals(new ArrayList<>(), augmentations.getFields());
    }

    @Test
    void test_getAugmentation_onlyIncludesRequiredAugmentationFields() {
        final AugmentationRequest request = new AugmentationRequest(new ArrayList<>(), new ArrayList<>())
                .addInputFieldsItem(new QueryField("inputField1", "value1"))
                .addInputFieldsItem(new QueryField("inputField2", "value2"))
                .addAugmentationFieldsItem(new AugmentationFieldRequest("nsi"))
                .queryType("ran");

        final List<List<AugmentationField>> augmentationFieldsList = List.of(
                List.of(
                        new AugmentationField().name("nsi").value("nsi1"),
                        new AugmentationField().name("otherField1").value("value1"),
                        new AugmentationField().name("otherField2").value("value2")
                ),

                List.of(
                        new AugmentationField().name("nsi").value("nsi2"),
                        new AugmentationField().name("otherField1").value("value1"),
                        new AugmentationField().name("otherField2").value("value2")
                )
        );
        when(this.topologyAugmentationDao.findByQueryService(any(GraphServiceType.class), anyMap())).thenReturn(augmentationFieldsList);

        final Augmentation expected = new Augmentation().fields(List.of(
                List.of(new AugmentationField().name("nsi").value("nsi1")),
                List.of(new AugmentationField().name("nsi").value("nsi2"))
        ));
        assertEquals(expected, this.augmentationRequestHandler.getAugmentation(request));
    }

    @Test
    void test_getAugmentation_duplicatedAugmentationFieldsWillBeRemoved() {
        final AugmentationRequest request = new AugmentationRequest(new ArrayList<>(), new ArrayList<>())
                .addInputFieldsItem(new QueryField("inputField1", "value1"))
                .addInputFieldsItem(new QueryField("inputField2", "value2"))
                .addAugmentationFieldsItem(new AugmentationFieldRequest("nsi"))
                .addAugmentationFieldsItem(new AugmentationFieldRequest("site"))
                .queryType("ran");

        final List<List<AugmentationField>> augmentationFieldsList = List.of(
                List.of(
                        new AugmentationField().name("nsi").value("nsi1"),
                        new AugmentationField().name("site").value("site"),
                        new AugmentationField().name("otherField1").value("value1")
                ),
                List.of(
                        new AugmentationField().name("nsi").value("nsi1"),
                        new AugmentationField().name("site").value("site"),
                        new AugmentationField().name("otherField2").value("value2")
                ),
                List.of(
                        new AugmentationField().name("nsi").value("nsi1"),
                        new AugmentationField().name("site").value("site2"),
                        new AugmentationField().name("otherField3").value("value3")
                ),
                List.of(
                        new AugmentationField().name("site").value("site2"),
                        new AugmentationField().name("nsi").value("nsi1"),
                        new AugmentationField().name("otherField3").value("value3")
                ),
                List.of(
                        new AugmentationField().name("nsi").value("nsi1"),
                        new AugmentationField().name("site").value("site2"),
                        new AugmentationField().name("otherField4").value("value4")
                )
        );
        when(this.topologyAugmentationDao.findByQueryService(any(GraphServiceType.class), anyMap())).thenReturn(augmentationFieldsList);

        final Augmentation expected = new Augmentation().fields(List.of(
                List.of(new AugmentationField().name("nsi").value("nsi1"),
                        new AugmentationField().name("site").value("site")),
                List.of(new AugmentationField().name("nsi").value("nsi1"),
                        new AugmentationField().name("site").value("site2"))));
        assertEquals(expected, this.augmentationRequestHandler.getAugmentation(request));
    }
}