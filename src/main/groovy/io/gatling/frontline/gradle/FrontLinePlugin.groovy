package io.gatling.frontline.gradle

import org.gradle.api.*
import org.gradle.api.plugins.JavaPluginConvention

class FrontLinePlugin implements Plugin<Project> {

  @Override
  void apply(Project project) {
    JavaPluginConvention convention = project.convention.getPlugin(JavaPluginConvention)
    FrontLineShadowJar shadow = project.tasks.create(name: "testJar", type: FrontLineShadowJar)

    shadow.conventionMapping.with {
      map("classifier") {
        "tests"
      }
    }
    shadow.from(convention.sourceSets.test.output)
    shadow.configurations = [
      project.configurations.testCompileClasspath
    ]
  }
}
