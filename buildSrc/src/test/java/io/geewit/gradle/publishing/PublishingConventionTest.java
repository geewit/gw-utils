package io.geewit.gradle.publishing;

import org.gradle.testkit.runner.BuildResult;
import org.gradle.testkit.runner.GradleRunner;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipFile;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PublishingConventionTest {
    @TempDir
    Path directory;

    @Test
    void localPublicationWorksWithoutCentralOrSigningCredentials() throws IOException {
        library("", "1.2.3", false);
        write("settings.gradle", "rootProject.name = 'fixture'\n");
        Path repository = directory.resolve("maven-local");

        run("publishToMavenLocal", "-Dmaven.repo.local=" + repository);

        Path artifact = repository.resolve("io/example/release/demo/1.2.3/demo-1.2.3");
        for (String suffix : List.of(".jar", "-sources.jar", "-javadoc.jar", ".pom", ".module")) {
            assertTrue(Files.isRegularFile(Path.of(artifact + suffix)), suffix);
        }
        String pom = Files.readString(Path.of(artifact + ".pom"));
        assertTrue(pom.contains("<groupId>io.example.release</groupId>"));
        assertTrue(pom.contains("<artifactId>demo</artifactId>"));
        assertTrue(pom.contains("<name>Fixture library</name>"));
        assertTrue(pom.contains("<developerConnection>scm:git:https://example.org/demo.git</developerConnection>"));
        assertFalse(Files.exists(Path.of(artifact + ".jar.asc")));
        try (var javadoc = new ZipFile(artifact + "-javadoc.jar")) {
            assertNotNull(javadoc.getEntry("index.html"));
            assertFalse(javadoc.stream().anyMatch(entry -> entry.getName().startsWith("resource-files/fonts/")));
        }
    }

    @Test
    void centralStagingCannotSilentlyPublishWhenDisabled() throws IOException {
        library("", "1.2.3", false);
        write("settings.gradle", "rootProject.name = 'fixture'\n");

        BuildResult result = runner("publishAllPublicationsToJreleaserRepository").buildAndFail();

        assertTrue(result.getOutput().contains("io.geewit.publish.enabled=true"));
        assertFalse(Files.exists(directory.resolve("build/staging-deploy/io/example/release/demo/1.2.3/demo-1.2.3.jar")));
    }

    @Test
    void centralRejectsSnapshotBeforeUploading() throws IOException {
        library("", "1.2.3-SNAPSHOT", true);
        write("settings.gradle", "rootProject.name = 'fixture'\n");

        BuildResult result = runner("publishAllPublicationsToJreleaserRepository").buildAndFail();

        assertTrue(result.getOutput().contains("release version"));
    }

    @Test
    void releaseStagingRequiresSignatures() throws IOException {
        library("", "1.2.3", true);
        write("settings.gradle", "rootProject.name = 'fixture'\n");

        BuildResult result = runner("publishAllPublicationsToJreleaserRepository").buildAndFail();

        assertTrue(result.getOutput().contains("signatory") || result.getOutput().contains("signing"));
        assertFalse(Files.exists(directory.resolve("build/staging-deploy/io/example/release/demo/1.2.3/demo-1.2.3.jar")));
    }

    @Test
    void aggregateDeploymentBuildsEveryDeclaredModuleAndStagesOnce() throws IOException {
        write("settings.gradle", "rootProject.name = 'fixture'\ninclude 'first', 'second'\n");
        write("gradle.properties", "version=1.2.3\nio.geewit.publish.enabled=false\n");
        write("build.gradle", """
                plugins { id 'io.geewit.central-publish' }
                dependencies {
                    centralStaging project(path: ':first', configuration: 'centralStagingElements')
                    centralStaging project(path: ':second', configuration: 'centralStagingElements')
                }
                """);
        library("first/", "1.2.3", false);
        library("second/", "1.2.3", false);

        for (String entry : List.of("jreleaserDeploy", "jreleaserFullRelease")) {
            BuildResult result = run(entry, "--dry-run");
            assertTrue(result.getOutput().contains(":first:publishMavenJavaPublicationToJreleaserRepository"));
            assertTrue(result.getOutput().contains(":second:publishMavenJavaPublicationToJreleaserRepository"));
            assertTrue(result.getOutput().contains(":stageMavenCentral"));
            assertFalse(result.getOutput().contains(":first:jreleaserDeploy"));
        }
    }

    @Test
    void signedAggregationDiscardsStaleArtifactsAndVersions() throws IOException {
        write("settings.gradle", "rootProject.name = 'fixture'\ninclude 'library'\n");
        write("gradle.properties", "version=1.2.3\nio.geewit.publish.enabled=true\n");
        write("build.gradle", """
                plugins { id 'io.geewit.central-publish' }
                dependencies {
                    centralStaging project(path: ':library', configuration: 'centralStagingElements')
                }
                """);
        library("library/", "1.2.3", true);
        write("library/build/staging-deploy/io/example/release/demo/1.2.3/demo-1.2.3-obsolete.jar", "old classifier");
        write("library/build/staging-deploy/io/example/release/retired/1.2.3/retired-1.2.3.jar", "old coordinate");
        write("library/build/staging-deploy/io/example/release/demo/1.0.0/demo-1.0.0.jar", "old version");
        write("build/central-staging/old-file", "old aggregate");
        // This unprotected fixture key has no identity or release authority and is never uploaded.
        String key;
        try (var input = getClass().getResourceAsStream("/publishing-test-key.asc")) {
            key = new String(input.readAllBytes(), StandardCharsets.UTF_8);
        }
        var environment = new HashMap<>(runner().getEnvironment());
        environment.putAll(Map.of("SIGNING_KEY", key, "SIGNING_PASSWORD", ""));

        runner("stageMavenCentral").withEnvironment(environment).build();

        Path staging = directory.resolve("build/central-staging");
        Path artifact = staging.resolve("io/example/release/demo/1.2.3/demo-1.2.3");
        for (String suffix : List.of(".jar", "-sources.jar", "-javadoc.jar", ".pom", ".module")) {
            assertTrue(Files.isRegularFile(Path.of(artifact + suffix)), suffix);
            assertTrue(Files.isRegularFile(Path.of(artifact + suffix + ".asc")), suffix + ".asc");
        }
        assertFalse(Files.exists(Path.of(artifact + "-obsolete.jar")));
        assertFalse(Files.exists(staging.resolve("io/example/release/retired")));
        assertFalse(Files.exists(staging.resolve("io/example/release/demo/1.0.0")));
        assertFalse(Files.exists(staging.resolve("old-file")));
    }

    @Test
    void localPublishingReusesConfigurationCache() throws IOException {
        library("", "1.2.3", false);
        write("settings.gradle", "rootProject.name = 'fixture'\n");
        String repository = "-Dmaven.repo.local=" + directory.resolve("maven-local");

        run("publishToMavenLocal", repository, "--configuration-cache");
        BuildResult second = run("publishToMavenLocal", repository, "--configuration-cache");

        assertTrue(second.getOutput().contains("Reusing configuration cache."));
    }

    @Test
    void standaloneDeploymentPreparesArtifactsForBothEntrypoints() throws IOException {
        library("", "1.2.3", false);
        write("settings.gradle", "rootProject.name = 'fixture'\n");
        Path properties = directory.resolve("gradle.properties");
        Files.writeString(properties, Files.readString(properties).replace(
                "io.geewit.jreleaser.skip=true", "io.geewit.jreleaser.skip=false"));

        for (String entry : List.of("jreleaserDeploy", "jreleaserFullRelease")) {
            BuildResult result = run(entry, "--dry-run");
            assertTrue(result.getOutput().contains(":publishMavenJavaPublicationToJreleaserRepository"));
        }
    }

    @Test
    void centralCredentialsPreservePriorityAndLegacyEncoding() throws IOException {
        library("", "1.2.3", false);
        write("settings.gradle", "rootProject.name = 'fixture'\n");
        Files.writeString(directory.resolve("build.gradle"), """
                import io.geewit.gradle.publishing.PublishingSupport
                plugins { id 'io.geewit.maven-publish' }
                def actual = PublishingSupport.centralToken(project)
                def expected = providers.gradleProperty('expectedToken')
                tasks.register('verifyToken') {
                    doLast { assert actual.get() == expected.get() }
                }
                """);
        String encoded = "dXNlcjpwYXNz"; // Base64 of the dummy user:pass credential.
        var environment = new HashMap<>(runner().getEnvironment());
        environment.putAll(Map.of("SONATYPE_USERNAME", "user", "SONATYPE_PASSWORD", "pass"));
        runner("verifyToken", "-PexpectedToken=" + encoded).withEnvironment(environment).build();
        environment.put("SONATYPE_PASSWORD", encoded);
        runner("verifyToken", "-PexpectedToken=" + encoded).withEnvironment(environment).build();
        environment.put("SONATYPE_TOKEN", "token-alias");
        runner("verifyToken", "-PexpectedToken=token-alias").withEnvironment(environment).build();
        environment.put("SONATYPE_BEARER_TOKEN", "environment-token");
        runner("verifyToken", "-PexpectedToken=environment-token", "-PSONATYPE_TOKEN=property-alias")
                .withEnvironment(environment).build();
        runner("verifyToken", "-PexpectedToken=property-token", "-PSONATYPE_BEARER_TOKEN=property-token")
                .withEnvironment(environment).build();
    }

    private void library(String prefix, String version, boolean centralEnabled) throws IOException {
        write(prefix + "build.gradle", "plugins { id 'io.geewit.maven-publish' }\n");
        write(prefix + "gradle.properties", """
                group=io.example.release
                artifactId=demo
                io.geewit.jreleaser.skip=true
                POM_NAME=Fixture library
                POM_DESCRIPTION=A fixture for publication integration tests
                POM_URL=https://example.org/demo
                POM_SCM_URL=https://example.org/demo
                POM_SCM_CONNECTION=scm:git:https://example.org/demo.git
                POM_SCM_DEV_CONNECTION=scm:git:https://example.org/demo.git
                """ + "version=" + version + "\nio.geewit.publish.enabled=" + centralEnabled + "\n");
        write(prefix + "src/main/java/example/Library.java", """
                package example;
                /** Minimal library used to verify the published artifacts. */
                public final class Library { private Library() {} }
                """);
    }

    private void write(String path, String content) throws IOException {
        Path target = directory.resolve(path);
        Files.createDirectories(target.getParent());
        Files.writeString(target, content);
    }

    private BuildResult run(String... arguments) {
        return runner(arguments).build();
    }

    private GradleRunner runner(String... arguments) {
        var args = new ArrayList<>(List.of(arguments));
        args.addAll(List.of("--offline", "--stacktrace", "--console=plain"));
        var environment = new HashMap<>(System.getenv());
        environment.keySet().removeIf(key -> key.startsWith("SONATYPE_")
                || key.startsWith("SIGNING_") || key.startsWith("JRELEASER_")
                || key.startsWith("ORG_GRADLE_PROJECT_"));
        return GradleRunner.create().withProjectDir(directory.toFile())
                .withTestKitDir(directory.resolve("testkit").toFile())
                .withPluginClasspath().withArguments(args).withEnvironment(environment);
    }
}
