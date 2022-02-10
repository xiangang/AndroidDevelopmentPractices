package com.nxg.plugins;

import org.gradle.api.Plugin;
import org.gradle.api.Project;

public class JavaGreetingPlugin implements Plugin<Project> {
    @Override
    public void apply(Project project) {
        System.out.println("JavaGreetingPlugin(buildSrc) ---> apply");
        project.task("helloJava").doLast(task -> System.out.println("Hello from the com.nxg.plugins.JavaGreetingPlugin(buildSrc)"));
    }
}
