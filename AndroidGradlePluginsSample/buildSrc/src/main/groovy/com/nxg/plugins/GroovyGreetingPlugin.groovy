package com.nxg.plugins

import org.gradle.api.Plugin
import org.gradle.api.Project

class GroovyGreetingPlugin implements Plugin<Project> {
    void apply(Project project) {
        println "GroovyGreetingPlugin(buildSrc) ---> apply"
        project.task('helloGroovy') {
            doLast {
                println "Hello from the com.nxg.plugins.GroovyGreetingPlugin(buildSrc)"
            }
        }
    }
}