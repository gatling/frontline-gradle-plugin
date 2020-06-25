package io.gatling.frontline.gradle

import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import org.gradle.api.artifacts.ResolvedConfiguration
import org.gradle.api.artifacts.ResolvedDependency
import org.gradle.api.file.FileCollection
import org.gradle.api.logging.LogLevel
import org.gradle.api.tasks.CacheableTask
import org.gradle.api.tasks.Classpath
import org.gradle.api.tasks.TaskAction

@CacheableTask
class FrontLineShadowJar extends ShadowJar {

    private ResolvedConfiguration resolvedConfiguration() {
        return project.configurations.testCompileClasspath.resolvedConfiguration
    }

    private String gatlingVersion() {
        for (artifact in resolvedConfiguration().resolvedArtifacts) {
            def id = artifact.moduleVersion.id
            if (id.group == "io.gatling" && id.name == "gatling-app") {
                getLogger().debug("Detection Gatling compile version: {}", id.version)
                return id.version
            }
        }
        throw new IllegalArgumentException("Couldn't locate io.gatling:gatling-app in dependencies")
    }

    private void collectGatlingDepsRec(Set<ResolvedDependency> deps, Set<File> acc) {
        for (dep in deps) {
            def id = dep.module.id
            if (id.group == "io.gatling" || id.group == "io.gatling.highcharts" || id.group == "io.gatling.frontline") {
                acc.addAll(dep.allModuleArtifacts.collect { it.file })
            } else {
                collectGatlingDepsRec(dep.children, acc)
            }
        }
    }

    private Set<File> gatlingDeps() {
        def acc = new HashSet<File>()
        collectGatlingDepsRec(resolvedConfiguration().firstLevelModuleDependencies, acc)
        return acc
    }

    @Override
    @Classpath
    FileCollection getIncludedDependencies() {
        def allDeps = new HashSet(configurations.files.flatten())
        def gatlingDeps = gatlingDeps()
        if (getLogger().isEnabled(LogLevel.DEBUG)) {
            for (dep in gatlingDeps) {
                getLogger().debug("Excluding Gatling dep {}", dep)
            }
        }
        def nonGatlingDeps = allDeps - gatlingDeps
        if (getLogger().isEnabled(LogLevel.DEBUG)) {
            for (dep in nonGatlingDeps) {
                getLogger().debug("Including non Gatling dep {}", dep)
            }
        }
        return project.files(nonGatlingDeps)
    }

    @TaskAction
    protected void copy() {
        String gatlingVersion = gatlingVersion()
        manifest {
            attributes("Manifest-Version": "1.0",
                    "Implementation-Title": project.name,
                    "Implementation-Version": project.version,
                    "Implementation-Vendor": project.group,
                    "Specification-Vendor": "GatlingCorp",
                    "Gatling-Version": gatlingVersion)
        }
        super.copy()
    }
}
