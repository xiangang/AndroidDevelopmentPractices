package com.nxg.plugins;

import org.gradle.api.Plugin;
import org.gradle.api.Project;

public class GreetingStandaloneGradlePlugins implements Plugin<Project> {

    private static final String TAG = "GreetingStandaloneGradlePlugins";

    @Override
    public void apply(Project project) {
        System.out.println("GreetingStandaloneGradlePlugins(standalone) ---> apply");
        project.task("helloStandalone").doLast(task -> System.out.println("Hello from the com.nxg.plugins.GreetingStandaloneGradlePlugins(standalone)"));
    }
}
