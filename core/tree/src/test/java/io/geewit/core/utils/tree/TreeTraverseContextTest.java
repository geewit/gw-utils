package io.geewit.core.utils.tree;

import java.util.*;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class TreeTraverseContextTest {

    private static final SignChildConsumer<Org, Long> signChildConsumer = (parentNode, childNode, childNodeSign) -> {
        Integer sign = childNodeSign.getSign();
        Boolean transmissionDown = childNodeSign.getTransmissionDown();
        if (parentNode == null) {
            childNode.setSign(sign);
            return;
        }
        if (parentNode.sign > 0) {
            if (sign > 1) {
                childNode.setSign(sign);
            } else {
                if (transmissionDown != null && transmissionDown) {
                    childNode.setSign(sign);
                } else {
                    childNode.setSign(1);
                }
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

    /**
     *  ORG_01                               ☑ 1     0     1     1
     *     |-ORG_02                          ☑ 1     1     1     0
     *        |-ORG_03                       ☑ 1     0     1     0
     *           |-ORG_05                    ☑ 2     0     2     2
     *        |-ORG_04                       ☑ 1     0     1     0
     *           |-ORG_06                    ☑ 2     2     2     2
     *              |-ORG_07                 ☑ 1     0     1     0
     *                 |-ORG_08              ☑ 1     2     2     2
     *                    |-ORG_09           ☑ 1     0     1     0
     *                       |-ORG_10        ☑ 2     0     1     0
     *                          |-ORG_11     ☑ 2     0     1     0
     */
    @Test
    void testBuildTreeAndMarkNodes01() {
        List<Org> nodes = new ArrayList<>();
        Org org1 = new Org();
        org1.setId(1L);
        org1.setSign(1);
        nodes.add(org1);
        Org org2 = new Org();
        org2.setId(2L);
        org2.setParentId(1L);
        org2.setSign(1);
        nodes.add(org2);
        Org org3 = new Org();
        org3.setId(3L);
        org3.setParentId(2L);
        org3.setSign(1);
        nodes.add(org3);
        Org org4 = new Org();
        org4.setId(4L);
        org4.setParentId(2L);
        org4.setSign(1);
        nodes.add(org4);
        Org org5 = new Org();
        org5.setId(5L);
        org5.setParentId(3L);
        org5.setSign(2);
        nodes.add(org5);
        Org org6 = new Org();
        org6.setId(6L);
        org6.setParentId(4L);
        org6.setSign(2);
        nodes.add(org6);
        Org org7 = new Org();
        org7.setId(7L);
        org7.setParentId(6L);
        org7.setSign(1);
        nodes.add(org7);
        Org org8 = new Org();
        org8.setId(8L);
        org8.setParentId(7L);
        org8.setSign(1);
        nodes.add(org8);
        Org org9 = new Org();
        org9.setId(9L);
        org9.setParentId(8L);
        org9.setSign(1);
        nodes.add(org9);
        Org org10 = new Org();
        org10.setId(10L);
        org10.setParentId(9L);
        org10.setSign(2);
        nodes.add(org10);
        Org org11 = new Org();
        org11.setId(11L);
        org11.setParentId(10L);
        org11.setSign(2);
        nodes.add(org11);

        Set<NodeSignParameter<Long>> signParameters = new HashSet<>();
        signParameters.add(NodeSignParameter.<Long>builder().id(2L).sign(1).build());
        signParameters.add(NodeSignParameter.<Long>builder().id(6L).sign(2).build());
        signParameters.add(NodeSignParameter.<Long>builder().id(8L).sign(2).build());

        TreeTraverseContext<Org, Long> treeTraversalContext = TreeTraverseContext.<Org, Long>builder()
                .nodes(nodes)
                .signParameters(signParameters)
                .overwrite(true)
                .transmission(true)
                .compress(true)
                .signChildConsumer(signChildConsumer)
                .signParentConsumer(signParentConsumer)
                .compressChildConsumer(compressChildConsumer)
                .transmissionChildConsumer(transmissionChildConsumer)
                .build();
        treeTraversalContext.cascadeSign();

        assertEquals(1, nodes.stream().filter(n -> n.getId().equals(1L)).findFirst().get().getSign());
        assertEquals(0, nodes.stream().filter(n -> n.getId().equals(2L)).findFirst().get().getSign());
        assertEquals(0, nodes.stream().filter(n -> n.getId().equals(3L)).findFirst().get().getSign());
        assertEquals(0, nodes.stream().filter(n -> n.getId().equals(4L)).findFirst().get().getSign());
        assertEquals(0, nodes.stream().filter(n -> n.getId().equals(5L)).findFirst().get().getSign());
        assertEquals(2, nodes.stream().filter(n -> n.getId().equals(6L)).findFirst().get().getSign());
        assertEquals(0, nodes.stream().filter(n -> n.getId().equals(7L)).findFirst().get().getSign());
        assertEquals(2, nodes.stream().filter(n -> n.getId().equals(8L)).findFirst().get().getSign());
        assertEquals(0, nodes.stream().filter(n -> n.getId().equals(9L)).findFirst().get().getSign());
        assertEquals(0, nodes.stream().filter(n -> n.getId().equals(10L)).findFirst().get().getSign());
        assertEquals(0, nodes.stream().filter(n -> n.getId().equals(11L)).findFirst().get().getSign());
    }

    @Test
    void testBuildTreeAndMarkNodes02() {
        List<Org> nodes = new ArrayList<>();
        Org org1 = new Org();
        org1.setId(1L);
        org1.setSign(1);
        nodes.add(org1);
        Org org2 = new Org();
        org2.setId(2L);
        org2.setParentId(1L);
        org2.setSign(1);
        nodes.add(org2);
        Org org3 = new Org();
        org3.setId(3L);
        org3.setParentId(2L);
        org3.setSign(1);
        nodes.add(org3);
        Org org4 = new Org();
        org4.setId(4L);
        org4.setParentId(2L);
        org4.setSign(1);
        nodes.add(org4);
        Org org5 = new Org();
        org5.setId(5L);
        org5.setParentId(3L);
        org5.setSign(2);
        nodes.add(org5);
        Org org6 = new Org();
        org6.setId(6L);
        org6.setParentId(4L);
        org6.setSign(2);
        nodes.add(org6);
        Org org7 = new Org();
        org7.setId(7L);
        org7.setParentId(6L);
        org7.setSign(1);
        nodes.add(org7);
        Org org8 = new Org();
        org8.setId(8L);
        org8.setParentId(7L);
        org8.setSign(1);
        nodes.add(org8);
        Org org9 = new Org();
        org9.setId(9L);
        org9.setParentId(8L);
        org9.setSign(1);
        nodes.add(org9);
        Org org10 = new Org();
        org10.setId(10L);
        org10.setParentId(9L);
        org10.setSign(2);
        nodes.add(org10);
        Org org11 = new Org();
        org11.setId(11L);
        org11.setParentId(10L);
        org11.setSign(2);
        nodes.add(org11);

        Set<NodeSignParameter<Long>> signParameters = new HashSet<>();
        signParameters.add(NodeSignParameter.<Long>builder().id(1L).sign(1).build());

        TreeTraverseContext<Org, Long> treeTraversalContext = TreeTraverseContext.<Org, Long>builder()
                .nodes(nodes)
                .signParameters(signParameters)
                .overwrite(true)
                .transmission(true)
                .compress(true)
                .signChildConsumer(signChildConsumer)
                .signParentConsumer(signParentConsumer)
                .compressChildConsumer(compressChildConsumer)
                .transmissionChildConsumer(transmissionChildConsumer)
                .build();
        treeTraversalContext.cascadeSign();

        assertEquals(1, nodes.stream().filter(n -> n.getId().equals(1L)).findFirst().get().getSign());
        assertEquals(0, nodes.stream().filter(n -> n.getId().equals(2L)).findFirst().get().getSign());
        assertEquals(0, nodes.stream().filter(n -> n.getId().equals(3L)).findFirst().get().getSign());
        assertEquals(0, nodes.stream().filter(n -> n.getId().equals(4L)).findFirst().get().getSign());
        assertEquals(0, nodes.stream().filter(n -> n.getId().equals(5L)).findFirst().get().getSign());
        assertEquals(0, nodes.stream().filter(n -> n.getId().equals(6L)).findFirst().get().getSign());
        assertEquals(0, nodes.stream().filter(n -> n.getId().equals(7L)).findFirst().get().getSign());
        assertEquals(0, nodes.stream().filter(n -> n.getId().equals(8L)).findFirst().get().getSign());
        assertEquals(0, nodes.stream().filter(n -> n.getId().equals(9L)).findFirst().get().getSign());
        assertEquals(0, nodes.stream().filter(n -> n.getId().equals(10L)).findFirst().get().getSign());
        assertEquals(0, nodes.stream().filter(n -> n.getId().equals(11L)).findFirst().get().getSign());
    }

    @Test
    void testBuildTreeAndMarkNodes03() {
        List<Org> nodes = new ArrayList<>();
        Org org1 = new Org();
        org1.setId(1L);
        org1.setSign(1);
        nodes.add(org1);
        Org org2 = new Org();
        org2.setId(2L);
        org2.setParentId(1L);
        org2.setSign(1);
        nodes.add(org2);
        Org org3 = new Org();
        org3.setId(3L);
        org3.setParentId(2L);
        org3.setSign(1);
        nodes.add(org3);
        Org org4 = new Org();
        org4.setId(4L);
        org4.setParentId(2L);
        org4.setSign(1);
        nodes.add(org4);
        Org org5 = new Org();
        org5.setId(5L);
        org5.setParentId(3L);
        org5.setSign(2);
        nodes.add(org5);
        Org org6 = new Org();
        org6.setId(6L);
        org6.setParentId(4L);
        org6.setSign(2);
        nodes.add(org6);
        Org org7 = new Org();
        org7.setId(7L);
        org7.setParentId(6L);
        org7.setSign(1);
        nodes.add(org7);
        Org org8 = new Org();
        org8.setId(8L);
        org8.setParentId(7L);
        org8.setSign(1);
        nodes.add(org8);
        Org org9 = new Org();
        org9.setId(9L);
        org9.setParentId(8L);
        org9.setSign(1);
        nodes.add(org9);
        Org org10 = new Org();
        org10.setId(10L);
        org10.setParentId(9L);
        org10.setSign(2);
        nodes.add(org10);
        Org org11 = new Org();
        org11.setId(11L);
        org11.setParentId(10L);
        org11.setSign(2);
        nodes.add(org11);

        Set<NodeSignParameter<Long>> signParameters = new HashSet<>();
        signParameters.add(NodeSignParameter.<Long>builder().id(1L).sign(1).build());

        TreeTraverseContext<Org, Long> treeTraversalContext = TreeTraverseContext.<Org, Long>builder()
                .nodes(nodes)
                .signParameters(signParameters)
                .overwrite(false)
                .transmission(true)
                .compress(true)
                .signChildConsumer(signChildConsumer)
                .signParentConsumer(signParentConsumer)
                .compressChildConsumer(compressChildConsumer)
                .transmissionChildConsumer(transmissionChildConsumer)
                .build();
        treeTraversalContext.cascadeSign();

        assertEquals(1, nodes.stream().filter(n -> n.getId().equals(1L)).findFirst().get().getSign());
        assertEquals(0, nodes.stream().filter(n -> n.getId().equals(2L)).findFirst().get().getSign());
        assertEquals(0, nodes.stream().filter(n -> n.getId().equals(3L)).findFirst().get().getSign());
        assertEquals(0, nodes.stream().filter(n -> n.getId().equals(4L)).findFirst().get().getSign());
        assertEquals(2, nodes.stream().filter(n -> n.getId().equals(5L)).findFirst().get().getSign());
        assertEquals(2, nodes.stream().filter(n -> n.getId().equals(6L)).findFirst().get().getSign());
        assertEquals(0, nodes.stream().filter(n -> n.getId().equals(7L)).findFirst().get().getSign());
        assertEquals(0, nodes.stream().filter(n -> n.getId().equals(8L)).findFirst().get().getSign());
        assertEquals(0, nodes.stream().filter(n -> n.getId().equals(9L)).findFirst().get().getSign());
        assertEquals(2, nodes.stream().filter(n -> n.getId().equals(10L)).findFirst().get().getSign());
        assertEquals(2, nodes.stream().filter(n -> n.getId().equals(11L)).findFirst().get().getSign());
    }

    @Test
    void testBuildTreeAndMarkNodes04() {
        List<Org> nodes = new ArrayList<>();
        Org org1 = new Org();
        org1.setId(1L);
        org1.setSign(1);
        nodes.add(org1);
        Org org2 = new Org();
        org2.setId(2L);
        org2.setParentId(1L);
        org2.setSign(1);
        nodes.add(org2);
        Org org3 = new Org();
        org3.setId(3L);
        org3.setParentId(2L);
        org3.setSign(1);
        nodes.add(org3);
        Org org4 = new Org();
        org4.setId(4L);
        org4.setParentId(2L);
        org4.setSign(1);
        nodes.add(org4);
        Org org5 = new Org();
        org5.setId(5L);
        org5.setParentId(3L);
        org5.setSign(1);
        nodes.add(org5);
        Org org6 = new Org();
        org6.setId(6L);
        org6.setParentId(4L);
        org6.setSign(1);
        nodes.add(org6);
        Org org7 = new Org();
        org7.setId(7L);
        org7.setParentId(6L);
        org7.setSign(1);
        nodes.add(org7);
        Org org8 = new Org();
        org8.setId(8L);
        org8.setParentId(7L);
        org8.setSign(1);
        nodes.add(org8);
        Org org9 = new Org();
        org9.setId(9L);
        org9.setParentId(8L);
        org9.setSign(1);
        nodes.add(org9);
        Org org10 = new Org();
        org10.setId(10L);
        org10.setParentId(9L);
        org10.setSign(1);
        nodes.add(org10);
        Org org11 = new Org();
        org11.setId(11L);
        org11.setParentId(10L);
        org11.setSign(1);
        nodes.add(org11);

        Set<NodeSignParameter<Long>> signParameters = new HashSet<>();
        signParameters.add(NodeSignParameter.<Long>builder().id(1L).sign(1).build());

        TreeTraverseContext<Org, Long> treeTraversalContext = TreeTraverseContext.<Org, Long>builder()
                .nodes(nodes)
                .signParameters(signParameters)
                .overwrite(false)
                .transmission(true)
                .compress(true)
                .signChildConsumer(signChildConsumer)
                .signParentConsumer(signParentConsumer)
                .compressChildConsumer(compressChildConsumer)
                .transmissionChildConsumer(transmissionChildConsumer)
                .build();
        treeTraversalContext.cascadeSign();

        assertEquals(1, nodes.stream().filter(n -> n.getId().equals(1L)).findFirst().get().getSign());
        assertEquals(0, nodes.stream().filter(n -> n.getId().equals(2L)).findFirst().get().getSign());
        assertEquals(0, nodes.stream().filter(n -> n.getId().equals(3L)).findFirst().get().getSign());
        assertEquals(0, nodes.stream().filter(n -> n.getId().equals(4L)).findFirst().get().getSign());
        assertEquals(0, nodes.stream().filter(n -> n.getId().equals(5L)).findFirst().get().getSign());
        assertEquals(0, nodes.stream().filter(n -> n.getId().equals(6L)).findFirst().get().getSign());
        assertEquals(0, nodes.stream().filter(n -> n.getId().equals(7L)).findFirst().get().getSign());
        assertEquals(0, nodes.stream().filter(n -> n.getId().equals(8L)).findFirst().get().getSign());
        assertEquals(0, nodes.stream().filter(n -> n.getId().equals(9L)).findFirst().get().getSign());
        assertEquals(0, nodes.stream().filter(n -> n.getId().equals(10L)).findFirst().get().getSign());
        assertEquals(0, nodes.stream().filter(n -> n.getId().equals(11L)).findFirst().get().getSign());
    }

    @Test
    void testBuildTreeAndMarkNodes05() {
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
        Org org3 = new Org();
        org3.setId(3L);
        org3.setParentId(2L);
        org3.setSign(0);
        nodes.add(org3);
        Org org4 = new Org();
        org4.setId(4L);
        org4.setParentId(2L);
        org4.setSign(0);
        nodes.add(org4);
        Org org5 = new Org();
        org5.setId(5L);
        org5.setParentId(3L);
        org5.setSign(0);
        nodes.add(org5);
        Org org6 = new Org();
        org6.setId(6L);
        org6.setParentId(4L);
        org6.setSign(0);
        nodes.add(org6);
        Org org7 = new Org();
        org7.setId(7L);
        org7.setParentId(6L);
        org7.setSign(0);
        nodes.add(org7);
        Org org8 = new Org();
        org8.setId(8L);
        org8.setParentId(7L);
        org8.setSign(0);
        nodes.add(org8);
        Org org9 = new Org();
        org9.setId(9L);
        org9.setParentId(8L);
        org9.setSign(0);
        nodes.add(org9);
        Org org10 = new Org();
        org10.setId(10L);
        org10.setParentId(9L);
        org10.setSign(0);
        nodes.add(org10);
        Org org11 = new Org();
        org11.setId(11L);
        org11.setParentId(10L);
        org11.setSign(0);
        nodes.add(org11);

        Set<NodeSignParameter<Long>> signParameters = new HashSet<>();
        signParameters.add(NodeSignParameter.<Long>builder().id(1L).sign(2).build());

        TreeTraverseContext<Org, Long> treeTraversalContext = TreeTraverseContext.<Org, Long>builder()
                .nodes(nodes)
                .signParameters(signParameters)
                .overwrite(false)
                .transmission(true)
                .compress(false)
                .signChildConsumer(signChildConsumer)
                .signParentConsumer(signParentConsumer)
                .compressChildConsumer(compressChildConsumer)
                .transmissionChildConsumer(transmissionChildConsumer)
                .build();
        treeTraversalContext.cascadeSign();

        assertEquals(2, nodes.stream().filter(n -> n.getId().equals(1L)).findFirst().get().getSign());
        assertEquals(1, nodes.stream().filter(n -> n.getId().equals(2L)).findFirst().get().getSign());
        assertEquals(1, nodes.stream().filter(n -> n.getId().equals(3L)).findFirst().get().getSign());
        assertEquals(1, nodes.stream().filter(n -> n.getId().equals(4L)).findFirst().get().getSign());
        assertEquals(1, nodes.stream().filter(n -> n.getId().equals(5L)).findFirst().get().getSign());
        assertEquals(1, nodes.stream().filter(n -> n.getId().equals(6L)).findFirst().get().getSign());
        assertEquals(1, nodes.stream().filter(n -> n.getId().equals(7L)).findFirst().get().getSign());
        assertEquals(1, nodes.stream().filter(n -> n.getId().equals(8L)).findFirst().get().getSign());
        assertEquals(1, nodes.stream().filter(n -> n.getId().equals(9L)).findFirst().get().getSign());
        assertEquals(1, nodes.stream().filter(n -> n.getId().equals(10L)).findFirst().get().getSign());
        assertEquals(1, nodes.stream().filter(n -> n.getId().equals(11L)).findFirst().get().getSign());
    }

    @Test
    void testBuildTreeAndMarkNodes06() {
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
        Org org3 = new Org();
        org3.setId(3L);
        org3.setParentId(2L);
        org3.setSign(0);
        nodes.add(org3);
        Org org4 = new Org();
        org4.setId(4L);
        org4.setParentId(2L);
        org4.setSign(0);
        nodes.add(org4);
        Org org5 = new Org();
        org5.setId(5L);
        org5.setParentId(3L);
        org5.setSign(0);
        nodes.add(org5);
        Org org6 = new Org();
        org6.setId(6L);
        org6.setParentId(4L);
        org6.setSign(0);
        nodes.add(org6);
        Org org7 = new Org();
        org7.setId(7L);
        org7.setParentId(6L);
        org7.setSign(0);
        nodes.add(org7);
        Org org8 = new Org();
        org8.setId(8L);
        org8.setParentId(7L);
        org8.setSign(0);
        nodes.add(org8);
        Org org9 = new Org();
        org9.setId(9L);
        org9.setParentId(8L);
        org9.setSign(0);
        nodes.add(org9);
        Org org10 = new Org();
        org10.setId(10L);
        org10.setParentId(9L);
        org10.setSign(0);
        nodes.add(org10);
        Org org11 = new Org();
        org11.setId(11L);
        org11.setParentId(10L);
        org11.setSign(0);
        nodes.add(org11);


        Set<NodeSignParameter<Long>> signParameters = new HashSet<>();
        signParameters.add(NodeSignParameter.<Long>builder().id(3L).sign(2).build());
        signParameters.add(NodeSignParameter.<Long>builder().id(4L).sign(2).build());

        TreeTraverseContext<Org, Long> treeTraversalContext = TreeTraverseContext.<Org, Long>builder()
                .nodes(nodes)
                .signParameters(signParameters)
                .overwrite(false)
                .transmission(true)
                .compress(false)
                .signChildConsumer(signChildConsumer)
                .signParentConsumer(signParentConsumer)
                .compressChildConsumer(compressChildConsumer)
                .transmissionChildConsumer(transmissionChildConsumer)
                .build();
        treeTraversalContext.cascadeSign();

        assertEquals(1, nodes.stream().filter(n -> n.getId().equals(1L)).findFirst().get().getSign());
        assertEquals(1, nodes.stream().filter(n -> n.getId().equals(2L)).findFirst().get().getSign());
        assertEquals(2, nodes.stream().filter(n -> n.getId().equals(3L)).findFirst().get().getSign());
        assertEquals(2, nodes.stream().filter(n -> n.getId().equals(4L)).findFirst().get().getSign());
        assertEquals(1, nodes.stream().filter(n -> n.getId().equals(5L)).findFirst().get().getSign());
        assertEquals(1, nodes.stream().filter(n -> n.getId().equals(6L)).findFirst().get().getSign());
        assertEquals(1, nodes.stream().filter(n -> n.getId().equals(7L)).findFirst().get().getSign());
        assertEquals(1, nodes.stream().filter(n -> n.getId().equals(8L)).findFirst().get().getSign());
        assertEquals(1, nodes.stream().filter(n -> n.getId().equals(9L)).findFirst().get().getSign());
        assertEquals(1, nodes.stream().filter(n -> n.getId().equals(10L)).findFirst().get().getSign());
        assertEquals(1, nodes.stream().filter(n -> n.getId().equals(11L)).findFirst().get().getSign());
    }

    @Test
    void testBuildTreeAndMarkNodes07() {
        List<Org> nodes = new ArrayList<>();
        Org org1 = new Org();
        org1.setId(1L);
        org1.setSign(2);
        nodes.add(org1);
        Org org2 = new Org();
        org2.setId(2L);
        org2.setParentId(1L);
        org2.setSign(0);
        nodes.add(org2);
        Org org3 = new Org();
        org3.setId(3L);
        org3.setParentId(2L);
        org3.setSign(0);
        nodes.add(org3);
        Org org4 = new Org();
        org4.setId(4L);
        org4.setParentId(2L);
        org4.setSign(0);
        nodes.add(org4);
        Org org5 = new Org();
        org5.setId(5L);
        org5.setParentId(3L);
        org5.setSign(0);
        nodes.add(org5);
        Org org6 = new Org();
        org6.setId(6L);
        org6.setParentId(4L);
        org6.setSign(0);
        nodes.add(org6);
        Org org7 = new Org();
        org7.setId(7L);
        org7.setParentId(6L);
        org7.setSign(0);
        nodes.add(org7);
        Org org8 = new Org();
        org8.setId(8L);
        org8.setParentId(7L);
        org8.setSign(0);
        nodes.add(org8);
        Org org9 = new Org();
        org9.setId(9L);
        org9.setParentId(8L);
        org9.setSign(0);
        nodes.add(org9);
        Org org10 = new Org();
        org10.setId(10L);
        org10.setParentId(9L);
        org10.setSign(0);
        nodes.add(org10);
        Org org11 = new Org();
        org11.setId(11L);
        org11.setParentId(10L);
        org11.setSign(0);
        nodes.add(org11);

        Set<NodeSignParameter<Long>> signParameters = new HashSet<>();
        signParameters.add(NodeSignParameter.<Long>builder().id(3L).sign(2).build());
        signParameters.add(NodeSignParameter.<Long>builder().id(4L).sign(2).build());

        TreeTraverseContext<Org, Long> treeTraversalContext = TreeTraverseContext.<Org, Long>builder()
                .nodes(nodes)
                .signParameters(signParameters)
                .overwrite(false)
                .transmission(true)
                .compress(false)
                .signChildConsumer(signChildConsumer)
                .signParentConsumer(signParentConsumer)
                .compressChildConsumer(compressChildConsumer)
                .transmissionChildConsumer(transmissionChildConsumer)
                .build();
        treeTraversalContext.cascadeSign();

        assertEquals(2, nodes.stream().filter(n -> n.getId().equals(1L)).findFirst().get().getSign());
        assertEquals(1, nodes.stream().filter(n -> n.getId().equals(2L)).findFirst().get().getSign());
        assertEquals(2, nodes.stream().filter(n -> n.getId().equals(3L)).findFirst().get().getSign());
        assertEquals(2, nodes.stream().filter(n -> n.getId().equals(4L)).findFirst().get().getSign());
        assertEquals(1, nodes.stream().filter(n -> n.getId().equals(5L)).findFirst().get().getSign());
        assertEquals(1, nodes.stream().filter(n -> n.getId().equals(6L)).findFirst().get().getSign());
        assertEquals(1, nodes.stream().filter(n -> n.getId().equals(7L)).findFirst().get().getSign());
        assertEquals(1, nodes.stream().filter(n -> n.getId().equals(8L)).findFirst().get().getSign());
        assertEquals(1, nodes.stream().filter(n -> n.getId().equals(9L)).findFirst().get().getSign());
        assertEquals(1, nodes.stream().filter(n -> n.getId().equals(10L)).findFirst().get().getSign());
        assertEquals(1, nodes.stream().filter(n -> n.getId().equals(11L)).findFirst().get().getSign());
    }

    /**
     *  ORG_01                               ☑ 2
     *     |-ORG_02                          ☑ 1
     *        |-ORG_03                       ☑ 1  2
     *           |-ORG_05                    ☑ 1
     *        |-ORG_04                       ☑ 1  2
     *           |-ORG_06                    ☑ 1
     *              |-ORG_07                 ☑ 1
     *                 |-ORG_08              ☑ 1
     *                    |-ORG_09           ☑ 1
     *                       |-ORG_09        ☑ 1
     *                          |-ORG_10     ☑ 1
     */
    @Test
    void testBuildTreeAndMarkNodes08() {
        List<Org> nodes = new ArrayList<>();
        Org org1 = new Org();
        org1.setId(1L);
        org1.setSign(2);
        nodes.add(org1);
        Org org2 = new Org();
        org2.setId(2L);
        org2.setParentId(1L);
        org2.setSign(1);
        nodes.add(org2);
        Org org3 = new Org();
        org3.setId(3L);
        org3.setParentId(2L);
        org3.setSign(1);
        nodes.add(org3);
        Org org4 = new Org();
        org4.setId(4L);
        org4.setParentId(2L);
        org4.setSign(1);
        nodes.add(org4);
        Org org5 = new Org();
        org5.setId(5L);
        org5.setParentId(3L);
        org5.setSign(1);
        nodes.add(org5);
        Org org6 = new Org();
        org6.setId(6L);
        org6.setParentId(4L);
        org6.setSign(1);
        nodes.add(org6);
        Org org7 = new Org();
        org7.setId(7L);
        org7.setParentId(6L);
        org7.setSign(1);
        nodes.add(org7);
        Org org8 = new Org();
        org8.setId(8L);
        org8.setParentId(7L);
        org8.setSign(1);
        nodes.add(org8);
        Org org9 = new Org();
        org9.setId(9L);
        org9.setParentId(8L);
        org9.setSign(1);
        nodes.add(org9);
        Org org10 = new Org();
        org10.setId(10L);
        org10.setParentId(9L);
        org10.setSign(1);
        nodes.add(org10);
        Org org11 = new Org();
        org11.setId(11L);
        org11.setParentId(10L);
        org11.setSign(1);
        nodes.add(org11);

        Set<NodeSignParameter<Long>> signParameters = new HashSet<>();
        signParameters.add(NodeSignParameter.<Long>builder().id(3L).sign(2).build());
        signParameters.add(NodeSignParameter.<Long>builder().id(4L).sign(2).build());

        TreeTraverseContext<Org, Long> treeTraversalContext = TreeTraverseContext.<Org, Long>builder()
                .nodes(nodes)
                .signParameters(signParameters)
                .overwrite(true)
                .transmission(true)
                .compress(false)
                .signChildConsumer(signChildConsumer)
                .signParentConsumer(signParentConsumer)
                .compressChildConsumer(compressChildConsumer)
                .transmissionChildConsumer(transmissionChildConsumer)
                .build();
        treeTraversalContext.cascadeSign();

        assertEquals(1, nodes.stream().filter(n -> n.getId().equals(1L)).findFirst().get().getSign());
        assertEquals(1, nodes.stream().filter(n -> n.getId().equals(2L)).findFirst().get().getSign());
        assertEquals(2, nodes.stream().filter(n -> n.getId().equals(3L)).findFirst().get().getSign());
        assertEquals(2, nodes.stream().filter(n -> n.getId().equals(4L)).findFirst().get().getSign());
        assertEquals(1, nodes.stream().filter(n -> n.getId().equals(5L)).findFirst().get().getSign());
        assertEquals(1, nodes.stream().filter(n -> n.getId().equals(6L)).findFirst().get().getSign());
        assertEquals(1, nodes.stream().filter(n -> n.getId().equals(7L)).findFirst().get().getSign());
        assertEquals(1, nodes.stream().filter(n -> n.getId().equals(8L)).findFirst().get().getSign());
        assertEquals(1, nodes.stream().filter(n -> n.getId().equals(9L)).findFirst().get().getSign());
        assertEquals(1, nodes.stream().filter(n -> n.getId().equals(10L)).findFirst().get().getSign());
        assertEquals(1, nodes.stream().filter(n -> n.getId().equals(11L)).findFirst().get().getSign());
    }

    /**
     *  ORG_01                               ☑ 2     0
     *     |-ORG_02                          ☑ 1     0
     *        |-ORG_03                       ☑ 1     0
     *           |-ORG_05                    ☑ 1     0
     *        |-ORG_04                       ☑ 1     2
     *           |-ORG_06                    ☑ 1     1
     *              |-ORG_07                 ☑ 1     1
     *                 |-ORG_08              ☑ 1     1
     *                    |-ORG_09           ☑ 1     1
     *                       |-ORG_09        ☑ 1     1
     *                          |-ORG_10     ☑ 1     1
     */
    @Test
    void testBuildTreeAndMarkNodes09() {
        List<Org> nodes = new ArrayList<>();
        Org org1 = new Org();
        org1.setId(1L);
        org1.setSign(2);
        nodes.add(org1);
        Org org2 = new Org();
        org2.setId(2L);
        org2.setParentId(1L);
        org2.setSign(1);
        nodes.add(org2);
        Org org3 = new Org();
        org3.setId(3L);
        org3.setParentId(2L);
        org3.setSign(1);
        nodes.add(org3);
        Org org4 = new Org();
        org4.setId(4L);
        org4.setParentId(2L);
        org4.setSign(1);
        nodes.add(org4);
        Org org5 = new Org();
        org5.setId(5L);
        org5.setParentId(3L);
        org5.setSign(1);
        nodes.add(org5);
        Org org6 = new Org();
        org6.setId(6L);
        org6.setParentId(4L);
        org6.setSign(1);
        nodes.add(org6);
        Org org7 = new Org();
        org7.setId(7L);
        org7.setParentId(6L);
        org7.setSign(1);
        nodes.add(org7);
        Org org8 = new Org();
        org8.setId(8L);
        org8.setParentId(7L);
        org8.setSign(1);
        nodes.add(org8);
        Org org9 = new Org();
        org9.setId(9L);
        org9.setParentId(8L);
        org9.setSign(1);
        nodes.add(org9);
        Org org10 = new Org();
        org10.setId(10L);
        org10.setParentId(9L);
        org10.setSign(1);
        nodes.add(org10);
        Org org11 = new Org();
        org11.setId(11L);
        org11.setParentId(10L);
        org11.setSign(1);
        nodes.add(org11);

        Set<NodeSignParameter<Long>> signParameters = new HashSet<>();
        signParameters.add(NodeSignParameter.<Long>builder().id(4L).sign(2).build());

        TreeTraverseContext<Org, Long> treeTraversalContext = TreeTraverseContext.<Org, Long>builder()
                .nodes(nodes)
                .signParameters(signParameters)
                .overwrite(true)
                .transmission(true)
                .compress(false)
                .signChildConsumer(signChildConsumer)
                .signParentConsumer(signParentConsumer)
                .compressChildConsumer(compressChildConsumer)
                .transmissionChildConsumer(transmissionChildConsumer)
                .build();
        treeTraversalContext.cascadeSign();

        assertEquals(0, nodes.stream().filter(n -> n.getId().equals(1L)).findFirst().get().getSign());
        assertEquals(0, nodes.stream().filter(n -> n.getId().equals(2L)).findFirst().get().getSign());
        assertEquals(0, nodes.stream().filter(n -> n.getId().equals(3L)).findFirst().get().getSign());
        assertEquals(2, nodes.stream().filter(n -> n.getId().equals(4L)).findFirst().get().getSign());
        assertEquals(0, nodes.stream().filter(n -> n.getId().equals(5L)).findFirst().get().getSign());
        assertEquals(1, nodes.stream().filter(n -> n.getId().equals(6L)).findFirst().get().getSign());
        assertEquals(1, nodes.stream().filter(n -> n.getId().equals(7L)).findFirst().get().getSign());
        assertEquals(1, nodes.stream().filter(n -> n.getId().equals(8L)).findFirst().get().getSign());
        assertEquals(1, nodes.stream().filter(n -> n.getId().equals(9L)).findFirst().get().getSign());
        assertEquals(1, nodes.stream().filter(n -> n.getId().equals(10L)).findFirst().get().getSign());
        assertEquals(1, nodes.stream().filter(n -> n.getId().equals(11L)).findFirst().get().getSign());
    }

    /**
     *  ORG_01                               ☑ 0     0
     *     |-ORG_02                          ☑ 0     0
     *        |-ORG_03                       ☑ 0     0
     *           |-ORG_05                    ☑ 0     0
     *        |-ORG_04                       ☑ 0     1
     *           |-ORG_06                    ☑ 0     1
     *              |-ORG_07                 ☑ 0     1
     *                 |-ORG_08              ☑ 0     1
     *                    |-ORG_09           ☑ 0     1
     *                       |-ORG_09        ☑ 0     1
     *                          |-ORG_10     ☑ 0     1
     */
    @Test
    void testBuildTreeAndMarkNodes10() {
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
        Org org3 = new Org();
        org3.setId(3L);
        org3.setParentId(2L);
        org3.setSign(0);
        nodes.add(org3);
        Org org4 = new Org();
        org4.setId(4L);
        org4.setParentId(2L);
        org4.setSign(0);
        nodes.add(org4);
        Org org5 = new Org();
        org5.setId(5L);
        org5.setParentId(3L);
        org5.setSign(0);
        nodes.add(org5);
        Org org6 = new Org();
        org6.setId(6L);
        org6.setParentId(4L);
        org6.setSign(0);
        nodes.add(org6);
        Org org7 = new Org();
        org7.setId(7L);
        org7.setParentId(6L);
        org7.setSign(0);
        nodes.add(org7);
        Org org8 = new Org();
        org8.setId(8L);
        org8.setParentId(7L);
        org8.setSign(0);
        nodes.add(org8);
        Org org9 = new Org();
        org9.setId(9L);
        org9.setParentId(8L);
        org9.setSign(0);
        nodes.add(org9);
        Org org10 = new Org();
        org10.setId(10L);
        org10.setParentId(9L);
        org10.setSign(0);
        nodes.add(org10);
        Org org11 = new Org();
        org11.setId(11L);
        org11.setParentId(10L);
        org11.setSign(0);
        nodes.add(org11);

        Set<NodeSignParameter<Long>> signParameters = new HashSet<>();
        signParameters.add(NodeSignParameter.<Long>builder().id(10L).sign(2).build());

        TreeTraverseContext<Org, Long> treeTraversalContext = TreeTraverseContext.<Org, Long>builder()
                .nodes(nodes)
                .signParameters(signParameters)
                .overwrite(true)
                .transmission(true)
                .compress(false)
                .signChildConsumer(signChildConsumer)
                .signParentConsumer(signParentConsumer)
                .compressChildConsumer(compressChildConsumer)
                .transmissionChildConsumer(transmissionChildConsumer)
                .build();
        treeTraversalContext.cascadeSign();

        assertEquals(0, nodes.stream().filter(n -> n.getId().equals(1L)).findFirst().get().getSign());
        assertEquals(0, nodes.stream().filter(n -> n.getId().equals(2L)).findFirst().get().getSign());
        assertEquals(0, nodes.stream().filter(n -> n.getId().equals(3L)).findFirst().get().getSign());
        assertEquals(1, nodes.stream().filter(n -> n.getId().equals(4L)).findFirst().get().getSign());
        assertEquals(0, nodes.stream().filter(n -> n.getId().equals(5L)).findFirst().get().getSign());
        assertEquals(1, nodes.stream().filter(n -> n.getId().equals(6L)).findFirst().get().getSign());
        assertEquals(1, nodes.stream().filter(n -> n.getId().equals(7L)).findFirst().get().getSign());
        assertEquals(1, nodes.stream().filter(n -> n.getId().equals(8L)).findFirst().get().getSign());
        assertEquals(1, nodes.stream().filter(n -> n.getId().equals(9L)).findFirst().get().getSign());
        assertEquals(2, nodes.stream().filter(n -> n.getId().equals(10L)).findFirst().get().getSign());
        assertEquals(1, nodes.stream().filter(n -> n.getId().equals(11L)).findFirst().get().getSign());
    }

    /**
     *  ORG_01                               ☑ 0     1
     *     |-ORG_02                          ☑ 0     1
     *        |-ORG_03                       ☑ 0     1
     *           |-ORG_05                    ☑ 0     1
     *        |-ORG_04                       ☑ 0     1
     *           |-ORG_06                    ☑ 0     1
     *              |-ORG_07                 ☑ 0     1
     *                 |-ORG_08              ☑ 0     1
     *                    |-ORG_09           ☑ 0     1
     *                       |-ORG_09        ☑ 0     1
     *                          |-ORG_10     ☑ 0     2
     *                             |-ORG_11  ☑ 0     1
     */
    @Test
    void testBuildTreeAndMarkNodes11() {
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
        Org org3 = new Org();
        org3.setId(3L);
        org3.setParentId(2L);
        org3.setSign(0);
        nodes.add(org3);
        Org org4 = new Org();
        org4.setId(4L);
        org4.setParentId(2L);
        org4.setSign(0);
        nodes.add(org4);
        Org org5 = new Org();
        org5.setId(5L);
        org5.setParentId(3L);
        org5.setSign(0);
        nodes.add(org5);
        Org org6 = new Org();
        org6.setId(6L);
        org6.setParentId(4L);
        org6.setSign(0);
        nodes.add(org6);
        Org org7 = new Org();
        org7.setId(7L);
        org7.setParentId(6L);
        org7.setSign(0);
        nodes.add(org7);
        Org org8 = new Org();
        org8.setId(8L);
        org8.setParentId(7L);
        org8.setSign(0);
        nodes.add(org8);
        Org org9 = new Org();
        org9.setId(9L);
        org9.setParentId(8L);
        org9.setSign(0);
        nodes.add(org9);
        Org org10 = new Org();
        org10.setId(10L);
        org10.setParentId(9L);
        org10.setSign(0);
        nodes.add(org10);
        Org org11 = new Org();
        org11.setId(11L);
        org11.setParentId(10L);
        org11.setSign(0);
        nodes.add(org11);

        Set<NodeSignParameter<Long>> signParameters = new HashSet<>();
        signParameters.add(NodeSignParameter.<Long>builder().id(5L).sign(1).build());
        signParameters.add(NodeSignParameter.<Long>builder().id(10L).sign(2).build());

        TreeTraverseContext<Org, Long> treeTraversalContext = TreeTraverseContext.<Org, Long>builder()
                .nodes(nodes)
                .signParameters(signParameters)
                .overwrite(true)
                .transmission(true)
                .compress(false)
                .signChildConsumer(signChildConsumer)
                .signParentConsumer(signParentConsumer)
                .compressChildConsumer(compressChildConsumer)
                .transmissionChildConsumer(transmissionChildConsumer)
                .build();
        treeTraversalContext.cascadeSign();

        assertEquals(1, nodes.stream().filter(n -> n.getId().equals(1L)).findFirst().get().getSign());
        assertEquals(1, nodes.stream().filter(n -> n.getId().equals(2L)).findFirst().get().getSign());
        assertEquals(1, nodes.stream().filter(n -> n.getId().equals(3L)).findFirst().get().getSign());
        assertEquals(1, nodes.stream().filter(n -> n.getId().equals(4L)).findFirst().get().getSign());
        assertEquals(1, nodes.stream().filter(n -> n.getId().equals(5L)).findFirst().get().getSign());
        assertEquals(1, nodes.stream().filter(n -> n.getId().equals(6L)).findFirst().get().getSign());
        assertEquals(1, nodes.stream().filter(n -> n.getId().equals(7L)).findFirst().get().getSign());
        assertEquals(1, nodes.stream().filter(n -> n.getId().equals(8L)).findFirst().get().getSign());
        assertEquals(1, nodes.stream().filter(n -> n.getId().equals(9L)).findFirst().get().getSign());
        assertEquals(2, nodes.stream().filter(n -> n.getId().equals(10L)).findFirst().get().getSign());
        assertEquals(1, nodes.stream().filter(n -> n.getId().equals(11L)).findFirst().get().getSign());
    }

    /**
     *  ORG_01                               ☑ 1     0
     *     |-ORG_02                          ☑ 1     0
     *        |-ORG_03                       ☑ 1     0
     *           |-ORG_05                    ☑ 1     0
     *        |-ORG_04                       ☑ 1     0
     *           |-ORG_06                    ☑ 1     0
     *              |-ORG_07                 ☑ 1     0
     *                 |-ORG_08              ☑ 1     0
     *                    |-ORG_09           ☑ 1     0
     *                       |-ORG_09        ☑ 1     0
     *                          |-ORG_10     ☑ 1     0
     */
    @Test
    void testBuildTreeAndMarkNodes12() {
        List<Org> nodes = new ArrayList<>();
        Org org1 = new Org();
        org1.setId(1L);
        org1.setSign(1);
        nodes.add(org1);
        Org org2 = new Org();
        org2.setId(2L);
        org2.setParentId(1L);
        org2.setSign(1);
        nodes.add(org2);
        Org org3 = new Org();
        org3.setId(3L);
        org3.setParentId(2L);
        org3.setSign(1);
        nodes.add(org3);
        Org org4 = new Org();
        org4.setId(4L);
        org4.setParentId(2L);
        org4.setSign(1);
        nodes.add(org4);
        Org org5 = new Org();
        org5.setId(5L);
        org5.setParentId(3L);
        org5.setSign(1);
        nodes.add(org5);
        Org org6 = new Org();
        org6.setId(6L);
        org6.setParentId(4L);
        org6.setSign(1);
        nodes.add(org6);
        Org org7 = new Org();
        org7.setId(7L);
        org7.setParentId(6L);
        org7.setSign(1);
        nodes.add(org7);
        Org org8 = new Org();
        org8.setId(8L);
        org8.setParentId(7L);
        org8.setSign(1);
        nodes.add(org8);
        Org org9 = new Org();
        org9.setId(9L);
        org9.setParentId(8L);
        org9.setSign(1);
        nodes.add(org9);
        Org org10 = new Org();
        org10.setId(10L);
        org10.setParentId(9L);
        org10.setSign(1);
        nodes.add(org10);
        Org org11 = new Org();
        org11.setId(11L);
        org11.setParentId(10L);
        org11.setSign(1);
        nodes.add(org11);

        Set<NodeSignParameter<Long>> signParameters = new HashSet<>();
        signParameters.add(NodeSignParameter.<Long>builder().id(3L).sign(0).build());
        signParameters.add(NodeSignParameter.<Long>builder().id(4L).sign(0).build());

        TreeTraverseContext<Org, Long> treeTraversalContext = TreeTraverseContext.<Org, Long>builder()
                .nodes(nodes)
                .signParameters(signParameters)
                .overwrite(true)
                .transmission(true)
                .compress(false)
                .signChildConsumer(signChildConsumer)
                .signParentConsumer(signParentConsumer)
                .compressChildConsumer(compressChildConsumer)
                .transmissionChildConsumer(transmissionChildConsumer)
                .build();
        treeTraversalContext.cascadeSign();

        assertEquals(0, nodes.stream().filter(n -> n.getId().equals(1L)).findFirst().get().getSign());
        assertEquals(0, nodes.stream().filter(n -> n.getId().equals(2L)).findFirst().get().getSign());
        assertEquals(0, nodes.stream().filter(n -> n.getId().equals(3L)).findFirst().get().getSign());
        assertEquals(0, nodes.stream().filter(n -> n.getId().equals(4L)).findFirst().get().getSign());
        assertEquals(0, nodes.stream().filter(n -> n.getId().equals(5L)).findFirst().get().getSign());
        assertEquals(0, nodes.stream().filter(n -> n.getId().equals(6L)).findFirst().get().getSign());
        assertEquals(0, nodes.stream().filter(n -> n.getId().equals(7L)).findFirst().get().getSign());
        assertEquals(0, nodes.stream().filter(n -> n.getId().equals(8L)).findFirst().get().getSign());
        assertEquals(0, nodes.stream().filter(n -> n.getId().equals(9L)).findFirst().get().getSign());
        assertEquals(0, nodes.stream().filter(n -> n.getId().equals(10L)).findFirst().get().getSign());
        assertEquals(0, nodes.stream().filter(n -> n.getId().equals(11L)).findFirst().get().getSign());
    }

    /**
     *  ORG_01                               ☑ 1     0
     *     |-ORG_02                          ☑ 1     0
     *        |-ORG_03                       ☑ 1     0
     *           |-ORG_05                    ☑ 1     0
     *        |-ORG_04                       ☑ 1     0
     *           |-ORG_06                    ☑ 1     0
     *              |-ORG_07                 ☑ 1     0
     *                 |-ORG_08              ☑ 1     0
     *                    |-ORG_09           ☑ 1     0
     *                       |-ORG_09        ☑ 1     0
     *                          |-ORG_10     ☑ 1     0
     */
    @Test
    void testBuildTreeAndMarkNodes13() {
        List<Org> nodes = new ArrayList<>();
        Org org1 = new Org();
        org1.setId(1L);
        org1.setSign(2);
        nodes.add(org1);
        Org org2 = new Org();
        org2.setId(2L);
        org2.setParentId(1L);
        org2.setSign(1);
        nodes.add(org2);
        Org org3 = new Org();
        org3.setId(3L);
        org3.setParentId(2L);
        org3.setSign(1);
        nodes.add(org3);
        Org org4 = new Org();
        org4.setId(4L);
        org4.setParentId(2L);
        org4.setSign(1);
        nodes.add(org4);
        Org org5 = new Org();
        org5.setId(5L);
        org5.setParentId(3L);
        org5.setSign(1);
        nodes.add(org5);
        Org org6 = new Org();
        org6.setId(6L);
        org6.setParentId(4L);
        org6.setSign(1);
        nodes.add(org6);
        Org org7 = new Org();
        org7.setId(7L);
        org7.setParentId(6L);
        org7.setSign(1);
        nodes.add(org7);
        Org org8 = new Org();
        org8.setId(8L);
        org8.setParentId(7L);
        org8.setSign(1);
        nodes.add(org8);
        Org org9 = new Org();
        org9.setId(9L);
        org9.setParentId(8L);
        org9.setSign(1);
        nodes.add(org9);
        Org org10 = new Org();
        org10.setId(10L);
        org10.setParentId(9L);
        org10.setSign(1);
        nodes.add(org10);
        Org org11 = new Org();
        org11.setId(11L);
        org11.setParentId(10L);
        org11.setSign(1);
        nodes.add(org11);

        Set<NodeSignParameter<Long>> signParameters = new HashSet<>();
        signParameters.add(NodeSignParameter.<Long>builder().id(1L).sign(0).transmissionDown(true).build());

        TreeTraverseContext<Org, Long> treeTraversalContext = TreeTraverseContext.<Org, Long>builder()
                .nodes(nodes)
                .signParameters(signParameters)
                .overwrite(false)
                .transmission(true)
                .compress(false)
                .signChildConsumer(signChildConsumer)
                .signParentConsumer(signParentConsumer)
                .compressChildConsumer(compressChildConsumer)
                .transmissionChildConsumer(transmissionChildConsumer)
                .build();
        treeTraversalContext.cascadeSign();

        assertEquals(0, nodes.stream().filter(n -> n.getId().equals(1L)).findFirst().get().getSign());
        assertEquals(0, nodes.stream().filter(n -> n.getId().equals(2L)).findFirst().get().getSign());
        assertEquals(0, nodes.stream().filter(n -> n.getId().equals(3L)).findFirst().get().getSign());
        assertEquals(0, nodes.stream().filter(n -> n.getId().equals(4L)).findFirst().get().getSign());
        assertEquals(0, nodes.stream().filter(n -> n.getId().equals(5L)).findFirst().get().getSign());
        assertEquals(0, nodes.stream().filter(n -> n.getId().equals(6L)).findFirst().get().getSign());
        assertEquals(0, nodes.stream().filter(n -> n.getId().equals(7L)).findFirst().get().getSign());
        assertEquals(0, nodes.stream().filter(n -> n.getId().equals(8L)).findFirst().get().getSign());
        assertEquals(0, nodes.stream().filter(n -> n.getId().equals(9L)).findFirst().get().getSign());
        assertEquals(0, nodes.stream().filter(n -> n.getId().equals(10L)).findFirst().get().getSign());
        assertEquals(0, nodes.stream().filter(n -> n.getId().equals(11L)).findFirst().get().getSign());
    }

    /**
     *  ORG_01                               ☑ 1     0
     *     |-ORG_02                          ☑ 1     0
     *        |-ORG_03                       ☑ 1     0
     *           |-ORG_05                    ☑ 1     0
     *        |-ORG_04                       ☑ 1     0
     *           |-ORG_06                    ☑ 1     0
     *              |-ORG_07                 ☑ 1     0
     *                 |-ORG_08              ☑ 1     0
     *                    |-ORG_09           ☑ 1     0
     *                       |-ORG_09        ☑ 1     0
     *                          |-ORG_10     ☑ 1     0
     */
    @Test
    void testBuildTreeAndMarkNodes14() {
        List<Org> nodes = new ArrayList<>();
        Org org1 = new Org();
        org1.setId(1L);
        org1.setSign(2);
        nodes.add(org1);
        Org org2 = new Org();
        org2.setId(2L);
        org2.setParentId(1L);
        org2.setSign(1);
        nodes.add(org2);
        Org org3 = new Org();
        org3.setId(3L);
        org3.setParentId(2L);
        org3.setSign(1);
        nodes.add(org3);
        Org org4 = new Org();
        org4.setId(4L);
        org4.setParentId(2L);
        org4.setSign(1);
        nodes.add(org4);
        Org org5 = new Org();
        org5.setId(5L);
        org5.setParentId(3L);
        org5.setSign(1);
        nodes.add(org5);
        Org org6 = new Org();
        org6.setId(6L);
        org6.setParentId(4L);
        org6.setSign(1);
        nodes.add(org6);
        Org org7 = new Org();
        org7.setId(7L);
        org7.setParentId(6L);
        org7.setSign(1);
        nodes.add(org7);
        Org org8 = new Org();
        org8.setId(8L);
        org8.setParentId(7L);
        org8.setSign(1);
        nodes.add(org8);
        Org org9 = new Org();
        org9.setId(9L);
        org9.setParentId(8L);
        org9.setSign(1);
        nodes.add(org9);
        Org org10 = new Org();
        org10.setId(10L);
        org10.setParentId(9L);
        org10.setSign(1);
        nodes.add(org10);
        Org org11 = new Org();
        org11.setId(11L);
        org11.setParentId(10L);
        org11.setSign(1);
        nodes.add(org11);

        Set<NodeSignParameter<Long>> signParameters = new HashSet<>();
        signParameters.add(NodeSignParameter.<Long>builder().id(3L).sign(0).transmissionDown(true).build());

        TreeTraverseContext<Org, Long> treeTraversalContext = TreeTraverseContext.<Org, Long>builder()
                .nodes(nodes)
                .signParameters(signParameters)
                .overwrite(false)
                .transmission(true)
                .compress(false)
                .signChildConsumer(signChildConsumer)
                .signParentConsumer(signParentConsumer)
                .compressChildConsumer(compressChildConsumer)
                .transmissionChildConsumer(transmissionChildConsumer)
                .build();
        treeTraversalContext.cascadeSign();

        assertEquals(0, nodes.stream().filter(n -> n.getId().equals(1L)).findFirst().get().getSign());
        assertEquals(0, nodes.stream().filter(n -> n.getId().equals(2L)).findFirst().get().getSign());
        assertEquals(0, nodes.stream().filter(n -> n.getId().equals(3L)).findFirst().get().getSign());
        assertEquals(1, nodes.stream().filter(n -> n.getId().equals(4L)).findFirst().get().getSign());
        assertEquals(0, nodes.stream().filter(n -> n.getId().equals(5L)).findFirst().get().getSign());
        assertEquals(1, nodes.stream().filter(n -> n.getId().equals(6L)).findFirst().get().getSign());
        assertEquals(1, nodes.stream().filter(n -> n.getId().equals(7L)).findFirst().get().getSign());
        assertEquals(1, nodes.stream().filter(n -> n.getId().equals(8L)).findFirst().get().getSign());
        assertEquals(1, nodes.stream().filter(n -> n.getId().equals(9L)).findFirst().get().getSign());
        assertEquals(1, nodes.stream().filter(n -> n.getId().equals(10L)).findFirst().get().getSign());
        assertEquals(1, nodes.stream().filter(n -> n.getId().equals(11L)).findFirst().get().getSign());
    }

    /**
     *  ORG_01                               ☑ 1     0
     *     |-ORG_02                          ☑ 1     0
     *        |-ORG_03                       ☑ 1     0
     *           |-ORG_05                    ☑ 1     0
     *        |-ORG_04                       ☑ 1     0
     *           |-ORG_06                    ☑ 1     0
     *              |-ORG_07                 ☑ 1     0
     *                 |-ORG_08              ☑ 1     0
     *                    |-ORG_09           ☑ 1     0
     *                       |-ORG_09        ☑ 1     0
     *                          |-ORG_10     ☑ 1     0
     */
    @Test
    void testBuildTreeAndMarkNodes15() {
        List<Org> nodes = new ArrayList<>();
        Org org1 = new Org();
        org1.setId(1L);
        org1.setSign(2);
        nodes.add(org1);
        Org org2 = new Org();
        org2.setId(2L);
        org2.setParentId(1L);
        org2.setSign(1);
        nodes.add(org2);
        Org org3 = new Org();
        org3.setId(3L);
        org3.setParentId(2L);
        org3.setSign(1);
        nodes.add(org3);
        Org org4 = new Org();
        org4.setId(4L);
        org4.setParentId(2L);
        org4.setSign(1);
        nodes.add(org4);
        Org org5 = new Org();
        org5.setId(5L);
        org5.setParentId(3L);
        org5.setSign(1);
        nodes.add(org5);
        Org org6 = new Org();
        org6.setId(6L);
        org6.setParentId(4L);
        org6.setSign(1);
        nodes.add(org6);
        Org org7 = new Org();
        org7.setId(7L);
        org7.setParentId(6L);
        org7.setSign(1);
        nodes.add(org7);
        Org org8 = new Org();
        org8.setId(8L);
        org8.setParentId(7L);
        org8.setSign(1);
        nodes.add(org8);
        Org org9 = new Org();
        org9.setId(9L);
        org9.setParentId(8L);
        org9.setSign(1);
        nodes.add(org9);
        Org org10 = new Org();
        org10.setId(10L);
        org10.setParentId(9L);
        org10.setSign(1);
        nodes.add(org10);
        Org org11 = new Org();
        org11.setId(11L);
        org11.setParentId(10L);
        org11.setSign(1);
        nodes.add(org11);

        Set<NodeSignParameter<Long>> signParameters = new HashSet<>();

        TreeTraverseContext<Org, Long> treeTraversalContext = TreeTraverseContext.<Org, Long>builder()
                .nodes(nodes)
                .signParameters(signParameters)
                .overwrite(true)
                .transmission(true)
                .compress(false)
                .signChildConsumer(signChildConsumer)
                .signParentConsumer(signParentConsumer)
                .compressChildConsumer(compressChildConsumer)
                .transmissionChildConsumer(transmissionChildConsumer)
                .build();
        treeTraversalContext.cascadeSign();

        assertEquals(0, nodes.stream().filter(n -> n.getId().equals(1L)).findFirst().get().getSign());
        assertEquals(0, nodes.stream().filter(n -> n.getId().equals(2L)).findFirst().get().getSign());
        assertEquals(0, nodes.stream().filter(n -> n.getId().equals(3L)).findFirst().get().getSign());
        assertEquals(0, nodes.stream().filter(n -> n.getId().equals(4L)).findFirst().get().getSign());
        assertEquals(0, nodes.stream().filter(n -> n.getId().equals(5L)).findFirst().get().getSign());
        assertEquals(0, nodes.stream().filter(n -> n.getId().equals(6L)).findFirst().get().getSign());
        assertEquals(0, nodes.stream().filter(n -> n.getId().equals(7L)).findFirst().get().getSign());
        assertEquals(0, nodes.stream().filter(n -> n.getId().equals(8L)).findFirst().get().getSign());
        assertEquals(0, nodes.stream().filter(n -> n.getId().equals(9L)).findFirst().get().getSign());
        assertEquals(0, nodes.stream().filter(n -> n.getId().equals(10L)).findFirst().get().getSign());
        assertEquals(0, nodes.stream().filter(n -> n.getId().equals(11L)).findFirst().get().getSign());
    }

    /**
     *  ORG_01                               ☑ 1     0
     *     |-ORG_02                          ☑ 1     0
     *     |-ORG_03                          ☑ 1     0
     */
    @Test
    void testBuildTreeAndMarkNodes16() {
        List<Org> nodes = new ArrayList<>();
        Org org1 = new Org();
        org1.setId(1L);
        org1.setSign(1);
        nodes.add(org1);
        Org org2 = new Org();
        org2.setId(2L);
        org2.setParentId(1L);
        org2.setSign(1);
        nodes.add(org2);
        Org org3 = new Org();
        org3.setId(3L);
        org3.setParentId(1L);
        org3.setSign(1);
        nodes.add(org3);

        Set<NodeSignParameter<Long>> signParameters = new HashSet<>();
        signParameters.add(NodeSignParameter.<Long>builder().id(3L).sign(0).transmission(true).build());

        TreeTraverseContext<Org, Long> treeTraversalContext = TreeTraverseContext.<Org, Long>builder()
                .nodes(nodes)
                .signParameters(signParameters)
                .overwrite(false)
                .transmission(true)
                .compress(false)
                .signChildConsumer(signChildConsumer)
                .signParentConsumer(signParentConsumer)
                .compressChildConsumer(compressChildConsumer)
                .transmissionChildConsumer(transmissionChildConsumer)
                .build();
        treeTraversalContext.cascadeSign();

        assertEquals(0, nodes.stream().filter(n -> n.getId().equals(1L)).findFirst().get().getSign());
        assertEquals(1, nodes.stream().filter(n -> n.getId().equals(2L)).findFirst().get().getSign());
        assertEquals(0, nodes.stream().filter(n -> n.getId().equals(3L)).findFirst().get().getSign());
    }

    /**
     *  ORG_01                               ☑ 1           0
     *     |-ORG_02                          ☑ 1     0
     *     |-ORG_03                          ☑ 1     0     0
     *     |-ORG_04                          ☑ 1
     */
    @Test
    void testBuildTreeAndMarkNodes17() {
        List<Org> nodes = new ArrayList<>();
        Org org1 = new Org();
        org1.setId(1L);
        org1.setSign(1);
        nodes.add(org1);
        Org org2 = new Org();
        org2.setId(2L);
        org2.setParentId(1L);
        org2.setSign(1);
        nodes.add(org2);
        Org org3 = new Org();
        org3.setId(3L);
        org3.setParentId(1L);
        org3.setSign(1);
        nodes.add(org3);
        Org org4 = new Org();
        org4.setId(4L);
        org4.setParentId(1L);
        org4.setSign(1);
        nodes.add(org4);

        Set<NodeSignParameter<Long>> signParameters = new HashSet<>();
        signParameters.add(NodeSignParameter.<Long>builder().id(3L).sign(0).transmission(true).build());

        TreeTraverseContext<Org, Long> treeTraversalContext = TreeTraverseContext.<Org, Long>builder()
                .nodes(nodes)
                .signParameters(signParameters)
                .overwrite(false)
                .transmission(true)
                .compress(false)
                .signChildConsumer(signChildConsumer)
                .signParentConsumer(signParentConsumer)
                .compressChildConsumer(compressChildConsumer)
                .transmissionChildConsumer(transmissionChildConsumer)
                .build();
        treeTraversalContext.cascadeSign();

        assertEquals(0, nodes.stream().filter(n -> n.getId().equals(1L)).findFirst().get().getSign());
        assertEquals(1, nodes.stream().filter(n -> n.getId().equals(2L)).findFirst().get().getSign());
        assertEquals(0, nodes.stream().filter(n -> n.getId().equals(3L)).findFirst().get().getSign());
        assertEquals(1, nodes.stream().filter(n -> n.getId().equals(4L)).findFirst().get().getSign());
    }

    /**
     *  ORG_01                               ☑ 1     0     0
     *     |-ORG_02                          ☑ 1           0
     *     |-ORG_03                          ☑ 1           0
     *     |-ORG_04                          ☑ 1           0
     */
    @Test
    void testBuildTreeAndMarkNodes18() {
        List<Org> nodes = new ArrayList<>();
        Org org1 = new Org();
        org1.setId(1L);
        org1.setSign(1);
        nodes.add(org1);
        Org org2 = new Org();
        org2.setId(2L);
        org2.setParentId(1L);
        org2.setSign(1);
        nodes.add(org2);
        Org org3 = new Org();
        org3.setId(3L);
        org3.setParentId(1L);
        org3.setSign(1);
        nodes.add(org3);
        Org org4 = new Org();
        org4.setId(4L);
        org4.setParentId(1L);
        org4.setSign(1);
        nodes.add(org4);

        Set<NodeSignParameter<Long>> signParameters = new HashSet<>();
        signParameters.add(NodeSignParameter.<Long>builder().id(1L).sign(0).transmission(true).build());

        TreeTraverseContext<Org, Long> treeTraversalContext = TreeTraverseContext.<Org, Long>builder()
                .nodes(nodes)
                .signParameters(signParameters)
                .overwrite(false)
                .transmission(true)
                .compress(false)
                .signChildConsumer(signChildConsumer)
                .signParentConsumer(signParentConsumer)
                .compressChildConsumer(compressChildConsumer)
                .transmissionChildConsumer(transmissionChildConsumer)
                .build();
        treeTraversalContext.cascadeSign();

        assertEquals(0, nodes.stream().filter(n -> n.getId().equals(1L)).findFirst().get().getSign());
        assertEquals(0, nodes.stream().filter(n -> n.getId().equals(2L)).findFirst().get().getSign());
        assertEquals(0, nodes.stream().filter(n -> n.getId().equals(3L)).findFirst().get().getSign());
        assertEquals(0, nodes.stream().filter(n -> n.getId().equals(4L)).findFirst().get().getSign());
    }

    /**
     *  ORG_01                               ☑ 1           0
     *     |-ORG_02                          ☑ 1     0     0
     *     |-ORG_03                          ☑ 1     0     0
     *     |-ORG_04                          ☑ 1     0     0
     */
    @Test
    void testBuildTreeAndMarkNodes19() {
        List<Org> nodes = new ArrayList<>();
        Org org1 = new Org();
        org1.setId(1L);
        org1.setSign(1);
        nodes.add(org1);
        Org org2 = new Org();
        org2.setId(2L);
        org2.setParentId(1L);
        org2.setSign(1);
        nodes.add(org2);
        Org org3 = new Org();
        org3.setId(3L);
        org3.setParentId(1L);
        org3.setSign(1);
        nodes.add(org3);
        Org org4 = new Org();
        org4.setId(4L);
        org4.setParentId(1L);
        org4.setSign(1);
        nodes.add(org4);

        Set<NodeSignParameter<Long>> signParameters = new HashSet<>();
        signParameters.add(NodeSignParameter.<Long>builder().id(2L).sign(0).transmission(true).build());
        signParameters.add(NodeSignParameter.<Long>builder().id(3L).sign(0).transmission(true).build());
        signParameters.add(NodeSignParameter.<Long>builder().id(4L).sign(0).transmission(true).build());

        TreeTraverseContext<Org, Long> treeTraversalContext = TreeTraverseContext.<Org, Long>builder()
                .nodes(nodes)
                .signParameters(signParameters)
                .overwrite(false)
                .transmission(true)
                .compress(false)
                .signChildConsumer(signChildConsumer)
                .signParentConsumer(signParentConsumer)
                .compressChildConsumer(compressChildConsumer)
                .transmissionChildConsumer(transmissionChildConsumer)
                .build();
        treeTraversalContext.cascadeSign();

        assertEquals(0, nodes.stream().filter(n -> n.getId().equals(1L)).findFirst().get().getSign());
        assertEquals(0, nodes.stream().filter(n -> n.getId().equals(2L)).findFirst().get().getSign());
        assertEquals(0, nodes.stream().filter(n -> n.getId().equals(3L)).findFirst().get().getSign());
        assertEquals(0, nodes.stream().filter(n -> n.getId().equals(4L)).findFirst().get().getSign());
    }
}