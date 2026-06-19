package io.geewit.utils.core.uuid;

import org.junit.jupiter.api.Test;

import java.lang.module.ModuleDescriptor;
import java.lang.module.ModuleFinder;
import java.nio.file.Path;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertTrue;

class UuidModuleDescriptorTest {

    @Test
    void moduleDescriptorExportsPublicUuidApiPackages() {
        ModuleDescriptor descriptor = ModuleFinder.of(Path.of("build", "classes", "java", "main"))
                .find("io.geewit.utils.core.uuid")
                .orElseThrow()
                .descriptor();

        Set<String> exportedPackages = descriptor.exports()
                .stream()
                .map(ModuleDescriptor.Exports::source)
                .collect(Collectors.toSet());

        Set<String> expectedPackages = Set.of(
                "io.geewit.utils.core.uuid",
                "io.geewit.utils.core.uuid.codec",
                "io.geewit.utils.core.uuid.codec.base",
                "io.geewit.utils.core.uuid.codec.base.function",
                "io.geewit.utils.core.uuid.enums",
                "io.geewit.utils.core.uuid.exception",
                "io.geewit.utils.core.uuid.factory",
                "io.geewit.utils.core.uuid.factory.function",
                "io.geewit.utils.core.uuid.factory.function.impl",
                "io.geewit.utils.core.uuid.factory.standard",
                "io.geewit.utils.core.uuid.util",
                "io.geewit.utils.core.uuid.util.immutable"
        );

        Set<String> missingPackages = new TreeSet<>(expectedPackages);
        missingPackages.removeAll(exportedPackages);

        assertTrue(missingPackages.isEmpty(), () -> "Missing UUID module exports: " + missingPackages);
    }
}
