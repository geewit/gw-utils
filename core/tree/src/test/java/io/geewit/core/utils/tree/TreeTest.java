package io.geewit.core.utils.tree;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.geewit.utils.uuid.UUIDUtils;
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

    @BeforeEach
    public void init() {
        int maxLevel = 5;
        int maxIndex = 5;
        nodes = this.buildTreeNodes(maxLevel, maxIndex);
    }

    /**
     *
     * @param maxDepth    最大深度
     * @param maxBreadth  最大广度
     * @return
     */
    private List<Org> buildTreeNodes(int maxDepth, int maxBreadth) {
        List<Org> orgs = new ArrayList<>();
        for(int depth = 0; depth < maxDepth; depth++) {
            Org parent = null;
            for(int breadth = 0; breadth < maxBreadth; breadth++) {
                Org org;
                if(parent == null) {
                    org = this.buildOrg(depth, breadth, null);
                } else {
                    org = this.buildOrg(depth, breadth, parent);
                }
                if(org.parentId == null) {
                    parent = org;
                }
                orgs.add(org);
            }
        }
        return orgs;
    }

    /**
     *
     * @param depth    深度
     * @param breadth  广度
     * @param parent   父节点
     * @return
     */
    private Org buildOrg(int depth, int breadth, Org parent) {
        Org org = new Org();
        long id = Double.valueOf(Math.pow(10, depth)).longValue() + breadth;
        org.setId(id);
        String name = UUIDUtils.randomUUID();
        org.setName(name);
        if(parent != null) {
            org.setParentId(parent.id);
            org.setParentIds(parent.parentIds + id + "#");
        } else {
            org.setParentIds(id + "#");
        }
        logger.info("org.id = {}, org.name = {}", id, name);
        return org;
    }

    private ObjectMapper objectMapper = new ObjectMapper();

    @Test
    public void testBuildTree() {
        try {
            logger.info("nodes: " + objectMapper.writeValueAsString(nodes));
            List<Org> tree = TreeUtils.buildTree(nodes);
            logger.info("tree: " + objectMapper.writeValueAsString(tree));
        } catch (JsonProcessingException e) {
            logger.warn(e.getMessage());
        }
    }


    @Test
    public void testBuildTreeByParentIds() {
        try {
            logger.info("nodes: " + objectMapper.writeValueAsString(nodes));
            List<Org> tree = TreeUtils.buildTreeByParentIds(nodes);
            logger.info("tree: " + objectMapper.writeValueAsString(tree));
        } catch (JsonProcessingException e) {
            logger.warn(e.getMessage());
        }
    }

    @Test
    public void testCheck() {
        Set<Long> checkingKeys = Stream.of(1L, 11L, 12L, 13L, 100L, 1000L, 10001L, 10002L, 10003L, 10004L).collect(Collectors.toSet());
        List<Org> tree = TreeUtils.buildTreeAndCascadeCheckKeys(nodes, checkingKeys);
        try {
            logger.info("tree: " + objectMapper.writeValueAsString(tree));
        } catch (JsonProcessingException e) {
            logger.warn(e.getMessage());
        }
    }
}
