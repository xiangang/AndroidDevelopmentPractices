package com.nxg.plugin;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.jetbrains.annotations.NotNull;

class CustomGradlePlugin implements Plugin<Project> {

    @Override
    public void apply(@NotNull Project project) {
        System.out.println("CustomGradlePlugin: apply --------------> ");
    }
}

