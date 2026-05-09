package io.geewit.utils.core.tree;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ConsumerInterfacesTest {

    static class Org extends SignedTreeNode<Org, Long> {
    }

    @Test
    void signChildConsumer_accept() {
        Org parent = new Org();
        parent.setId(1L);
        parent.setSign(5);

        Org child = new Org();
        child.setId(2L);
        child.setParentId(1L);
        child.setSign(0);

        NodeSignParameter<Long> signParam = NodeSignParameter.<Long>builder()
                .id(2L)
                .sign(3)
                .build();

        SignChildConsumer<Org, Long> consumer = (parentNode, childNode, signParameter) -> {
            if (parentNode != null && parentNode.getSign() != null && parentNode.getSign() > 0) {
                childNode.setSign(signParameter.getSign());
            }
        };

        consumer.accept(parent, child, signParam);
        assertEquals(3, child.getSign());
    }

    @Test
    void signChildConsumer_withNullParent() {
        Org child = new Org();
        child.setId(2L);
        child.setSign(0);

        NodeSignParameter<Long> signParam = NodeSignParameter.<Long>builder()
                .id(2L)
                .sign(3)
                .build();

        SignChildConsumer<Org, Long> consumer = (parentNode, childNode, signParameter) -> {
            if (parentNode == null) {
                childNode.setSign(signParameter.getSign());
            }
        };

        consumer.accept(null, child, signParam);
        assertEquals(3, child.getSign());
    }

    @Test
    void signChildConsumer_nullSignParameter() {
        Org parent = new Org();
        parent.setId(1L);

        Org child = new Org();
        child.setId(2L);

        SignChildConsumer<Org, Long> consumer = (parentNode, childNode, signParameter) -> {
            if (signParameter != null && signParameter.getSign() != null) {
                childNode.setSign(signParameter.getSign());
            }
        };

        consumer.accept(parent, child, null);
        assertNull(child.getSign());
    }

    @Test
    void signParentConsumer_accept() {
        Org parent = new Org();
        parent.setId(1L);
        parent.setSign(0);

        SignParentConsumer<Org, Long> consumer = (parentNode, allChildrenSign, overwrite) -> {
            if (parentNode != null) {
                parentNode.setSign(allChildrenSign);
            }
        };

        consumer.accept(parent, 7, false);
        assertEquals(7, parent.getSign());
    }

    @Test
    void signParentConsumer_withOverwrite() {
        Org parent = new Org();
        parent.setId(1L);
        parent.setSign(10);

        SignParentConsumer<Org, Long> consumer = (parentNode, allChildrenSign, overwrite) -> {
            if (overwrite || parentNode.getSign() == null || parentNode.getSign() == 0) {
                parentNode.setSign(allChildrenSign);
            }
        };

        consumer.accept(parent, 5, true);
        assertEquals(5, parent.getSign());
    }

    @Test
    void signParentConsumer_withoutOverwrite() {
        Org parent = new Org();
        parent.setId(1L);
        parent.setSign(10);

        SignParentConsumer<Org, Long> consumer = (parentNode, allChildrenSign, overwrite) -> {
            if (overwrite || parentNode.getSign() == null || parentNode.getSign() == 0) {
                parentNode.setSign(allChildrenSign);
            }
        };

        consumer.accept(parent, 5, false);
        // Should NOT change because existing sign is 10 and overwrite is false
        assertEquals(10, parent.getSign());
    }

    @Test
    void compressChildConsumer_accept() {
        Org parent = new Org();
        parent.setId(1L);
        parent.setSign(15); // 1111 in binary

        Org child = new Org();
        child.setId(2L);
        child.setSign(12); // 1100 in binary

        CompressChildConsumer<Org, Long> consumer = (parentNode, childNode) -> {
            if (parentNode != null && childNode != null) {
                // Compress: child_sign & parent_sign
                Integer compressed = parentNode.getSign() & childNode.getSign();
                childNode.setSign(compressed);
            }
        };

        consumer.accept(parent, child);
        assertEquals(12 & 15, child.getSign()); // 1100 & 1111 = 1100 = 12
    }

    @Test
    void compressChildConsumer_withNullParent() {
        Org child = new Org();
        child.setId(2L);
        child.setSign(12);

        CompressChildConsumer<Org, Long> consumer = (parentNode, childNode) -> {
            if (parentNode == null && childNode != null) {
                childNode.setSign(0);
            }
        };

        consumer.accept(null, child);
        assertEquals(0, child.getSign());
    }

    @Test
    void compressChildConsumer_withNullChild() {
        Org parent = new Org();
        parent.setId(1L);
        parent.setSign(15);

        CompressChildConsumer<Org, Long> consumer = (parentNode, childNode) -> {
            // Should not throw
            if (childNode != null) {
                childNode.setSign(0);
            }
        };

        assertDoesNotThrow(() -> consumer.accept(parent, null));
    }

    @Test
    void transmissionChildConsumer_accept() {
        Org parent = new Org();
        parent.setId(1L);
        parent.setSign(10);

        Org child = new Org();
        child.setId(2L);
        child.setSign(0);

        TransmissionChildConsumer<Org, Long> consumer = (parentNode, childNode) -> {
            if (parentNode != null && childNode != null && parentNode.getSign() != null) {
                childNode.setSign(parentNode.getSign());
            }
        };

        consumer.accept(parent, child);
        assertEquals(10, child.getSign());
    }

    @Test
    void transmissionChildConsumer_withNullParent() {
        Org child = new Org();
        child.setId(2L);
        child.setSign(5);

        TransmissionChildConsumer<Org, Long> consumer = (parentNode, childNode) -> {
            if (parentNode == null && childNode != null) {
                childNode.setSign(99);
            }
        };

        consumer.accept(null, child);
        assertEquals(99, child.getSign());
    }

    @Test
    void transmissionChildConsumer_withNullChild() {
        Org parent = new Org();
        parent.setId(1L);
        parent.setSign(10);

        TransmissionChildConsumer<Org, Long> consumer = (parentNode, childNode) -> {
            // Should not throw
            if (childNode != null) {
                childNode.setSign(parentNode.getSign());
            }
        };

        assertDoesNotThrow(() -> consumer.accept(parent, null));
    }
}
