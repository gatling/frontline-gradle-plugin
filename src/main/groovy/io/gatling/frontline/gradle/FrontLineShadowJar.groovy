package io.gatling.frontline.gradle

import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import org.gradle.api.artifacts.ModuleIdentifier
import org.gradle.api.artifacts.ResolvedConfiguration
import org.gradle.api.artifacts.ResolvedDependency
import org.gradle.api.file.FileCollection
import org.gradle.api.tasks.CacheableTask
import org.gradle.api.tasks.Classpath
import org.gradle.api.tasks.TaskAction

@CacheableTask
class FrontLineShadowJar extends ShadowJar {

    private static final DependencyId NETTY_ALL = new DependencyId("io.netty", "netty-all")

    private ResolvedConfiguration getResolvedConfiguration() {
        return project.configurations.gatlingCompileClasspath.resolvedConfiguration
    }

    private String gatlingVersion() {
        for (artifact in getResolvedConfiguration().resolvedArtifacts.flatten()) {
            def id = artifact.moduleVersion.id
            if (id.group == "io.gatling" && id.name == "gatling-app") {
                getLogger().debug("Detected Gatling compile version: {}", id.version)
                return id.version
            }
        }
        throw new IllegalArgumentException("Couldn't locate io.gatling:gatling-app in dependencies")
    }

    private void treeToSet(ResolvedDependency dep, Set<DependencyId> acc) {
        def id = dep.module.id
        acc.add(new DependencyId(id.group, id.name))
        for (child in dep.children) {
            treeToSet(child, acc)
        }
    }

    private void collectGatlingDepsRec(Set<ResolvedDependency> deps, Set<DependencyId> acc) {
        for (dep in deps) {
            if (dep?.module?.id?.group in ["io.gatling", "io.gatling.highcharts", "io.gatling.frontline"]) {
                treeToSet(dep, acc)
            } else {
                collectGatlingDepsRec(dep.children, acc)
            }
        }
    }

    private static boolean excludeDep(ModuleIdentifier moduleIdentifier, Set<DependencyId> gatlingDependencies) {
        def dependency = new DependencyId(moduleIdentifier.group, moduleIdentifier.name)
        dependency == NETTY_ALL || gatlingDependencies.contains(dependency)
    }

    @Override
    @Classpath
    FileCollection getIncludedDependencies() {
        Set<DependencyId> gatlingDependencies = new HashSet<DependencyId>()
        collectGatlingDepsRec(getResolvedConfiguration().getFirstLevelModuleDependencies(), gatlingDependencies)

        Set<File> wantedFiles = getResolvedConfiguration().getFiles {
            !it.hasProperty("module") || !ModuleIdentifier.isInstance(it.module) || !FrontLineShadowJar.excludeDep(it.module, gatlingDependencies)
        }

        project.files(wantedFiles)
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

    private static class DependencyId {
        private String group
        private String artifact

        DependencyId(String group, String artifact) {
            this.group = group
            this.artifact = artifact
        }

        @Override
        boolean equals(o) {
            if (this.is(o)) return true
            if (getClass() != o.class) return false

            DependencyId that = (DependencyId) o

            if (artifact != that.artifact) return false
            if (group != that.group) return false

            return true
        }

        @Override
        int hashCode() {
            int result
            result = (group != null ? group.hashCode() : 0)
            result = 31 * result + (artifact != null ? artifact.hashCode() : 0)
            return result
        }

        @Override
        String toString() {
            return group + ":" + artifact
        }
    }
}
