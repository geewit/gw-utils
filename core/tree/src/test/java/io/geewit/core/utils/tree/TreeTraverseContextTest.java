package io.geewit.core.utils.tree;

import java.util.*;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class TreeTraverseContextTest {

    private static final SignChildConsumer<Org, Long> signChildConsumer = (parentNode, childNode, simpleNodeSign) -> {
        Integer sign = simpleNodeSign.getSign();
        if (parentNode == null) {
            childNode.setSign(sign);
            return;
        }
        if (parentNode.sign > 0) {
            if (sign > 1) {
                childNode.setSign(sign);
            } else {
                childNode.setSign(1);
            }
        } else {
            childNode.setSign(sign);
        }
    };

    private static final SignParentConsumer<Org, Long> signParentConsumer = (parentNode, allChildrenSign, overwrite) -> {
        if (allChildrenSign > 0 && parentNode.sign == 0) {
            parentNode.setSign(1);
            return;
        }
        if (allChildrenSign == 0 && parentNode.sign > 0 && overwrite) {
            parentNode.setSign(0);
        }
    };

    private static final CompressChildConsumer<Org, Long> compressChildConsumer = (parentNode, childNode) -> {
        if (parentNode == null) {
            return;
        }
        if (parentNode.sign != null && parentNode.sign > 0 && childNode.sign <= 1) {
            childNode.setSign(0);
        }
    };

    private static final TransmissionChildConsumer<Org, Long> transmissionChildConsumer = (parentNode, childNode) -> {
        if (parentNode == null) {
            return;
        }
        if (parentNode.sign != null && parentNode.sign > 0 && childNode.sign < 1) {
            childNode.setSign(1);
        }
    };

    @Test
    void testBuildTreeAndMarkNodes01() {
        List<Org> nodes = new ArrayList<>();
        nodes.add(Org.builder().id(1L).parentId(null).sign(1).build());
        nodes.add(Org.builder().id(2L).parentId(1L).sign(1).build());
        nodes.add(Org.builder().id(3L).parentId(2L).sign(1).build());
        nodes.add(Org.builder().id(4L).parentId(2L).sign(1).build());
        nodes.add(Org.builder().id(5L).parentId(3L).sign(2).build());
        nodes.add(Org.builder().id(6L).parentId(4L).sign(2).build());
        nodes.add(Org.builder().id(7L).parentId(6L).sign(1).build());
        nodes.add(Org.builder().id(8L).parentId(7L).sign(1).build());
        nodes.add(Org.builder().id(9L).parentId(8L).sign(1).build());
        nodes.add(Org.builder().id(10L).parentId(9L).sign(2).build());
        nodes.add(Org.builder().id(11L).parentId(10L).sign(2).build());

        Set<NodeSignParameter<Long>> signParameters = new HashSet<>();
        signParameters.add(NodeSignParameter.<Long>builder().id(2L).sign(1).build());
        signParameters.add(NodeSignParameter.<Long>builder().id(6L).sign(2).build());
        signParameters.add(NodeSignParameter.<Long>builder().id(8L).sign(2).build());

        TreeTraverseContext<Org, Long> treeTraversalContext = TreeTraverseContext.<Org, Long>builder()
                .nodes(nodes)
                .signParameters(signParameters)
                .overwrite(true)
                .transmission(true)
                .needCompress(true)
                .signChildConsumer(signChildConsumer)
                .signParentConsumer(signParentConsumer)
                .compressChildConsumer(compressChildConsumer)
                .transmissionChildConsumer(transmissionChildConsumer)
                .build();
        treeTraversalContext.cascadeSign();

        assertEquals(1, nodes.stream().filter(n -> n.id.equals(1L)).findFirst().get().sign);
        assertEquals(0, nodes.stream().filter(n -> n.id.equals(2L)).findFirst().get().sign);
        assertEquals(0, nodes.stream().filter(n -> n.id.equals(3L)).findFirst().get().sign);
        assertEquals(0, nodes.stream().filter(n -> n.id.equals(4L)).findFirst().get().sign);
        assertEquals(0, nodes.stream().filter(n -> n.id.equals(5L)).findFirst().get().sign);
        assertEquals(2, nodes.stream().filter(n -> n.id.equals(6L)).findFirst().get().sign);
        assertEquals(0, nodes.stream().filter(n -> n.id.equals(7L)).findFirst().get().sign);
        assertEquals(2, nodes.stream().filter(n -> n.id.equals(8L)).findFirst().get().sign);
        assertEquals(0, nodes.stream().filter(n -> n.id.equals(9L)).findFirst().get().sign);
        assertEquals(0, nodes.stream().filter(n -> n.id.equals(10L)).findFirst().get().sign);
        assertEquals(0, nodes.stream().filter(n -> n.id.equals(11L)).findFirst().get().sign);
    }

    @Test
    void testBuildTreeAndMarkNodes02() {
        List<Org> nodes = new ArrayList<>();
        nodes.add(Org.builder().id(1L).parentId(null).sign(1).build());
        nodes.add(Org.builder().id(2L).parentId(1L).sign(1).build());
        nodes.add(Org.builder().id(3L).parentId(2L).sign(1).build());
        nodes.add(Org.builder().id(4L).parentId(2L).sign(1).build());
        nodes.add(Org.builder().id(5L).parentId(3L).sign(2).build());
        nodes.add(Org.builder().id(6L).parentId(4L).sign(2).build());
        nodes.add(Org.builder().id(7L).parentId(6L).sign(1).build());
        nodes.add(Org.builder().id(8L).parentId(7L).sign(1).build());
        nodes.add(Org.builder().id(9L).parentId(8L).sign(1).build());
        nodes.add(Org.builder().id(10L).parentId(9L).sign(2).build());
        nodes.add(Org.builder().id(11L).parentId(10L).sign(2).build());

        Set<NodeSignParameter<Long>> signParameters = new HashSet<>();
        signParameters.add(NodeSignParameter.<Long>builder().id(1L).sign(1).build());

        TreeTraverseContext<Org, Long> treeTraversalContext = TreeTraverseContext.<Org, Long>builder()
                .nodes(nodes)
                .signParameters(signParameters)
                .overwrite(true)
                .transmission(true)
                .needCompress(true)
                .signChildConsumer(signChildConsumer)
                .signParentConsumer(signParentConsumer)
                .compressChildConsumer(compressChildConsumer)
                .transmissionChildConsumer(transmissionChildConsumer)
                .build();
        treeTraversalContext.cascadeSign();

        assertEquals(1, nodes.stream().filter(n -> n.id.equals(1L)).findFirst().get().sign);
        assertEquals(0, nodes.stream().filter(n -> n.id.equals(2L)).findFirst().get().sign);
        assertEquals(0, nodes.stream().filter(n -> n.id.equals(3L)).findFirst().get().sign);
        assertEquals(0, nodes.stream().filter(n -> n.id.equals(4L)).findFirst().get().sign);
        assertEquals(0, nodes.stream().filter(n -> n.id.equals(5L)).findFirst().get().sign);
        assertEquals(0, nodes.stream().filter(n -> n.id.equals(6L)).findFirst().get().sign);
        assertEquals(0, nodes.stream().filter(n -> n.id.equals(7L)).findFirst().get().sign);
        assertEquals(0, nodes.stream().filter(n -> n.id.equals(8L)).findFirst().get().sign);
        assertEquals(0, nodes.stream().filter(n -> n.id.equals(9L)).findFirst().get().sign);
        assertEquals(0, nodes.stream().filter(n -> n.id.equals(10L)).findFirst().get().sign);
        assertEquals(0, nodes.stream().filter(n -> n.id.equals(11L)).findFirst().get().sign);
    }

    @Test
    void testBuildTreeAndMarkNodes03() {
        List<Org> nodes = new ArrayList<>();
        nodes.add(Org.builder().id(1L).parentId(null).sign(1).build());
        nodes.add(Org.builder().id(2L).parentId(1L).sign(1).build());
        nodes.add(Org.builder().id(3L).parentId(2L).sign(1).build());
        nodes.add(Org.builder().id(4L).parentId(2L).sign(1).build());
        nodes.add(Org.builder().id(5L).parentId(3L).sign(2).build());
        nodes.add(Org.builder().id(6L).parentId(4L).sign(2).build());
        nodes.add(Org.builder().id(7L).parentId(6L).sign(1).build());
        nodes.add(Org.builder().id(8L).parentId(7L).sign(1).build());
        nodes.add(Org.builder().id(9L).parentId(8L).sign(1).build());
        nodes.add(Org.builder().id(10L).parentId(9L).sign(2).build());
        nodes.add(Org.builder().id(11L).parentId(10L).sign(2).build());

        Set<NodeSignParameter<Long>> signParameters = new HashSet<>();
        signParameters.add(NodeSignParameter.<Long>builder().id(1L).sign(1).build());

        TreeTraverseContext<Org, Long> treeTraversalContext = TreeTraverseContext.<Org, Long>builder()
                .nodes(nodes)
                .signParameters(signParameters)
                .overwrite(false)
                .transmission(true)
                .needCompress(true)
                .signChildConsumer(signChildConsumer)
                .signParentConsumer(signParentConsumer)
                .compressChildConsumer(compressChildConsumer)
                .transmissionChildConsumer(transmissionChildConsumer)
                .build();
        treeTraversalContext.cascadeSign();

        assertEquals(1, nodes.stream().filter(n -> n.id.equals(1L)).findFirst().get().sign);
        assertEquals(0, nodes.stream().filter(n -> n.id.equals(2L)).findFirst().get().sign);
        assertEquals(0, nodes.stream().filter(n -> n.id.equals(3L)).findFirst().get().sign);
        assertEquals(0, nodes.stream().filter(n -> n.id.equals(4L)).findFirst().get().sign);
        assertEquals(2, nodes.stream().filter(n -> n.id.equals(5L)).findFirst().get().sign);
        assertEquals(2, nodes.stream().filter(n -> n.id.equals(6L)).findFirst().get().sign);
        assertEquals(0, nodes.stream().filter(n -> n.id.equals(7L)).findFirst().get().sign);
        assertEquals(0, nodes.stream().filter(n -> n.id.equals(8L)).findFirst().get().sign);
        assertEquals(0, nodes.stream().filter(n -> n.id.equals(9L)).findFirst().get().sign);
        assertEquals(2, nodes.stream().filter(n -> n.id.equals(10L)).findFirst().get().sign);
        assertEquals(2, nodes.stream().filter(n -> n.id.equals(11L)).findFirst().get().sign);
    }

    @Test
    void testBuildTreeAndMarkNodes04() {
        List<Org> nodes = new ArrayList<>();
        nodes.add(Org.builder().id(1L).parentId(null).sign(1).build());
        nodes.add(Org.builder().id(2L).parentId(1L).sign(1).build());
        nodes.add(Org.builder().id(3L).parentId(2L).sign(1).build());
        nodes.add(Org.builder().id(4L).parentId(2L).sign(1).build());
        nodes.add(Org.builder().id(5L).parentId(3L).sign(1).build());
        nodes.add(Org.builder().id(6L).parentId(4L).sign(1).build());
        nodes.add(Org.builder().id(7L).parentId(6L).sign(1).build());
        nodes.add(Org.builder().id(8L).parentId(7L).sign(1).build());
        nodes.add(Org.builder().id(9L).parentId(8L).sign(1).build());
        nodes.add(Org.builder().id(10L).parentId(9L).sign(1).build());
        nodes.add(Org.builder().id(11L).parentId(10L).sign(1).build());

        Set<NodeSignParameter<Long>> signParameters = new HashSet<>();
        signParameters.add(NodeSignParameter.<Long>builder().id(1L).sign(1).build());

        TreeTraverseContext<Org, Long> treeTraversalContext = TreeTraverseContext.<Org, Long>builder()
                .nodes(nodes)
                .signParameters(signParameters)
                .overwrite(false)
                .transmission(true)
                .needCompress(true)
                .signChildConsumer(signChildConsumer)
                .signParentConsumer(signParentConsumer)
                .compressChildConsumer(compressChildConsumer)
                .transmissionChildConsumer(transmissionChildConsumer)
                .build();
        treeTraversalContext.cascadeSign();

        assertEquals(1, nodes.stream().filter(n -> n.id.equals(1L)).findFirst().get().sign);
        assertEquals(0, nodes.stream().filter(n -> n.id.equals(2L)).findFirst().get().sign);
        assertEquals(0, nodes.stream().filter(n -> n.id.equals(3L)).findFirst().get().sign);
        assertEquals(0, nodes.stream().filter(n -> n.id.equals(4L)).findFirst().get().sign);
        assertEquals(0, nodes.stream().filter(n -> n.id.equals(5L)).findFirst().get().sign);
        assertEquals(0, nodes.stream().filter(n -> n.id.equals(6L)).findFirst().get().sign);
        assertEquals(0, nodes.stream().filter(n -> n.id.equals(7L)).findFirst().get().sign);
        assertEquals(0, nodes.stream().filter(n -> n.id.equals(8L)).findFirst().get().sign);
        assertEquals(0, nodes.stream().filter(n -> n.id.equals(9L)).findFirst().get().sign);
        assertEquals(0, nodes.stream().filter(n -> n.id.equals(10L)).findFirst().get().sign);
        assertEquals(0, nodes.stream().filter(n -> n.id.equals(11L)).findFirst().get().sign);
    }

    @Test
    void testBuildTreeAndMarkNodes05() {
        List<Org> nodes = new ArrayList<>();
        nodes.add(Org.builder().id(1L).parentId(null).sign(0).build());
        nodes.add(Org.builder().id(2L).parentId(1L).sign(0).build());
        nodes.add(Org.builder().id(3L).parentId(2L).sign(0).build());
        nodes.add(Org.builder().id(4L).parentId(2L).sign(0).build());
        nodes.add(Org.builder().id(5L).parentId(3L).sign(0).build());
        nodes.add(Org.builder().id(6L).parentId(4L).sign(0).build());
        nodes.add(Org.builder().id(7L).parentId(6L).sign(0).build());
        nodes.add(Org.builder().id(8L).parentId(7L).sign(0).build());
        nodes.add(Org.builder().id(9L).parentId(8L).sign(0).build());
        nodes.add(Org.builder().id(10L).parentId(9L).sign(0).build());
        nodes.add(Org.builder().id(11L).parentId(10L).sign(0).build());

        Set<NodeSignParameter<Long>> signParameters = new HashSet<>();
        signParameters.add(NodeSignParameter.<Long>builder().id(1L).sign(2).build());

        TreeTraverseContext<Org, Long> treeTraversalContext = TreeTraverseContext.<Org, Long>builder()
                .nodes(nodes)
                .signParameters(signParameters)
                .overwrite(false)
                .transmission(true)
                .needCompress(false)
                .signChildConsumer(signChildConsumer)
                .signParentConsumer(signParentConsumer)
                .compressChildConsumer(compressChildConsumer)
                .transmissionChildConsumer(transmissionChildConsumer)
                .build();
        treeTraversalContext.cascadeSign();

        assertEquals(2, nodes.stream().filter(n -> n.id.equals(1L)).findFirst().get().sign);
        assertEquals(1, nodes.stream().filter(n -> n.id.equals(2L)).findFirst().get().sign);
        assertEquals(1, nodes.stream().filter(n -> n.id.equals(3L)).findFirst().get().sign);
        assertEquals(1, nodes.stream().filter(n -> n.id.equals(4L)).findFirst().get().sign);
        assertEquals(1, nodes.stream().filter(n -> n.id.equals(5L)).findFirst().get().sign);
        assertEquals(1, nodes.stream().filter(n -> n.id.equals(6L)).findFirst().get().sign);
        assertEquals(1, nodes.stream().filter(n -> n.id.equals(7L)).findFirst().get().sign);
        assertEquals(1, nodes.stream().filter(n -> n.id.equals(8L)).findFirst().get().sign);
        assertEquals(1, nodes.stream().filter(n -> n.id.equals(9L)).findFirst().get().sign);
        assertEquals(1, nodes.stream().filter(n -> n.id.equals(10L)).findFirst().get().sign);
        assertEquals(1, nodes.stream().filter(n -> n.id.equals(11L)).findFirst().get().sign);
    }

    @Test
    void testBuildTreeAndMarkNodes06() {
        List<Org> nodes = new ArrayList<>();
        nodes.add(Org.builder().id(1L).parentId(null).sign(0).build());
        nodes.add(Org.builder().id(2L).parentId(1L).sign(0).build());
        nodes.add(Org.builder().id(3L).parentId(2L).sign(0).build());
        nodes.add(Org.builder().id(4L).parentId(2L).sign(0).build());
        nodes.add(Org.builder().id(5L).parentId(3L).sign(0).build());
        nodes.add(Org.builder().id(6L).parentId(4L).sign(0).build());
        nodes.add(Org.builder().id(7L).parentId(6L).sign(0).build());
        nodes.add(Org.builder().id(8L).parentId(7L).sign(0).build());
        nodes.add(Org.builder().id(9L).parentId(8L).sign(0).build());
        nodes.add(Org.builder().id(10L).parentId(9L).sign(0).build());
        nodes.add(Org.builder().id(11L).parentId(10L).sign(0).build());

        Set<NodeSignParameter<Long>> signParameters = new HashSet<>();
        signParameters.add(NodeSignParameter.<Long>builder().id(3L).sign(2).build());
        signParameters.add(NodeSignParameter.<Long>builder().id(4L).sign(2).build());

        TreeTraverseContext<Org, Long> treeTraversalContext = TreeTraverseContext.<Org, Long>builder()
                .nodes(nodes)
                .signParameters(signParameters)
                .overwrite(false)
                .transmission(true)
                .needCompress(false)
                .signChildConsumer(signChildConsumer)
                .signParentConsumer(signParentConsumer)
                .compressChildConsumer(compressChildConsumer)
                .transmissionChildConsumer(transmissionChildConsumer)
                .build();
        treeTraversalContext.cascadeSign();

        assertEquals(1, nodes.stream().filter(n -> n.id.equals(1L)).findFirst().get().sign);
        assertEquals(1, nodes.stream().filter(n -> n.id.equals(2L)).findFirst().get().sign);
        assertEquals(2, nodes.stream().filter(n -> n.id.equals(3L)).findFirst().get().sign);
        assertEquals(2, nodes.stream().filter(n -> n.id.equals(4L)).findFirst().get().sign);
        assertEquals(1, nodes.stream().filter(n -> n.id.equals(5L)).findFirst().get().sign);
        assertEquals(1, nodes.stream().filter(n -> n.id.equals(6L)).findFirst().get().sign);
        assertEquals(1, nodes.stream().filter(n -> n.id.equals(7L)).findFirst().get().sign);
        assertEquals(1, nodes.stream().filter(n -> n.id.equals(8L)).findFirst().get().sign);
        assertEquals(1, nodes.stream().filter(n -> n.id.equals(9L)).findFirst().get().sign);
        assertEquals(1, nodes.stream().filter(n -> n.id.equals(10L)).findFirst().get().sign);
        assertEquals(1, nodes.stream().filter(n -> n.id.equals(11L)).findFirst().get().sign);
    }

    @Test
    void testBuildTreeAndMarkNodes07() {
        List<Org> nodes = new ArrayList<>();
        nodes.add(Org.builder().id(1L).parentId(null).sign(2).build());
        nodes.add(Org.builder().id(2L).parentId(1L).sign(0).build());
        nodes.add(Org.builder().id(3L).parentId(2L).sign(0).build());
        nodes.add(Org.builder().id(4L).parentId(2L).sign(0).build());
        nodes.add(Org.builder().id(5L).parentId(3L).sign(0).build());
        nodes.add(Org.builder().id(6L).parentId(4L).sign(0).build());
        nodes.add(Org.builder().id(7L).parentId(6L).sign(0).build());
        nodes.add(Org.builder().id(8L).parentId(7L).sign(0).build());
        nodes.add(Org.builder().id(9L).parentId(8L).sign(0).build());
        nodes.add(Org.builder().id(10L).parentId(9L).sign(0).build());
        nodes.add(Org.builder().id(11L).parentId(10L).sign(0).build());

        Set<NodeSignParameter<Long>> signParameters = new HashSet<>();
        signParameters.add(NodeSignParameter.<Long>builder().id(3L).sign(2).build());
        signParameters.add(NodeSignParameter.<Long>builder().id(4L).sign(2).build());

        TreeTraverseContext<Org, Long> treeTraversalContext = TreeTraverseContext.<Org, Long>builder()
                .nodes(nodes)
                .signParameters(signParameters)
                .overwrite(false)
                .transmission(true)
                .needCompress(false)
                .signChildConsumer(signChildConsumer)
                .signParentConsumer(signParentConsumer)
                .compressChildConsumer(compressChildConsumer)
                .transmissionChildConsumer(transmissionChildConsumer)
                .build();
        treeTraversalContext.cascadeSign();

        assertEquals(2, nodes.stream().filter(n -> n.id.equals(1L)).findFirst().get().sign);
        assertEquals(1, nodes.stream().filter(n -> n.id.equals(2L)).findFirst().get().sign);
        assertEquals(2, nodes.stream().filter(n -> n.id.equals(3L)).findFirst().get().sign);
        assertEquals(2, nodes.stream().filter(n -> n.id.equals(4L)).findFirst().get().sign);
        assertEquals(1, nodes.stream().filter(n -> n.id.equals(5L)).findFirst().get().sign);
        assertEquals(1, nodes.stream().filter(n -> n.id.equals(6L)).findFirst().get().sign);
        assertEquals(1, nodes.stream().filter(n -> n.id.equals(7L)).findFirst().get().sign);
        assertEquals(1, nodes.stream().filter(n -> n.id.equals(8L)).findFirst().get().sign);
        assertEquals(1, nodes.stream().filter(n -> n.id.equals(9L)).findFirst().get().sign);
        assertEquals(1, nodes.stream().filter(n -> n.id.equals(10L)).findFirst().get().sign);
        assertEquals(1, nodes.stream().filter(n -> n.id.equals(11L)).findFirst().get().sign);
    }

    @Test
    void testBuildTreeAndMarkNodes08() {
        List<Org> nodes = new ArrayList<>();
        nodes.add(Org.builder().id(1L).parentId(null).sign(2).build());
        nodes.add(Org.builder().id(2L).parentId(1L).sign(1).build());
        nodes.add(Org.builder().id(3L).parentId(2L).sign(1).build());
        nodes.add(Org.builder().id(4L).parentId(2L).sign(1).build());
        nodes.add(Org.builder().id(5L).parentId(3L).sign(1).build());
        nodes.add(Org.builder().id(6L).parentId(4L).sign(1).build());
        nodes.add(Org.builder().id(7L).parentId(6L).sign(1).build());
        nodes.add(Org.builder().id(8L).parentId(7L).sign(1).build());
        nodes.add(Org.builder().id(9L).parentId(8L).sign(1).build());
        nodes.add(Org.builder().id(10L).parentId(9L).sign(1).build());
        nodes.add(Org.builder().id(11L).parentId(10L).sign(1).build());

        Set<NodeSignParameter<Long>> signParameters = new HashSet<>();
        signParameters.add(NodeSignParameter.<Long>builder().id(3L).sign(2).build());
        signParameters.add(NodeSignParameter.<Long>builder().id(4L).sign(2).build());

        TreeTraverseContext<Org, Long> treeTraversalContext = TreeTraverseContext.<Org, Long>builder()
                .nodes(nodes)
                .signParameters(signParameters)
                .overwrite(true)
                .transmission(true)
                .needCompress(false)
                .signChildConsumer(signChildConsumer)
                .signParentConsumer(signParentConsumer)
                .compressChildConsumer(compressChildConsumer)
                .transmissionChildConsumer(transmissionChildConsumer)
                .build();
        treeTraversalContext.cascadeSign();

        assertEquals(1, nodes.stream().filter(n -> n.id.equals(1L)).findFirst().get().sign);
        assertEquals(1, nodes.stream().filter(n -> n.id.equals(2L)).findFirst().get().sign);
        assertEquals(2, nodes.stream().filter(n -> n.id.equals(3L)).findFirst().get().sign);
        assertEquals(2, nodes.stream().filter(n -> n.id.equals(4L)).findFirst().get().sign);
        assertEquals(1, nodes.stream().filter(n -> n.id.equals(5L)).findFirst().get().sign);
        assertEquals(1, nodes.stream().filter(n -> n.id.equals(6L)).findFirst().get().sign);
        assertEquals(1, nodes.stream().filter(n -> n.id.equals(7L)).findFirst().get().sign);
        assertEquals(1, nodes.stream().filter(n -> n.id.equals(8L)).findFirst().get().sign);
        assertEquals(1, nodes.stream().filter(n -> n.id.equals(9L)).findFirst().get().sign);
        assertEquals(1, nodes.stream().filter(n -> n.id.equals(10L)).findFirst().get().sign);
        assertEquals(1, nodes.stream().filter(n -> n.id.equals(11L)).findFirst().get().sign);
    }
}