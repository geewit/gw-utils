package gw.utils;

import org.gradle.api.Plugin;
import org.gradle.api.initialization.ProjectDescriptor;
import org.gradle.api.initialization.Settings;

import java.io.File;

/**
 * Java implementation that dynamically includes subprojects.
 */
public class GwSettingsPlugin implements Plugin<Settings> {
    @Override
    public void apply(Settings settings) {
        settings.getRootProject().setName("gw-utils");
        includeProjects(settings, settings.getRootDir(), settings.getRootDir());
    }

    private void includeProjects(Settings settings, File rootDir, File dir) {
        File[] children = dir.listFiles();
        if (children == null) {
            return;
        }
        for (File child : children) {
            if (child.isDirectory() && !"buildSrc".equals(child.getName()) && !"build-logic".equals(child.getName())) {
                boolean isProject = new File(child, "build.gradle").exists()
                        || new File(child, "settings.gradle").exists()
                        || new File(child, "package.json").exists();
                if (isProject) {
                    String name = buildFullProjectName(rootDir, child);
                    settings.include(name);
                    ProjectDescriptor descriptor = settings.project(name);
                    descriptor.setProjectDir(child);
                }
                includeProjects(settings, rootDir, child);
            }
        }
    }

    private String buildFullProjectName(File rootDir, File dir) {
        String relative = rootDir.toPath().relativize(dir.toPath()).toString();
        return ":" + relative.replace(File.separatorChar, ':');
    }
}
