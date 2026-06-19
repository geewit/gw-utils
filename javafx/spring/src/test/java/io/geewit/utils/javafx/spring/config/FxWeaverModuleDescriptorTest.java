package io.geewit.utils.javafx.spring.config;

import org.junit.jupiter.api.Test;

import java.lang.module.ModuleDescriptor;
import java.lang.module.ModuleFinder;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;

class FxWeaverModuleDescriptorTest {

    @Test
    void moduleDescriptorExportsPublicSpringFxApiPackages() {
        ModuleDescriptor descriptor = ModuleFinder.of(Path.of("build", "classes", "java", "main"))
                .find("io.geewit.utils.javafx.spring")
                .orElseThrow()
                .descriptor();

        assertThat(descriptor.exports())
                .extracting(ModuleDescriptor.Exports::source)
                .contains(
                        "io.geewit.utils.javafx.spring",
                        "io.geewit.utils.javafx.spring.config",
                        "io.geewit.utils.javafx.spring.constants",
                        "io.geewit.utils.javafx.spring.weaver"
                );
    }
}
