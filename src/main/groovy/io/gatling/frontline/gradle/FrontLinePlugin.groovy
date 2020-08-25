package io.gatling.frontline.gradle

import org.gradle.api.*
import org.gradle.api.plugins.JavaLibraryPlugin
import org.gradle.api.plugins.scala.ScalaPlugin

class FrontLinePlugin implements Plugin<Project> {

  @Override
  void apply(Project project) {
    project.getPluginManager().apply(JavaLibraryPlugin.class);
    project.getPluginManager().apply(ScalaPlugin.class);

    FrontLineShadowJar shadow = project.tasks.create(name: "testJar", type: FrontLineShadowJar)

    shadow.conventionMapping.with {
      map("classifier") {
        "tests"
      }
    }
    shadow.from(project.sourceSets.test.output)
    if(project.sourceSets.hasProperty("gatling")) {
      shadow.from(project.sourceSets.gatling.output)
    }
    shadow.configurations = [
      project.configurations.testCompileClasspath
    ]
  }
}
