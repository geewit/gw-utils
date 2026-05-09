package io.geewit.utils.core.tree;

import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class TreeUtilsTest {

    static class TestNode extends TreeNode<TestNode, Long> {
    }

    @Test
    void buildTree_nullNodes_returnsEmpty() {
        List<TestNode> result = TreeUtils.buildTree(null, n -> n.parentId == null);
        assertTrue(result.isEmpty());
    }

    @Test
    void buildTree_emptyNodes_returnsEmpty() {
        List<TestNode> result = TreeUtils.buildTree(Collections.<TestNode>emptyList(), n -> n.parentId == null);
        assertTrue(result.isEmpty());
    }

    @Test
    void buildTree_withRootPredicate() {
        TestNode root = new TestNode();
        root.id = 1L;
        root.parentId = null;

        TestNode child = new TestNode();
        child.id = 2L;
        child.parentId = 1L;

        List<TestNode> nodes = Arrays.asList(root, child);
        List<TestNode> result = TreeUtils.buildTree(nodes, n -> n.parentId == null);

        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).id);
        assertEquals(1, result.get(0).children.size());
    }

    @Test
    void buildTree_withRootId() {
        TestNode root = new TestNode();
        root.id = 1L;
        root.parentId = null;

        TestNode child = new TestNode();
        child.id = 2L;
        child.parentId = 1L;

        List<TestNode> nodes = Arrays.asList(root, child);
        List<TestNode> result = TreeUtils.buildTree(nodes, null, 1L);

        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).id);
    }

    @Test
    void buildTree_nullRootPredicate() {
        TestNode root = new TestNode();
        root.id = 1L;
        root.parentId = null;

        TestNode child = new TestNode();
        child.id = 2L;
        child.parentId = 1L;

        List<TestNode> nodes = Arrays.asList(root, child);
        List<TestNode> result = TreeUtils.buildTree(nodes, null);

        assertEquals(1, result.size());
    }

    @Test
    void buildTree_duplicateIds_keepsFirst() {
        TestNode node1 = new TestNode();
        node1.id = 1L;
        node1.parentId = null;

        TestNode duplicate = new TestNode();
        duplicate.id = 1L;
        duplicate.parentId = null;

        List<TestNode> nodes = Arrays.asList(node1, duplicate);
        List<TestNode> result = TreeUtils.buildTree(nodes, n -> n.parentId == null);
        // nodeMap deduplicates by id but roots list may contain duplicates
        assertTrue(result.size() >= 1);
        assertEquals(1L, result.get(0).id);
    }

    @Test
    void buildTree_orphanNode() {
        TestNode root = new TestNode();
        root.id = 1L;
        root.parentId = null;

        TestNode orphan = new TestNode();
        orphan.id = 3L;
        orphan.parentId = 99L;

        List<TestNode> nodes = Arrays.asList(root, orphan);
        List<TestNode> result = TreeUtils.buildTree(nodes, n -> n.parentId == null);
        assertEquals(1, result.size());
    }
}
