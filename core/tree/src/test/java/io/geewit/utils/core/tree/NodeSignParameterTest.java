package io.geewit.utils.core.tree;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class NodeSignParameterTest {

    @Test
    void builder_defaultTransmissionDownIsTrue() {
        NodeSignParameter<Long> param = NodeSignParameter.<Long>builder()
                .id(1L)
                .sign(1)
                .build();
        assertEquals(1L, param.getId());
        assertEquals(1, param.getSign());
        assertTrue(param.getTransmissionDown());
    }

    @Test
    void builder_defaultTransmissionUpIsTrue() {
        NodeSignParameter<Long> param = NodeSignParameter.<Long>builder()
                .id(1L)
                .sign(1)
                .build();
        assertTrue(param.getTransmissionUp());
    }

    @Test
    void builder_customTransmissionValues() {
        NodeSignParameter<Long> param = NodeSignParameter.<Long>builder()
                .id(1L)
                .sign(1)
                .transmissionDown(false)
                .transmissionUp(false)
                .build();
        assertFalse(param.getTransmissionDown());
        assertFalse(param.getTransmissionUp());
    }

    @Test
    void builder_transmission_setsBothDirection() {
        NodeSignParameter<Long> param = NodeSignParameter.<Long>builder()
                .id(1L)
                .sign(1)
                .transmission(true)
                .build();
        assertTrue(param.getTransmissionDown());
        assertTrue(param.getTransmissionUp());
    }

    @Test
    void builder_transmission_nullDoesNotSet() {
        NodeSignParameter<Long> param = NodeSignParameter.<Long>builder()
                .id(1L)
                .sign(1)
                .transmissionDown(false)
                .transmissionUp(true)
                .transmission(null)
                .build();
        // transmission(null) should not change already set values
        assertFalse(param.getTransmissionDown());
        assertTrue(param.getTransmissionUp());
    }

    @Test
    void builder_transmission_falseSetsBothDirection() {
        NodeSignParameter<Long> param = NodeSignParameter.<Long>builder()
                .id(1L)
                .sign(1)
                .transmission(false)
                .build();
        assertFalse(param.getTransmissionDown());
        assertFalse(param.getTransmissionUp());
    }

    @Test
    void equals_sameId() {
        NodeSignParameter<Long> p1 = NodeSignParameter.<Long>builder().id(1L).sign(1).build();
        NodeSignParameter<Long> p2 = NodeSignParameter.<Long>builder().id(1L).sign(2).build();
        assertEquals(p1, p2);
    }

    @Test
    void equals_differentId() {
        NodeSignParameter<Long> p1 = NodeSignParameter.<Long>builder().id(1L).sign(1).build();
        NodeSignParameter<Long> p2 = NodeSignParameter.<Long>builder().id(2L).sign(1).build();
        assertNotEquals(p1, p2);
    }

    @Test
    void equals_sameObject() {
        NodeSignParameter<Long> p = NodeSignParameter.<Long>builder().id(1L).sign(1).build();
        assertEquals(p, p);
    }

    @Test
    void equals_differentType() {
        NodeSignParameter<Long> p = NodeSignParameter.<Long>builder().id(1L).sign(1).build();
        assertNotEquals(p, "string");
    }

    @Test
    void hashCode_sameId() {
        NodeSignParameter<Long> p1 = NodeSignParameter.<Long>builder().id(1L).sign(1).build();
        NodeSignParameter<Long> p2 = NodeSignParameter.<Long>builder().id(1L).sign(2).build();
        assertEquals(p1.hashCode(), p2.hashCode());
    }

    @Test
    void hashCode_differentId() {
        NodeSignParameter<Long> p1 = NodeSignParameter.<Long>builder().id(1L).sign(1).build();
        NodeSignParameter<Long> p2 = NodeSignParameter.<Long>builder().id(2L).sign(1).build();
        assertNotEquals(p1.hashCode(), p2.hashCode());
    }

    @Test
    void toBuilder_preservesValues() {
        NodeSignParameter<Long> p1 = NodeSignParameter.<Long>builder()
                .id(1L)
                .sign(5)
                .transmissionDown(false)
                .transmissionUp(false)
                .build();
        NodeSignParameter<Long> p2 = p1.toBuilder().build();
        assertEquals(p1.getId(), p2.getId());
        assertEquals(p1.getSign(), p2.getSign());
        assertEquals(p1.getTransmissionDown(), p2.getTransmissionDown());
        assertEquals(p1.getTransmissionUp(), p2.getTransmissionUp());
    }

    @Test
    void builder_withAllSetters() {
        NodeSignParameter<Long> param = NodeSignParameter.<Long>builder()
                .id(10L)
                .sign(20)
                .transmissionDown(false)
                .transmissionUp(true)
                .build();
        assertEquals(10L, param.getId());
        assertEquals(20, param.getSign());
        assertFalse(param.getTransmissionDown());
        assertTrue(param.getTransmissionUp());
    }
}
