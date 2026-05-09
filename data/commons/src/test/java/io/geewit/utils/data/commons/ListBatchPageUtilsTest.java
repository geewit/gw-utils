package io.geewit.utils.data.commons;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.*;

class ListBatchPageUtilsTest {

    @Test
    void constructor_canBeInstantiatedViaReflection() throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        Constructor<ListBatchPageUtils> constructor = ListBatchPageUtils.class.getDeclaredConstructor();
        constructor.setAccessible(true);
        ListBatchPageUtils instance = constructor.newInstance();
        assertNotNull(instance);
    }

    @Test
    void pageLoadAndBatchConsumer_normalPaging() {
        List<Integer> list = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
        AtomicInteger callCount = new AtomicInteger(0);

        ListBatchPageUtils.pageLoadAndBatchConsumer(list, 3, batch -> {
            callCount.incrementAndGet();
            assertTrue(batch.size() <= 3);
        });

        assertEquals(4, callCount.get());
    }

    @Test
    void pageLoadAndBatchConsumer_pageSizeEqualsListSize() {
        List<Integer> list = Arrays.asList(1, 2, 3);
        AtomicInteger callCount = new AtomicInteger(0);

        ListBatchPageUtils.pageLoadAndBatchConsumer(list, 3, batch -> callCount.incrementAndGet());

        assertEquals(1, callCount.get());
    }

    @Test
    void pageLoadAndBatchConsumer_pageSizeGreaterThanList() {
        List<Integer> list = Arrays.asList(1, 2);
        AtomicInteger callCount = new AtomicInteger(0);

        ListBatchPageUtils.pageLoadAndBatchConsumer(list, 10, batch -> callCount.incrementAndGet());

        assertEquals(1, callCount.get());
    }

    @Test
    void pageLoadAndBatchConsumer_pageSizeOne() {
        List<Integer> list = Arrays.asList(1, 2, 3);
        AtomicInteger callCount = new AtomicInteger(0);

        ListBatchPageUtils.pageLoadAndBatchConsumer(list, 1, batch -> {
            assertEquals(1, batch.size());
            callCount.incrementAndGet();
        });

        assertEquals(3, callCount.get());
    }

    @Test
    void pageLoadAndBatchConsumer_emptyList() {
        List<Integer> list = new ArrayList<>();
        AtomicInteger callCount = new AtomicInteger(0);

        ListBatchPageUtils.pageLoadAndBatchConsumer(list, 5, batch -> callCount.incrementAndGet());

        assertEquals(0, callCount.get());
    }

    @Test
    void pageLoadAndBatchConsumer_nullListThrowsException() {
        assertThrows(NullPointerException.class, () ->
            ListBatchPageUtils.pageLoadAndBatchConsumer(null, 5, batch -> {})
        );
    }

    @Test
    void pageLoadAndBatchConsumer_nullConsumerThrowsException() {
        List<Integer> list = Arrays.asList(1, 2, 3);
        assertThrows(NullPointerException.class, () ->
            ListBatchPageUtils.pageLoadAndBatchConsumer(list, 5, null)
        );
    }

    @Test
    void pageLoadAndBatchConsumer_zeroPageSizeThrowsException() {
        List<Integer> list = Arrays.asList(1, 2, 3);
        assertThrows(IllegalArgumentException.class, () ->
            ListBatchPageUtils.pageLoadAndBatchConsumer(list, 0, batch -> {})
        );
    }

    @Test
    void pageLoadAndBatchConsumer_negativePageSizeThrowsException() {
        List<Integer> list = Arrays.asList(1, 2, 3);
        assertThrows(IllegalArgumentException.class, () ->
            ListBatchPageUtils.pageLoadAndBatchConsumer(list, -1, batch -> {})
        );
    }

    @Test
    void pageLoadAndBatchConsumer_verifyBatchContent() {
        List<String> list = Arrays.asList("a", "b", "c", "d", "e");
        List<List<String>> batches = new ArrayList<>();

        ListBatchPageUtils.pageLoadAndBatchConsumer(list, 2, batches::add);

        assertEquals(3, batches.size());
        assertEquals(Arrays.asList("a", "b"), batches.get(0));
        assertEquals(Arrays.asList("c", "d"), batches.get(1));
        assertEquals(Arrays.asList("e"), batches.get(2));
    }

    @Test
    void pageLoadAndBatchConsumer_singleElement() {
        List<Integer> list = Arrays.asList(42);
        AtomicInteger callCount = new AtomicInteger(0);

        ListBatchPageUtils.pageLoadAndBatchConsumer(list, 5, batch -> {
            assertEquals(1, batch.size());
            assertEquals(42, batch.get(0));
            callCount.incrementAndGet();
        });

        assertEquals(1, callCount.get());
    }

    @Test
    void pageLoadAndBatchConsumer_largeList() {
        List<Integer> list = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            list.add(i);
        }
        AtomicInteger callCount = new AtomicInteger(0);
        AtomicInteger elementCount = new AtomicInteger(0);

        ListBatchPageUtils.pageLoadAndBatchConsumer(list, 10, batch -> {
            callCount.incrementAndGet();
            elementCount.addAndGet(batch.size());
        });

        assertEquals(10, callCount.get());
        assertEquals(100, elementCount.get());
    }
}