package io.gatling.frontline.gradle


import org.gradle.api.Plugin
import org.gradle.api.Project

class FrontLinePlugin implements Plugin<Project> {

    @Override
    void apply(Project project) {
        if (!project.plugins.findPlugin('io.gatling.gradle')) {
            project.getLogger().info("io.gatling.gradle not found, applying it")
            project.pluginManager.apply('io.gatling.gradle')
        }

        FrontLineShadowJar frontLineJar = project.tasks.create(name: "frontLineJar", type: FrontLineShadowJar)

        frontLineJar.conventionMapping.with {
            map("classifier") {
                "tests"
            }
        }

        frontLineJar.exclude(
          "META-INF/LICENSE",
          "META-INF/MANIFEST.MF",
          "META-INF/versions/**",
          "**/*.SF",
          "**/*.DSA",
          "**/*.RSA"
        )

        frontLineJar.from(project.sourceSets.gatling.output)
        frontLineJar.configurations = [
                project.configurations.gatlingRuntimeClasspath
        ]
    }
}
