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

    private static final SignNodeConsumer<Org, Long> signNodeConsumer = (node, simpleNodeSign, compress) -> {
        Integer sign = simpleNodeSign.getSign();
        node.setSign(sign);
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
        if (parentNode.sign != null && parentNode.sign > 0 && childNode.sign <= 1) {
            childNode.setSign(1);
        }
    };

    @Test
    void testBuildTreeAndMarkNodes() {
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

        Set<SimpleNodeSign<Long>> signs = new HashSet<>();
        signs.add(SimpleNodeSign.<Long>builder().id(2L).sign(1).build());
        signs.add(SimpleNodeSign.<Long>builder().id(6L).sign(2).build());
        signs.add(SimpleNodeSign.<Long>builder().id(8L).sign(2).build());

        TreeTraverseContext<Org, Long> treeTraversalContext = TreeTraverseContext.<Org, Long>builder()
                .nodes(nodes)
                .signs(signs)
                .overwrite(true)
                .transmission(true)
                .needCompress(true)
                .signNodeConsumer(signNodeConsumer)
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

}