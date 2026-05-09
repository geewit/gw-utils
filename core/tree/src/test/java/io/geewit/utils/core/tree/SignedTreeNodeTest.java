package io.geewit.utils.core.tree;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SignedTreeNodeTest {

    static class TestSignedNode extends SignedTreeNode<TestSignedNode, Long> {
    }

    @Test
    void clear_clearsAllFields() {
        TestSignedNode node = new TestSignedNode();
        node.id = 1L;
        node.parentId = 0L;
        node.parentIds = "0";
        node.sign = 10;
        node.transmission = true;
        node.children = new java.util.ArrayList<>();
        node.clear();
        assertNull(node.id);
        assertNull(node.parentId);
        assertNull(node.parentIds);
        assertNull(node.children);
        assertNull(node.sign);
        assertNull(node.transmission);
    }

    @Test
    void equals_sameObject() {
        TestSignedNode node = new TestSignedNode();
        node.id = 1L;
        assertEquals(node, node);
    }

    @Test
    void equals_differentType() {
        TestSignedNode node = new TestSignedNode();
        node.id = 1L;
        assertNotEquals(node, "string");
    }

    @Test
    void equals_sameId() {
        TestSignedNode n1 = new TestSignedNode();
        n1.id = 1L;
        TestSignedNode n2 = new TestSignedNode();
        n2.id = 1L;
        assertEquals(n1, n2);
    }

    @Test
    void equals_differentId() {
        TestSignedNode n1 = new TestSignedNode();
        n1.id = 1L;
        TestSignedNode n2 = new TestSignedNode();
        n2.id = 2L;
        assertNotEquals(n1, n2);
    }

    @Test
    void hashCode_sameId() {
        TestSignedNode n1 = new TestSignedNode();
        n1.id = 1L;
        TestSignedNode n2 = new TestSignedNode();
        n2.id = 1L;
        assertEquals(n1.hashCode(), n2.hashCode());
    }

    @Test
    void hashCode_differentId() {
        TestSignedNode n1 = new TestSignedNode();
        n1.id = 1L;
        TestSignedNode n2 = new TestSignedNode();
        n2.id = 2L;
        assertNotEquals(n1.hashCode(), n2.hashCode());
    }

    @Test
    void signAndTransmission_defaultsToNull() {
        TestSignedNode node = new TestSignedNode();
        assertNull(node.sign);
        assertNull(node.transmission);
    }

    @Test
    void setSign_and_getSign() {
        TestSignedNode node = new TestSignedNode();
        node.setSign(5);
        assertEquals(5, node.getSign());
    }

    @Test
    void setTransmission_and_getTransmission() {
        TestSignedNode node = new TestSignedNode();
        node.setTransmission(true);
        assertTrue(node.getTransmission());
    }
}
