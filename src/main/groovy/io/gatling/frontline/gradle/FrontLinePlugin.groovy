package io.gatling.frontline.gradle

import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import org.gradle.api.*
import org.gradle.api.plugins.JavaPluginConvention

class FrontLinePlugin implements Plugin<Project> {

  @Override
  void apply(Project project) {
    JavaPluginConvention convention = project.convention.getPlugin(JavaPluginConvention)
    ShadowJar shadow = project.tasks.create(name: "testJar", type: ShadowJar)
    shadow.group = "Gatling FrontLine"

    shadow.conventionMapping.with {
      map("classifier") {
        "tests"
      }
    }
    shadow.from(convention.sourceSets.test.output)
    shadow.configurations = [
      project.configurations.testRuntime
        .exclude(group: "io.gatling", module: "gatling-app")
        .exclude(group: "io.gatling.frontline", module: "frontline-probe")
        .exclude(group: "io.gatling.highcharts", module: "gatling-charts-highcharts")
    ]
    shadow.manifest {
      attributes("Manifest-Version": "1.0",
        "Implementation-Title": project.name,
        "Implementation-Version": project.version,
        "Specification-Vendor": "io.gatling.frontline",
        "Implementation-Vendor": "GatlingCorp")
    }
  }
}
