package io.geewit.utils.uuid;

import org.junit.Test;

public class UUIDTest {

    @Test
    public void randomUUID() {
        // uuid
        String uuid = UUID.randomUUID().toString();
        System.out.println("uuid：" + uuid);
    }

    @Test
    public void fromUUID() {
        UUID uuid = UUID.fromString("9084a02bf5b84af9928a4661c2ca7a0d");
        System.out.println(uuid.toString());
    }
}
