package io.geewit.utils.core.tree;

import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class TreeTraverseContextMethodsTest {

    static class Org extends SignedTreeNode<Org, Long> {
    }

    private Org createNode(Long id, Long parentId, Integer sign) {
        Org node = new Org();
        node.setId(id);
        node.setParentId(parentId);
        node.setSign(sign);
        return node;
    }

    @Test
    void buildTree_withNullNodes() {
        TreeTraverseContext<Org, Long> ctx = TreeTraverseContext.<Org, Long>builder()
                .nodes(null)
                .build();
        // buildTree is private, but we can test via cascadeSign which calls it
        assertDoesNotThrow(ctx::cascadeSign);
    }

    @Test
    void buildTree_withEmptyNodes() {
        List<Org> nodes = Collections.emptyList();
        TreeTraverseContext<Org, Long> ctx = TreeTraverseContext.<Org, Long>builder()
                .nodes(nodes)
                .signChildConsumer((p, c, s) -> {})
                .signParentConsumer((p, s, o) -> {})
                .compressChildConsumer((p, c) -> {})
                .transmissionChildConsumer((p, c) -> {})
                .build();
        assertDoesNotThrow(ctx::cascadeSign);
    }

    @Test
    void buildTree_withRootPredicate() {
        Org root = createNode(1L, null, 0);
        Org child = createNode(2L, 1L, 0);

        List<Org> nodes = Arrays.asList(root, child);
        TreeTraverseContext<Org, Long> ctx = TreeTraverseContext.<Org, Long>builder()
                .nodes(nodes)
                .rootPredicate(n -> n.getId() == 1L)
                .signChildConsumer((p, c, s) -> {})
                .signParentConsumer((p, s, o) -> {})
                .compressChildConsumer((p, c) -> {})
                .transmissionChildConsumer((p, c) -> {})
                .build();
        assertDoesNotThrow(ctx::cascadeSign);
    }

    @Test
    void cascadeSign_withNullSignAndNoOverwrite_keepsNull() {
        Org node = createNode(1L, null, null);

        List<Org> nodes = new ArrayList<>(Collections.singletonList(node));
        TreeTraverseContext<Org, Long> ctx = TreeTraverseContext.<Org, Long>builder()
                .nodes(nodes)
                .signChildConsumer((p, c, s) -> {
                    if (s != null && s.getSign() != null) {
                        c.setSign(s.getSign());
                    }
                })
                .signParentConsumer((p, s, o) -> {})
                .compressChildConsumer((p, c) -> {})
                .transmissionChildConsumer((p, c) -> {})
                .build();
        ctx.cascadeSign();
        // With null signParameters and overwrite=false, sign stays as-is
        assertNull(node.getSign());
    }

    @Test
    void cascadeSign_withoutOverwrite_preservesExistingSign() {
        Org node = createNode(1L, null, 5);

        List<Org> nodes = Collections.singletonList(node);
        TreeTraverseContext<Org, Long> ctx = TreeTraverseContext.<Org, Long>builder()
                .nodes(nodes)
                .overwrite(false)
                .signChildConsumer((p, c, s) -> {})
                .signParentConsumer((p, s, o) -> {})
                .compressChildConsumer((p, c) -> {})
                .transmissionChildConsumer((p, c) -> {})
                .build();
        ctx.cascadeSign();
        assertEquals(5, node.getSign());
    }

    @Test
    void cascadeSign_withOverwrite_setsZeroWhenNoSignParam() {
        Org node = createNode(1L, null, 5);

        List<Org> nodes = Collections.singletonList(node);
        TreeTraverseContext<Org, Long> ctx = TreeTraverseContext.<Org, Long>builder()
                .nodes(nodes)
                .overwrite(true)
                .signChildConsumer((p, c, s) -> {})
                .signParentConsumer((p, s, o) -> {})
                .compressChildConsumer((p, c) -> {})
                .transmissionChildConsumer((p, c) -> {})
                .build();
        ctx.cascadeSign();
        assertEquals(0, node.getSign());
    }

    @Test
    void cascadeSign_withSignParameters() {
        Org node = createNode(1L, null, 0);

        List<Org> nodes = Collections.singletonList(node);
        Set<NodeSignParameter<Long>> signParams = new HashSet<>();
        signParams.add(NodeSignParameter.<Long>builder().id(1L).sign(3).build());

        TreeTraverseContext<Org, Long> ctx = TreeTraverseContext.<Org, Long>builder()
                .nodes(nodes)
                .signParameters(signParams)
                .signChildConsumer((p, c, s) -> c.setSign(s.getSign()))
                .signParentConsumer((p, s, o) -> {})
                .compressChildConsumer((p, c) -> {})
                .transmissionChildConsumer((p, c) -> {})
                .build();
        ctx.cascadeSign();
        assertEquals(3, node.getSign());
    }

    @Test
    void cascadeSign_transmissionDownFalse() {
        Org parent = createNode(1L, null, 0);
        Org child = createNode(2L, 1L, 0);

        List<Org> nodes = Arrays.asList(parent, child);
        Set<NodeSignParameter<Long>> signParams = new HashSet<>();
        signParams.add(NodeSignParameter.<Long>builder().id(1L).sign(3).transmissionDown(false).build());

        TreeTraverseContext<Org, Long> ctx = TreeTraverseContext.<Org, Long>builder()
                .nodes(nodes)
                .signParameters(signParams)
                .signChildConsumer((p, c, s) -> c.setSign(s.getSign()))
                .signParentConsumer((p, s, o) -> {})
                .compressChildConsumer((p, c) -> {})
                .transmissionChildConsumer((p, c) -> {})
                .build();
        ctx.cascadeSign();
        // Child should not receive parent's sign since transmissionDown is false
        assertEquals(0, child.getSign());
    }

    @Test
    void cascadeSign_compressEnabled() {
        Org root = createNode(1L, null, 0);
        Org child = createNode(2L, 1L, 0);

        List<Org> nodes = Arrays.asList(root, child);
        TreeTraverseContext<Org, Long> ctx = TreeTraverseContext.<Org, Long>builder()
                .nodes(nodes)
                .compress(true)
                .signChildConsumer((p, c, s) -> {})
                .signParentConsumer((p, s, o) -> {})
                .compressChildConsumer((p, c) -> {})
                .transmissionChildConsumer((p, c) -> {})
                .build();
        assertDoesNotThrow(ctx::cascadeSign);
    }

    @Test
    void cascadeSign_transmissionDisabled() {
        Org parent = createNode(1L, null, 0);
        Org child = createNode(2L, 1L, 0);

        List<Org> nodes = Arrays.asList(parent, child);
        TreeTraverseContext<Org, Long> ctx = TreeTraverseContext.<Org, Long>builder()
                .nodes(nodes)
                .transmission(false)
                .signChildConsumer((p, c, s) -> {})
                .signParentConsumer((p, s, o) -> {})
                .compressChildConsumer((p, c) -> {})
                .transmissionChildConsumer((p, c) -> {})
                .build();
        assertDoesNotThrow(ctx::cascadeSign);
    }

    @Test
    void buildSignParametersMap_withNullSignParameters() {
        Org node = createNode(1L, null, 0);
        List<Org> nodes = Collections.singletonList(node);

        TreeTraverseContext<Org, Long> ctx = TreeTraverseContext.<Org, Long>builder()
                .nodes(nodes)
                .signParameters(null)
                .signChildConsumer((p, c, s) -> {})
                .signParentConsumer((p, s, o) -> {})
                .compressChildConsumer((p, c) -> {})
                .transmissionChildConsumer((p, c) -> {})
                .build();
        assertDoesNotThrow(ctx::cascadeSign);
    }

    @Test
    void buildSignParametersMap_withEmptySignParameters() {
        Org node = createNode(1L, null, 0);
        List<Org> nodes = Collections.singletonList(node);

        TreeTraverseContext<Org, Long> ctx = TreeTraverseContext.<Org, Long>builder()
                .nodes(nodes)
                .signParameters(Collections.emptySet())
                .signChildConsumer((p, c, s) -> {})
                .signParentConsumer((p, s, o) -> {})
                .compressChildConsumer((p, c) -> {})
                .transmissionChildConsumer((p, c) -> {})
                .build();
        assertDoesNotThrow(ctx::cascadeSign);
    }

    @Test
    void signByParameters_withNullSignInParameter() {
        Org node = createNode(1L, null, 0);
        List<Org> nodes = Collections.singletonList(node);

        Set<NodeSignParameter<Long>> signParams = new HashSet<>();
        signParams.add(NodeSignParameter.<Long>builder().id(1L).sign(null).build()); // null sign

        TreeTraverseContext<Org, Long> ctx = TreeTraverseContext.<Org, Long>builder()
                .nodes(nodes)
                .signParameters(signParams)
                .signChildConsumer((p, c, s) -> {})
                .signParentConsumer((p, s, o) -> {})
                .compressChildConsumer((p, c) -> {})
                .transmissionChildConsumer((p, c) -> {})
                .build();
        assertDoesNotThrow(ctx::cascadeSign);
    }

    @Test
    void signByParameters_withNullIdInParameter() {
        Org node = createNode(1L, null, 0);
        List<Org> nodes = Collections.singletonList(node);

        Set<NodeSignParameter<Long>> signParams = new HashSet<>();
        signParams.add(NodeSignParameter.<Long>builder().id(null).sign(1).build()); // null id

        TreeTraverseContext<Org, Long> ctx = TreeTraverseContext.<Org, Long>builder()
                .nodes(nodes)
                .signParameters(signParams)
                .signChildConsumer((p, c, s) -> {})
                .signParentConsumer((p, s, o) -> {})
                .compressChildConsumer((p, c) -> {})
                .transmissionChildConsumer((p, c) -> {})
                .build();
        assertDoesNotThrow(ctx::cascadeSign);
    }

    @Test
    void transmissionAndCompressDownSign_withTransmissionAndCompress() {
        Org parent = createNode(1L, null, 1);
        Org child = createNode(2L, 1L, 2);

        List<Org> nodes = Arrays.asList(parent, child);
        TreeTraverseContext<Org, Long> ctx = TreeTraverseContext.<Org, Long>builder()
                .nodes(nodes)
                .transmission(true)
                .compress(true)
                .signChildConsumer((p, c, s) -> {})
                .signParentConsumer((p, s, o) -> {})
                .compressChildConsumer((p, c) -> {})
                .transmissionChildConsumer((p, c) -> {})
                .build();
        assertDoesNotThrow(ctx::cascadeSign);
    }

    @Test
    void transmissionAndCompressDownSign_withNullParentId() {
        Org root = createNode(1L, null, 0);
        Org child = createNode(2L, 1L, 0);

        List<Org> nodes = Arrays.asList(root, child);
        TreeTraverseContext<Org, Long> ctx = TreeTraverseContext.<Org, Long>builder()
                .nodes(nodes)
                .transmission(true)
                .compress(true)
                .signChildConsumer((p, c, s) -> {})
                .signParentConsumer((p, s, o) -> {})
                .compressChildConsumer((p, c) -> {})
                .transmissionChildConsumer((p, c) -> {})
                .build();
        assertDoesNotThrow(ctx::cascadeSign);
    }

    @Test
    void transmissionAndCompressDownSign_withNodeNotInMap() {
        Org root = createNode(1L, null, 0);
        Org orphan = createNode(2L, 99L, 0); // parent not in nodeMap

        List<Org> nodes = Arrays.asList(root, orphan);
        TreeTraverseContext<Org, Long> ctx = TreeTraverseContext.<Org, Long>builder()
                .nodes(nodes)
                .signChildConsumer((p, c, s) -> {})
                .signParentConsumer((p, s, o) -> {})
                .compressChildConsumer((p, c) -> {})
                .transmissionChildConsumer((p, c) -> {})
                .build();
        assertDoesNotThrow(ctx::cascadeSign);
    }

    @Test
    void setNodeSign_withNullNode() {
        Org node = createNode(1L, null, 0);
        List<Org> nodes = Collections.singletonList(node);

        TreeTraverseContext<Org, Long> ctx = TreeTraverseContext.<Org, Long>builder()
                .nodes(nodes)
                .signChildConsumer((p, c, s) -> {})
                .signParentConsumer((p, s, o) -> {})
                .compressChildConsumer((p, c) -> {})
                .transmissionChildConsumer((p, c) -> {})
                .build();
        // setNodeSign is called internally - just verify cascadeSign works
        assertDoesNotThrow(ctx::cascadeSign);
    }

    @Test
    void setNodeSign_withParentInSignParameters() {
        Org parent = createNode(1L, null, 0);
        Org child = createNode(2L, 1L, 0);

        List<Org> nodes = Arrays.asList(parent, child);
        Set<NodeSignParameter<Long>> signParams = new HashSet<>();
        signParams.add(NodeSignParameter.<Long>builder().id(1L).sign(0).transmissionDown(true).build());

        TreeTraverseContext<Org, Long> ctx = TreeTraverseContext.<Org, Long>builder()
                .nodes(nodes)
                .signParameters(signParams)
                .signChildConsumer((p, c, s) -> {
                    if (s.getSign() != null && s.getSign() == 0) {
                        c.setSign(0);
                    }
                })
                .signParentConsumer((p, s, o) -> {})
                .compressChildConsumer((p, c) -> {})
                .transmissionChildConsumer((p, c) -> {})
                .build();
        assertDoesNotThrow(ctx::cascadeSign);
    }

    @Test
    void setNodeSign_withChildNodeTransmission() {
        Org parent = createNode(1L, null, 0);
        Org child = createNode(2L, 1L, 0);
        child.setTransmission(true);

        List<Org> nodes = Arrays.asList(parent, child);
        Set<NodeSignParameter<Long>> signParams = new HashSet<>();
        signParams.add(NodeSignParameter.<Long>builder().id(2L).sign(5).build());

        TreeTraverseContext<Org, Long> ctx = TreeTraverseContext.<Org, Long>builder()
                .nodes(nodes)
                .signParameters(signParams)
                .signChildConsumer((p, c, s) -> c.setSign(s.getSign()))
                .signParentConsumer((p, s, o) -> {})
                .compressChildConsumer((p, c) -> {})
                .transmissionChildConsumer((p, c) -> {})
                .build();
        assertDoesNotThrow(ctx::cascadeSign);
        assertEquals(5, child.getSign());
    }

    @Test
    void signByParameters_allChildrenSignCalculation() {
        Org parent = createNode(1L, null, 0);
        Org child1 = createNode(2L, 1L, 3);
        Org child2 = createNode(3L, 1L, 5);

        List<Org> nodes = Arrays.asList(parent, child1, child2);
        TreeTraverseContext<Org, Long> ctx = TreeTraverseContext.<Org, Long>builder()
                .nodes(nodes)
                .signChildConsumer((p, c, s) -> {})
                .signParentConsumer((p, s, o) -> {
                    if (p != null) {
                        p.setSign(s);
                    }
                })
                .compressChildConsumer((p, c) -> {})
                .transmissionChildConsumer((p, c) -> {})
                .build();
        assertDoesNotThrow(ctx::cascadeSign);
    }

    @Test
    void signByParameters_withSiblings() {
        Org parent = createNode(1L, null, 0);
        Org child1 = createNode(2L, 1L, 4);
        Org child2 = createNode(3L, 1L, 2);

        List<Org> nodes = Arrays.asList(parent, child1, child2);
        TreeTraverseContext<Org, Long> ctx = TreeTraverseContext.<Org, Long>builder()
                .nodes(nodes)
                .signChildConsumer((p, c, s) -> {})
                .signParentConsumer((p, s, o) -> {
                    if (p != null) {
                        p.setSign(s);
                    }
                })
                .compressChildConsumer((p, c) -> {})
                .transmissionChildConsumer((p, c) -> {})
                .build();
        assertDoesNotThrow(ctx::cascadeSign);
    }

    @Test
    void clear_afterClear_gettersReturnNull() {
        Org node = createNode(1L, null, 0);
        List<Org> nodes = new ArrayList<>();
        nodes.add(node);

        TreeTraverseContext<Org, Long> ctx = TreeTraverseContext.<Org, Long>builder()
                .nodes(nodes)
                .rootId(1L)
                .signParameters(new HashSet<>())
                .signChildConsumer((p, c, s) -> {})
                .signParentConsumer((p, s, o) -> {})
                .compressChildConsumer((p, c) -> {})
                .transmissionChildConsumer((p, c) -> {})
                .build();
        ctx.clear();
        assertNull(ctx.getNodes());
        assertNull(ctx.getRootId());
        assertNull(ctx.getSignParameters());
        assertNull(ctx.getSignParametersMap());
        assertNull(ctx.getNodeMap());
        assertNull(ctx.getRoots());
        assertNull(ctx.getSignChildConsumer());
        assertNull(ctx.getSignParentConsumer());
        assertNull(ctx.getCompressChildConsumer());
        assertNull(ctx.getTransmissionChildConsumer());
        assertNull(ctx.getRootPredicate());
    }
}
