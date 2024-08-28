/*******************************************************************************
 * COPYRIGHT Ericsson 2023
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

package com.ericsson.oss.air.cardq.utils;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

public class CommonUtilsTest {

    @Test
    public void containsAllEmpty() {
        Map<String, String> map1 = new HashMap<>();
        map1.put("a", "1");
        map1.put("b", "2");

        Map<String, String> map2 = new HashMap<>();

        assertTrue(CommonUtils.containsAll(map1, map2));
    }

    @Test
    public void containsAllEmptyEmpty() {
        Map<String, String> map1 = new HashMap<>();

        Map<String, String> map2 = new HashMap<>();

        assertTrue(CommonUtils.containsAll(map1, map2));
    }

    @Test
    public void containsAllSuccess() {
        Map<String, String> map1 = new HashMap<>();
        map1.put("a", "1");
        map1.put("b", "2");

        Map<String, String> map2 = new HashMap<>();
        map2.put("a", "1");

        assertTrue(CommonUtils.containsAll(map1, map2));
    }

    @Test
    public void containsAllFailure() {
        Map<String, String> map1 = new HashMap<>();
        map1.put("a", "1");
        map1.put("b", "2");

        Map<String, String> map2 = new HashMap<>();
        map2.put("a", "2");

        assertFalse(CommonUtils.containsAll(map1, map2));
    }

    @Test
    public void containsAllEmptyFailure() {
        Map<String, String> map1 = new HashMap<>();

        Map<String, String> map2 = new HashMap<>();
        map2.put("a", "2");

        assertFalse(CommonUtils.containsAll(map1, map2));
    }

    @Test
    public void containsAllSame() {
        Map<String, String> map1 = new HashMap<>();
        map1.put("a", "1");
        map1.put("b", "2");

        Map<String, String> map2 = new HashMap<>();
        map1.put("a", "1");
        map1.put("b", "2");

        assertTrue(CommonUtils.containsAll(map1, map2));
    }

    @Test
    void convertListsToSingleMaps() {
        Map<String, List<String>> map = Map.of("a", List.of("a1"),
                                               "b", List.of("b1", "b2"),
                                               "c", List.of("c1"),
                                               "d", List.of("d1"));
        List<Map<String, String>> list = CommonUtils.convertListsToSingleMaps(map);
        Assertions.assertThat(list).containsExactlyInAnyOrderElementsOf(List.of(Map.of("a", "a1", "b", "b1", "c", "c1", "d", "d1"),
                                                                                Map.of("a", "a1", "b", "b2", "c", "c1", "d", "d1")));
    }

    @Test
    void createMappedItem() {
        Map<String, String> map = CommonUtils.createMappedItem("abc", "123");
        Assertions.assertThat(map).containsAllEntriesOf(Map.of("name", "abc", "value", "123"));
    }

    @Test
    void transformMapToNameValueMap() {
        List<Map<String, String>> list = List.of(Map.of("a", "a1", "b", "b1", "c", "c1", "d", "d1"),
                Map.of("a", "a1", "b", "b2", "c", "c1", "d", "d1"));
        List<List<Map<String, String>>> map = CommonUtils.transformMapToNameValueMap(list);
        Assertions.assertThat(map).containsExactlyInAnyOrderElementsOf(List.of(
                List.of(Map.of("name", "a", "value", "a1"),
                        Map.of("name", "b", "value", "b1"),
                        Map.of("name", "c", "value", "c1"),
                        Map.of("name", "d", "value", "d1")
                ),
                List.of(Map.of("name", "a", "value", "a1"),
                        Map.of("name", "b", "value", "b2"),
                        Map.of("name", "c", "value", "c1"),
                        Map.of("name", "d", "value", "d1")
                )
        ));
    }
}
