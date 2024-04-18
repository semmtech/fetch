package com.semmtech.laces.fetch.imports.generic.service;

import com.semmtech.laces.fetch.visualization.model.QueryExecutionRequest;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

import static java.lang.Math.max;
import static java.lang.Math.min;

/**
 * All items in the add-on tree are given a unique id that allows to detect where in the tree it is situated.
 * This sorter sorts the elements based on this id, to make sure no element is sent to the target system before it's
 * parent or any ancestor higher up in the tree.
 */
@Component
public class TreeNodeSelectedValuesSorter {

    public void sort(QueryExecutionRequest original) {
        original.getValues()
                .stream()
                .filter(node -> !node.containsKey("treeNodeId"))
                .findFirst()
                .ifPresent(node -> new IllegalArgumentException("All nodes should have a treeNodeId "));

        Collections.sort(original.getValues(), (left, right) -> this.compare(left.get("treeNodeId"), right.get("treeNodeId")));
    }

    /**
     * Compare 2 index strings represented by a sequence numbers, separated by dots, by comparing matching parts from left to right.
     * @param left the first id-string to compare
     * @param right the second id-string to compare
     * @return <pre>a number < 0 when the first id needs to be placed after the second, a number ></pre>
     */
    private int compare(String left, String right) {
        List<Integer> leftToInt = Arrays.stream(left.split("\\.")).mapToInt(Integer::parseInt).boxed().collect(Collectors.toList());
        List<Integer> rightToInt = Arrays.stream(right.split("\\.")).mapToInt(Integer::parseInt).boxed().collect(Collectors.toList());

        int shortestSize = min(leftToInt.size(), rightToInt.size());
        int longestSize = max(leftToInt.size(), rightToInt.size());

        for (int i = 0; i < longestSize; ++ i) {
            if (i < shortestSize) {
                int result = Integer.compare(leftToInt.get(i), rightToInt.get(i));
                if (result != 0) {
                    return result;
                }
            } else if (onlySecondHasReachedLastPart(leftToInt, rightToInt, i)) {
                return 1;
            } else if (onlySecondHasReachedLastPart(rightToInt, leftToInt, i)) {
                return -1;
            }

        }
        return 0;
    }

    /**
     * Determines the order if 2 ids have the same prefix but one is longer than the other:
     * for example when comparing 1.1 with 1.1.3, 1.1 should be first in the sort order.
     * @param left the first id-list to compare
     * @param right the second id-list to compare
     * @param index the position of the part at which the 2 id-lists need to be compared
     * @return true if the left id-list is longer than the right: for example left: 1.1.3 and right:1.1 when comparing at index=1, false otherwise.
     */
    private boolean onlySecondHasReachedLastPart(List<Integer> left, List<Integer> right, int index) {
        return index < left.size() && index == right.size();
    }
}
