package io.geewit.utils.core.tree;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TreeNodeTest {

    static class TestNode extends TreeNode<TestNode, Long> {
    }

    @Test
    void addChild_firstChild() {
        TestNode parent = new TestNode();
        parent.id = 1L;
        TestNode child = new TestNode();
        child.id = 2L;
        parent.addChild(child);
        assertEquals(1, parent.getChildren().size());
    }

    @Test
    void addChild_duplicateNotAdded() {
        TestNode parent = new TestNode();
        parent.id = 1L;
        TestNode child = new TestNode();
        child.id = 2L;
        parent.addChild(child);
        parent.addChild(child);
        assertEquals(1, parent.getChildren().size());
    }

    @Test
    void addChild_differentChildren() {
        TestNode parent = new TestNode();
        parent.id = 1L;
        TestNode child1 = new TestNode();
        child1.id = 2L;
        TestNode child2 = new TestNode();
        child2.id = 3L;
        parent.addChild(child1);
        parent.addChild(child2);
        assertEquals(2, parent.getChildren().size());
    }

    @Test
    void addChild_nullChildrenList() {
        TestNode parent = new TestNode();
        parent.id = 1L;
        parent.children = null;
        TestNode child = new TestNode();
        child.id = 2L;
        parent.addChild(child);
        assertNotNull(parent.getChildren());
        assertEquals(1, parent.getChildren().size());
    }

    @Test
    void clear_clearsAllFields() {
        TestNode node = new TestNode();
        node.id = 1L;
        node.parentId = 0L;
        node.parentIds = "0";
        node.children = new java.util.ArrayList<>();
        node.clear();
        assertNull(node.id);
        assertNull(node.parentId);
        assertNull(node.parentIds);
        assertNull(node.children);
    }

    @Test
    void equals_sameObject() {
        TestNode node = new TestNode();
        node.id = 1L;
        assertEquals(node, node);
    }

    @Test
    void equals_differentType() {
        TestNode node = new TestNode();
        node.id = 1L;
        assertNotEquals(node, "string");
    }

    @Test
    void equals_sameId() {
        TestNode n1 = new TestNode();
        n1.id = 1L;
        TestNode n2 = new TestNode();
        n2.id = 1L;
        assertEquals(n1, n2);
    }

    @Test
    void equals_differentId() {
        TestNode n1 = new TestNode();
        n1.id = 1L;
        TestNode n2 = new TestNode();
        n2.id = 2L;
        assertNotEquals(n1, n2);
    }

    @Test
    void hashCode_sameId() {
        TestNode n1 = new TestNode();
        n1.id = 1L;
        TestNode n2 = new TestNode();
        n2.id = 1L;
        assertEquals(n1.hashCode(), n2.hashCode());
    }
}
