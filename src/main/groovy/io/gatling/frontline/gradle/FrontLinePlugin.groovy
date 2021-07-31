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
          "META-INF/maven/**",
          "**/*.SF",
          "**/*.DSA",
          "**/*.RSA"
        )

        frontLineJar.from(project.sourceSets.gatling.output)
        frontLineJar.configurations = [
                project.configurations.gatlingRuntimeClasspath
        ]
        frontLineJar.metaInf {
            def tempDir = new File(frontLineJar.getTemporaryDir(), "META-INF")
            def maven = new File(tempDir, "maven")
            maven.mkdirs()
            new File(maven,"pom.properties").text =
              """groupId=${project.group}
                |artifactId=${project.name}
                |version=${project.version}
                |""".stripMargin()
            new File(maven, "pom.xml").text =
              """<?xml version="1.0" encoding="UTF-8"?>
                |<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                |xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
                |  <modelVersion>4.0.0</modelVersion>
                |  <groupId>${project.group}</groupId>
                |  <artifactId>${project.name}</artifactId>
                |  <version>${project.version}</version>
                |</project>
                |""".stripMargin()
            from (tempDir)
        }
    }
}
