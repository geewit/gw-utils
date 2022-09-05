package io.geewit.core.utils.tree;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;


public class TreeTest {
    private final static Logger logger = LoggerFactory.getLogger(TreeTest.class);

    private List<Org> nodes1;
    private List<Org> nodes2;

    public void init1() {
        nodes1 = new ArrayList<>();
        Org root = new Org();
        root.setId(1L);
        root.setName("root");
//        root.setSign(2);
        nodes1.add(root);
        Org child1_1 = new Org();
        child1_1.setId(11L);
        child1_1.setParentId(1L);
        child1_1.setName("child1_1");
        nodes1.add(child1_1);
        Org child1_2 = new Org();
        child1_2.setId(12L);
        child1_2.setParentId(1L);
        child1_2.setName("child1_2");
        child1_2.setSign(1);
        nodes1.add(child1_2);
        Org child2_1 = new Org();//
        child2_1.setId(101L);
        child2_1.setParentId(11L);
        child2_1.setName("child2_1");
        child2_1.setSign(2);
        nodes1.add(child2_1);
        Org child2_2 = new Org();
        child2_2.setId(102L);
        child2_2.setParentId(11L);
        child2_2.setSign(1);
        child2_2.setName("child2_2");
        nodes1.add(child2_2);
        Org child3_1 = new Org();
        child3_1.setId(1001L);
        child3_1.setParentId(101L);
        child3_1.setName("child3_1");
        nodes1.add(child3_1);
        Org child4_1 = new Org();
        child4_1.setId(10001L);
        child4_1.setParentId(1001L);
        child4_1.setName("child4_1");
        nodes1.add(child4_1);
    }

    public void init2() {
        nodes2 = new ArrayList<>();
        Org root = new Org();
        root.setId(1L);
        root.setName("root");
//        root.setSign(2);
        nodes2.add(root);
        Org child1_1 = new Org();
        child1_1.setId(11L);
        child1_1.setParentId(1L);
        child1_1.setName("child1_1");
        nodes2.add(child1_1);
        Org child1_2 = new Org();
        child1_2.setId(12L);
        child1_2.setParentId(1L);
        child1_2.setName("child1_2");
        nodes2.add(child1_2);
        Org child2_1 = new Org();//
        child2_1.setId(101L);
        child2_1.setParentId(11L);
        child2_1.setName("child2_1");
        nodes2.add(child2_1);
        Org child3_1 = new Org();
        child3_1.setId(1001L);
        child3_1.setParentId(101L);
        child3_1.setSign(2);
        child3_1.setName("child3_1");
        nodes2.add(child3_1);
        Org child4_1 = new Org();
        child4_1.setId(10001L);
        child4_1.setParentId(1001L);
        child4_1.setName("child3_1");
        nodes2.add(child4_1);
        Org child5_1 = new Org();
        child5_1.setId(100001L);
        child5_1.setParentId(10001L);
        child5_1.setName("child5_1");
        nodes2.add(child5_1);
        Org child5_2 = new Org();
        child5_2.setId(100002L);
        child5_2.setParentId(10001L);
        child5_2.setName("child5_2");
        nodes2.add(child5_2);
        Org child3_2 = new Org();
        child3_2.setId(1002L);
        child3_2.setParentId(101L);
        child3_2.setName("child3_2");
        child3_2.setSign(1);
        nodes2.add(child3_2);
        Org child3_3 = new Org();
        child3_3.setId(1003L);
        child3_3.setParentId(101L);
        child3_3.setName("child3_3");
        child3_3.setSign(1);
        nodes2.add(child3_3);
        Org child3_4 = new Org();
        child3_4.setId(1004L);
        child3_4.setParentId(101L);
        child3_4.setName("child3_4");
        nodes2.add(child3_4);
        Org child3_5 = new Org();
        child3_5.setId(1005L);
        child3_5.setParentId(101L);
        child3_5.setName("child3_5");
        nodes2.add(child3_5);
    }

    private ObjectMapper objectMapper = new ObjectMapper();

    @Test
    public void testBuildTree() {
        long start = System.currentTimeMillis();
        SignContext<Org, Long> signContext = SignContext.<Org, Long>builder().nodes(nodes1).build();
        signContext.buildTree();
        long end = System.currentTimeMillis();
        try {
            logger.info("tree: " + objectMapper.writeValueAsString(signContext.getRoots()));
        } catch (JsonProcessingException e) {
            logger.warn(e.getMessage());
        }
        logger.info("buildTree cost: {}ms", (end - start));
    }

    @Test
    public void testBuildTreeByParentIds() {
        init1();
        long start = System.currentTimeMillis();
        TreeUtils.buildTreeByParentIds(nodes1);
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
        Set<Long> checkingKeys = Stream.of(1L, 11L, 12L).collect(Collectors.toSet());
        Pair<List<Org>, Set<Long>> pair = TreeUtils.buildTreeAndCascadeCheckKeys(nodes1, checkingKeys);
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
        List<Org> tree = TreeUtils.buildTree(nodes1);
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

    @Test
    public void testSign1() {
        init1();
        long start = System.currentTimeMillis();
        SignContext<Org, Long> signContext = SignContext.<Org, Long>builder().nodes(nodes1).build();
        signContext.buildTree();
        signContext.cascadeSignRoots();
        long end = System.currentTimeMillis();
        try {
            logger.info("tree: " + objectMapper.writeValueAsString(signContext.getRoots()));
        } catch (JsonProcessingException e) {
            logger.warn(e.getMessage());
        }
        logger.info("buildTree cost: {}ms", (end - start));
    }

    @Test
    public void testSign2() {
        init2();
        long start = System.currentTimeMillis();
        SignContext<Org, Long> signContext = SignContext.<Org, Long>builder().nodes(nodes2).build();
        signContext.buildTree();
        signContext.cascadeSignRoots();
        long end = System.currentTimeMillis();
        try {
            logger.info("tree: " + objectMapper.writeValueAsString(signContext.getRoots()));
        } catch (JsonProcessingException e) {
            logger.warn(e.getMessage());
        }
        logger.info("buildTree cost: {}ms", (end - start));
    }
}
