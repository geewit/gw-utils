package io.geewit.core.jackson.databind.serializer;

import org.junit.jupiter.api.Test;

public class LongSerializerTest {

    @Test
    public void maxLength() {
        System.out.println("maxInteger: " + Integer.MAX_VALUE);
        System.out.println("maxLength: " + (int)Math.log10(Integer.MAX_VALUE));
    }
}
