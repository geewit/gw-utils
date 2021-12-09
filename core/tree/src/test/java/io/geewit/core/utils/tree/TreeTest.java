package io.geewit.core.utils.tree;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;


public class TreeTest {
    private final static Logger logger = LoggerFactory.getLogger(TreeTest.class);

    private List<Org> nodes;

    private int maxLevel = 4;
    private int maxIndex = 10;

    @BeforeEach
    public void init() {
        nodes = this.buildTreeNodes(maxLevel, maxIndex);
    }

    /**
     * @param maxDepth   最大深度
     * @param maxBreadth 最大广度
     * @return
     */
    private List<Org> buildTreeNodes(int maxDepth, int maxBreadth) {
        List<Org> orgs = new ArrayList<>();
        for (int depth = 0; depth < maxDepth; depth++) {
            Org parent = null;
            for (int breadth = 0; breadth < maxBreadth; breadth++) {
                Org org;
                if (parent == null) {
                    org = buildOrg(depth, breadth, null);
                } else {
                    org = buildOrg(depth, breadth, parent);
                }
                if (org.parentId == null) {
                    parent = org;
                }
                orgs.add(org);
            }
        }
        return orgs;
    }

    /**
     * @param depth   深度
     * @param breadth 广度
     * @param parent  父节点
     * @return
     */
    private Org buildOrg(int depth, int breadth, Org parent) {
        Org org = new Org();
        double log10 = Math.ceil(Math.log10(maxIndex));
        long pow = Double.valueOf(Math.pow(10, log10) * depth).longValue();

        long id = pow + breadth;
        org.setId(id);
        String name = UUID.randomUUID().toString();
        org.setName(name);
        if (parent != null) {
            org.setParentId(parent.id);
            org.setParentIds(parent.parentIds + id + "#");
        } else {
            org.setParentIds(id + "#");
        }
        logger.debug("org.id = {}, org.name = {}", id, name);
        return org;
    }

    private ObjectMapper objectMapper = new ObjectMapper();

    @Test
    public void testBuildTree() {
        long start = System.currentTimeMillis();
        TreeUtils.buildTree(nodes);
        long end = System.currentTimeMillis();
        logger.info("buildTree cost: {}ms", (end - start));
    }

    @Test
    public void testBuildTreeByParentIds() {
        long start = System.currentTimeMillis();
        TreeUtils.buildTreeByParentIds(nodes);
        long end = System.currentTimeMillis();
        logger.info("buildTreeByParentIds cost: {}ms", (end - start));
    }

    @Test
    public void comparePerformance() {
        this.testBuildTreeByParentIds();
        this.testBuildTree();

        this.testBuildTree();
        this.testBuildTreeByParentIds();
    }

    @Test
    public void testBuildTreeAndCheck() {
        Set<Long> checkingKeys = Stream.of(1L, 11L, 12L, 13L, 100L, 1000L, 10001L, 10002L, 10003L, 10004L).collect(Collectors.toSet());
        Pair<List<Org>, Set<Long>> pair = TreeUtils.buildTreeAndCascadeCheckKeys(nodes, checkingKeys);
        try {
            logger.info("tree: " + objectMapper.writeValueAsString(pair.getLeft()));
            logger.info("checkedKeys: " + objectMapper.writeValueAsString(pair.getRight()));
            List<Long> sortedNodeIds = new ArrayList<>(pair.getRight());
            Collections.sort(sortedNodeIds);
            logger.info("sortedNodeIds: " + objectMapper.writeValueAsString(sortedNodeIds));
        } catch (JsonProcessingException e) {
            logger.warn(e.getMessage());
        }
    }

    @Test
    public void testCheck() {
        Set<Long> checkingKeys = Stream.of(1L, 11L, 12L, 13L, 100L, 1000L, 10001L, 10002L, 10003L, 10004L).collect(Collectors.toSet());
        List<Org> tree = TreeUtils.buildTree(nodes);
        try {
            logger.info("tree: " + objectMapper.writeValueAsString(tree));
            Set<Long> checkedNodeIds = TreeUtils.cascadeCheckKeys(tree, checkingKeys);
            List<Long> sortedNodeIds = new ArrayList<>(checkedNodeIds);
            Collections.sort(sortedNodeIds);
            logger.info("sortedNodeIds: " + objectMapper.writeValueAsString(sortedNodeIds));
        } catch (JsonProcessingException e) {
            logger.warn(e.getMessage());
        }
    }

}
