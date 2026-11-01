package io.geewit.gradle.publishing;

import org.gradle.api.DefaultTask;
import org.gradle.api.GradleException;
import org.gradle.api.provider.Property;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.TaskAction;
import org.gradle.work.DisableCachingByDefault;

/** Validates the opt-in and release version before writing Central staging artifacts. */
@DisableCachingByDefault(because = "Validates publication inputs without producing artifacts")
public abstract class VerifyCentralPublication extends DefaultTask {
    @Input
    public abstract Property<Boolean> getPublishingEnabled();

    @Input
    public abstract Property<String> getPublicationVersion();

    @TaskAction
    public void verify() {
        if (!getPublishingEnabled().get()) {
            throw new GradleException("Central publishing requires -Pio.geewit.publish.enabled=true. "
                    + "Use publishToMavenLocal for unsigned local development.");
        }
        String version = getPublicationVersion().get();
        if (version.isBlank() || "unspecified".equals(version) || version.endsWith("-SNAPSHOT")) {
            throw new GradleException("Central Portal requires a release version; received: " + version);
        }
    }
}
