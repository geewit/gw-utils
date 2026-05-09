package io.geewit.utils.core.tree;

import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class TreeUtilsEdgeCasesTest {

    static class TestNode extends TreeNode<TestNode, Long> {
    }

    @Test
    void buildTree_withRootId_matched() {
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
    void buildTree_withRootId_notFound() {
        TestNode root = new TestNode();
        root.id = 1L;
        root.parentId = null;

        List<TestNode> nodes = Collections.singletonList(root);
        List<TestNode> result = TreeUtils.buildTree(nodes, null, 99L);

        assertTrue(result.isEmpty());
    }

    @Test
    void buildTree_rootIdWithNullParentId() {
        TestNode root = new TestNode();
        root.id = 1L;
        root.parentId = null;

        TestNode child = new TestNode();
        child.id = 2L;
        child.parentId = 1L;

        TestNode grandchild = new TestNode();
        grandchild.id = 3L;
        grandchild.parentId = 2L;

        List<TestNode> nodes = Arrays.asList(root, child, grandchild);
        List<TestNode> result = TreeUtils.buildTree(nodes, null, 2L);

        assertEquals(1, result.size());
        assertEquals(2L, result.get(0).id);
        assertEquals(1, result.get(0).children.size());
        assertEquals(3L, result.get(0).children.get(0).id);
    }

    @Test
    void buildTree_withRootPredicate_matching() {
        TestNode root = new TestNode();
        root.id = 1L;
        root.parentId = 99L; // not null, but rootPredicate will match

        TestNode child = new TestNode();
        child.id = 2L;
        child.parentId = 1L;

        List<TestNode> nodes = Arrays.asList(root, child);
        List<TestNode> result = TreeUtils.buildTree(nodes, n -> n.id == 1L);

        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).id);
    }

    @Test
    void buildTree_withRootPredicate_nullParent() {
        TestNode root = new TestNode();
        root.id = 1L;
        root.parentId = null;

        TestNode child = new TestNode();
        child.id = 2L;
        child.parentId = 1L;

        List<TestNode> nodes = Arrays.asList(root, child);
        // rootPredicate takes precedence when both are null
        List<TestNode> result = TreeUtils.buildTree(nodes, n -> n.parentId == null);

        assertEquals(1, result.size());
    }

    @Test
    void buildTree_multipleRoots() {
        TestNode root1 = new TestNode();
        root1.id = 1L;
        root1.parentId = null;

        TestNode root2 = new TestNode();
        root2.id = 2L;
        root2.parentId = null;

        TestNode child1 = new TestNode();
        child1.id = 3L;
        child1.parentId = 1L;

        List<TestNode> nodes = Arrays.asList(root1, root2, child1);
        List<TestNode> result = TreeUtils.buildTree(nodes, n -> n.parentId == null);

        assertEquals(2, result.size());
    }

    @Test
    void buildTree_deepHierarchy() {
        TestNode root = new TestNode();
        root.id = 1L;
        root.parentId = null;

        TestNode level1 = new TestNode();
        level1.id = 2L;
        level1.parentId = 1L;

        TestNode level2 = new TestNode();
        level2.id = 3L;
        level2.parentId = 2L;

        TestNode level3 = new TestNode();
        level3.id = 4L;
        level3.parentId = 3L;

        List<TestNode> nodes = new ArrayList<>(Arrays.asList(root, level1, level2, level3));
        List<TestNode> result = TreeUtils.buildTree(nodes, n -> n.parentId == null);

        assertEquals(1, result.size());
        TestNode resultRoot = result.get(0);
        assertEquals(1L, resultRoot.id);
        assertEquals(1, resultRoot.children.size());
        assertEquals(2L, resultRoot.children.get(0).id);
    }

    @Test
    void buildTree_siblingRelationships() {
        TestNode parent = new TestNode();
        parent.id = 1L;
        parent.parentId = null;

        TestNode child1 = new TestNode();
        child1.id = 2L;
        child1.parentId = 1L;

        TestNode child2 = new TestNode();
        child2.id = 3L;
        child2.parentId = 1L;

        List<TestNode> nodes = Arrays.asList(parent, child1, child2);
        List<TestNode> result = TreeUtils.buildTree(nodes, n -> n.parentId == null);

        assertEquals(1, result.size());
        assertEquals(2, result.get(0).children.size());
    }

    @Test
    void buildTree_duplicateRoots() {
        TestNode root1 = new TestNode();
        root1.id = 1L;
        root1.parentId = null;

        TestNode root2 = new TestNode();
        root2.id = 1L; // same id
        root2.parentId = null;

        List<TestNode> nodes = Arrays.asList(root1, root2);
        List<TestNode> result = TreeUtils.buildTree(nodes, n -> n.parentId == null);
        // Should keep first
        assertTrue(result.size() >= 1);
    }
}
