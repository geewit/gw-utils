package gw.utils;

import org.gradle.api.Plugin;
import org.gradle.api.Project;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * Java implementation of the root build logic.
 */
public class GwBuildPlugin implements Plugin<Project> {
    @Override
    public void apply(Project project) {
        project.getLogger().info("rootProject: " + project.getRootProject().getName());
        loadVersionProperties(project);
        project.subprojects(sub -> sub.getPluginManager().apply("java-library"));
    }

    private void loadVersionProperties(Project project) {
        File versionFile = project.getRootProject().file("version.properties");
        if (!versionFile.exists()) {
            return;
        }
        Properties props = new Properties();
        try (FileInputStream fis = new FileInputStream(versionFile)) {
            props.load(fis);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load version.properties", e);
        }
        props.forEach((k, v) -> project.getRootProject().getExtensions().getExtraProperties()
                .set(k.toString(), v));
    }
}
