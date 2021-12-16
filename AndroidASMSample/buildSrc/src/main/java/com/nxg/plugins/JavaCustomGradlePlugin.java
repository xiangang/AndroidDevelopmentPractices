package com.nxg.plugins;

import org.gradle.api.Plugin;
import org.gradle.api.Project;

public class JavaCustomGradlePlugin implements Plugin<Project> {

    @Override
    public void apply(Project project) {
        System.out.println("JavaCustomGradlePlugin: apply------------------>");

        project.task("helloBuildSrc").doLast(task -> System.out.println("Hello from the com.nxg.plugins.JavaGreetingPlugin(buildSrc)"));
    }
}
