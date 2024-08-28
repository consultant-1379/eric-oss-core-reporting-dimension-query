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

import static java.util.stream.Collectors.toSet;

import static com.ericsson.oss.air.cardq.utils.Constants.NAME;
import static com.ericsson.oss.air.cardq.utils.Constants.VALUE;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import lombok.experimental.UtilityClass;

@UtilityClass
public class CommonUtils {
    public static <T, R> Set<R> findDuplicates(Collection<? extends T> collection, Function<? super T, ? extends R> mapper) {
        Set<R> uniques = new HashSet<>();
        return collection.stream()
                .map(mapper)
                .filter(e -> !uniques.add(e))
                .collect(toSet());
    }

    public static <K, V> boolean containsAll(Map<K, V> containerMap, Map<K, V> subMap) {
        return containerMap.entrySet().containsAll(subMap.entrySet());
    }

    /**
     * method to convert Map of lists to list of maps
     * @param map
     * @return
     */
    public static List<Map<String, String>> convertListsToSingleMaps(Map<String, List<String>> map) {
        List<Map<String, String>> list = new LinkedList<>();
        generateAllPossibleCombinations(map, new LinkedList<>(map.keySet()).listIterator(),
                                        new HashMap<>(), list);
        return list.stream().distinct().collect(Collectors.toList());
    }

    /**
     * method to generate combinations from map of lists and store in list of maps
     * @param map
     * @param iter
     * @param cur
     * @param outList
     */
    private static void generateAllPossibleCombinations(Map<String, List<String>> map, ListIterator<String> iter,
                                                          Map<String, String> cur, List<Map<String, String>> outList) {
        if (!iter.hasNext()) {
            Map<String, String> entry = new HashMap<>();

            for (Map.Entry<String, String> mapEntry : cur.entrySet()) {
                entry.put(mapEntry.getKey(), cur.get(mapEntry.getKey()));
            }
            outList.add(entry);
        } else {
            String key = iter.next();
            List<String> set = map.get(key);

            for (String value : set) {
                cur.put(key, value);
                generateAllPossibleCombinations(map, iter, cur, outList);
                cur.remove(key);
            }
            if (set.isEmpty()) {
                cur.put(key, "");
                generateAllPossibleCombinations(map, iter, cur, outList);
                cur.remove(key);
            }
            iter.previous();
        }
    }

    /**
     * method to populate map with name and value
     * @param name
     * @param value
     * @return
     */
    public static Map<String, String> createMappedItem(String name, String value) {
        Map<String, String> entry = new HashMap<>();
        entry.put(NAME, name);
        entry.put(VALUE, value);
        return entry;
    }

    /**
     * mmethod to transform map data in name and value format
     * @param listOfMaps
     * @return
     */
    public static  List<List<Map<String, String>>> transformMapToNameValueMap(List<Map<String, String>> listOfMaps) {
        List<List<Map<String, String>>> combinationsOfSets = new ArrayList<>();
        Comparator<Map<String, String>> sortByName = Comparator.comparing(x -> x.get("name"));
        listOfMaps.forEach(set -> {
            List<Map<String, String>> newSetWithName = new ArrayList<>();
            set.forEach((key, value) -> newSetWithName.add(createMappedItem(key, value)));
            newSetWithName.sort(sortByName);
            combinationsOfSets.add(newSetWithName);
        });
        return combinationsOfSets;
    }
}
