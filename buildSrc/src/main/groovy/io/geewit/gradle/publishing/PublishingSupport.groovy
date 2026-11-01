package io.geewit.gradle.publishing

import org.gradle.api.Project
import org.gradle.api.provider.Provider
import org.jreleaser.gradle.plugin.JReleaserExtension
import org.jreleaser.model.Active
import org.jreleaser.model.Http

import java.nio.charset.StandardCharsets

/** Shared configuration for aggregate and opt-in standalone Central deployments. */
final class PublishingSupport {
    private PublishingSupport() {}

    static Provider<String> credential(Project project, String name) {
        project.providers.gradleProperty(name).filter { !it.isBlank() }
                .orElse(project.providers.environmentVariable(name).filter { !it.isBlank() })
    }

    static Provider<String> centralToken(Project project) {
        credential(project, 'SONATYPE_BEARER_TOKEN')
                .orElse(credential(project, 'SONATYPE_TOKEN'))
                .orElse(credential(project, 'SONATYPE_USERNAME').zip(
                        credential(project, 'SONATYPE_PASSWORD')) { username, password ->
                    // Preserve the existing convention accepting an already encoded user token.
                    try {
                        String decoded = new String(Base64.decoder.decode(password), StandardCharsets.UTF_8)
                        if (decoded.startsWith(username + ':')) {
                            return password
                        }
                    } catch (IllegalArgumentException ignored) {
                        // A normal Central token password is not necessarily Base64.
                    }
                    Base64.encoder.encodeToString((username + ':' + password).getBytes(StandardCharsets.UTF_8))
                })
    }

    static void configureCentral(Project project, Provider<List<String>> repositories) {
        project.pluginManager.apply('org.jreleaser')
        def enabled = project.providers.gradleProperty('io.geewit.publish.enabled')
                .map { it.toBoolean() }.orElse(false)
        def bearerToken = centralToken(project)
        def centralNamespace = project.providers.gradleProperty('io.geewit.publish.namespace').orElse('io.geewit')
        def githubToken = project.providers.gradleProperty('jreleaser.github.token')
                .orElse(project.providers.environmentVariable('JRELEASER_GITHUB_TOKEN'))

        String projectGroup = project.group.toString()
        project.extensions.configure(JReleaserExtension) { extension ->
            // Aggregate roots have no Maven publication/group of their own.
            extension.project.languages.java.groupId.set(projectGroup.isBlank() ? centralNamespace : projectGroup)
            extension.gitRootSearch.set(true)
            extension.dependsOnAssemble.set(false)
            extension.announce.active.set(Active.NEVER)
            extension.assemble.active.set(Active.NEVER)
            extension.catalog.active.set(Active.NEVER)
            extension.signing.active.set(Active.NEVER) // Gradle signs the Maven publications once.
            extension.upload.active.set(Active.NEVER)
            def github = extension.release.github
            if (githubToken.present) github.token.set(githubToken)
            github.skipTag.set(true)
            github.skipRelease.set(true)
            github.uploadAssets.set(Active.NEVER)

            extension.deploy.maven.mavenCentral.register('app') { deployer ->
                deployer.active.set(enabled.map { it ? Active.RELEASE : Active.NEVER })
                deployer.url.set('https://central.sonatype.com/api/v1/publisher')
                deployer.authorization.set(Http.Authorization.BEARER)
                deployer.password.set(bearerToken)
                deployer.namespace.set(centralNamespace)
                deployer.snapshotSupported.set(false)
                deployer.applyMavenCentralRules.set(true)
                deployer.sign.set(false)
                deployer.checksums.set(false)
                deployer.sourceJar.set(true)
                deployer.javadocJar.set(true)
                deployer.verifyPom.set(true)
                deployer.stagingRepositories.set(repositories)
                // deploymentId is assigned by the Portal, not derived from a GAV.
            }
        }
    }
}
