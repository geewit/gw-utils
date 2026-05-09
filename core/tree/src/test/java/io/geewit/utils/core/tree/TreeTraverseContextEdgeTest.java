package io.geewit.utils.core.tree;

import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class TreeTraverseContextEdgeTest {

    private static final SignChildConsumer<Org, Long> signChildConsumer = (parentNode, childNode, childNodeSign) -> {
        childNode.setSign(childNodeSign.getSign());
    };

    private static final SignParentConsumer<Org, Long> signParentConsumer = (parentNode, allChildrenSign, overwrite) -> {
    };

    private static final CompressChildConsumer<Org, Long> compressChildConsumer = (parentNode, childNode) -> {
    };

    private static final TransmissionChildConsumer<Org, Long> transmissionChildConsumer = (parentNode, childNode) -> {
    };

    @Test
    void cascadeSign_emptyNodes() {
        TreeTraverseContext<Org, Long> ctx = TreeTraverseContext.<Org, Long>builder()
                .nodes(Collections.emptyList())
                .signChildConsumer(signChildConsumer)
                .signParentConsumer(signParentConsumer)
                .compressChildConsumer(compressChildConsumer)
                .transmissionChildConsumer(transmissionChildConsumer)
                .build();
        assertDoesNotThrow(ctx::cascadeSign);
    }

    @Test
    void cascadeSign_nullNodes() {
        TreeTraverseContext<Org, Long> ctx = TreeTraverseContext.<Org, Long>builder()
                .nodes(null)
                .signChildConsumer(signChildConsumer)
                .signParentConsumer(signParentConsumer)
                .compressChildConsumer(compressChildConsumer)
                .transmissionChildConsumer(transmissionChildConsumer)
                .build();
        assertDoesNotThrow(ctx::cascadeSign);
    }

    @Test
    void cascadeSign_emptySignParameters_withOverwrite() {
        List<Org> nodes = new ArrayList<>();
        Org org = new Org();
        org.setId(1L);
        org.setSign(1);
        nodes.add(org);

        TreeTraverseContext<Org, Long> ctx = TreeTraverseContext.<Org, Long>builder()
                .nodes(nodes)
                .overwrite(true)
                .signChildConsumer(signChildConsumer)
                .signParentConsumer(signParentConsumer)
                .compressChildConsumer(compressChildConsumer)
                .transmissionChildConsumer(transmissionChildConsumer)
                .build();
        ctx.cascadeSign();
        assertEquals(0, org.getSign());
    }

    @Test
    void clear_clearsAll() {
        List<Org> nodes = new ArrayList<>();
        Org org = new Org();
        org.setId(1L);
        nodes.add(org);

        TreeTraverseContext<Org, Long> ctx = TreeTraverseContext.<Org, Long>builder()
                .nodes(nodes)
                .rootId(1L)
                .signParameters(new HashSet<>())
                .signChildConsumer(signChildConsumer)
                .signParentConsumer(signParentConsumer)
                .compressChildConsumer(compressChildConsumer)
                .transmissionChildConsumer(transmissionChildConsumer)
                .build();
        ctx.clear();
        assertNull(ctx.getNodes());
        assertNull(ctx.getRootId());
        assertNull(ctx.getSignParameters());
    }

    @Test
    void cascadeSign_withTransmissionDown() {
        List<Org> nodes = new ArrayList<>();
        Org org1 = new Org();
        org1.setId(1L);
        org1.setSign(0);
        nodes.add(org1);
        Org org2 = new Org();
        org2.setId(2L);
        org2.setParentId(1L);
        org2.setSign(0);
        nodes.add(org2);

        Set<NodeSignParameter<Long>> signParams = new HashSet<>();
        signParams.add(NodeSignParameter.<Long>builder().id(1L).sign(0).transmissionDown(true).build());

        TreeTraverseContext<Org, Long> ctx = TreeTraverseContext.<Org, Long>builder()
                .nodes(nodes)
                .signParameters(signParams)
                .transmission(true)
                .signChildConsumer(signChildConsumer)
                .signParentConsumer(signParentConsumer)
                .compressChildConsumer(compressChildConsumer)
                .transmissionChildConsumer(transmissionChildConsumer)
                .build();
        ctx.cascadeSign();
        // Should not throw
    }
}
