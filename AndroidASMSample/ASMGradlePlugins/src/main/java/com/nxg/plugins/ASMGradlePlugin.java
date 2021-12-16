package com.nxg.plugins;

import com.android.build.gradle.AppExtension;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.jetbrains.annotations.NotNull;

public class ASMGradlePlugin implements Plugin<Project> {

    @Override
    public void apply(@NotNull Project project) {
        System.out.println("ASMGradlePlugin: apply------------------>");
        AppExtension appExtension = project.getExtensions().getByType(AppExtension.class);
        appExtension.registerTransform(new RoomOpenHelperTransform(project));
        project.task("hello").doLast(task -> System.out.println("Hello from the com.nxg.plugins.JavaGreetingPlugin(buildSrc)"));
    }
}
