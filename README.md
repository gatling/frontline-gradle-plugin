# FrontLine Gradle plugin
[![test-only](https://github.com/gatling/frontline-gradle-plugin/workflows/test-only/badge.svg?branch=master)](https://github.com/gatling/frontline-gradle-plugin/actions?query=branch%3Amaster)

Gradle plugin to create a package in the format expected by
[Gatling FrontLine](https://gatling.io/gatling-frontline/).

## Dependency

This plugin applies `io.gatling.gradle`.

## Dev testing

Steps to be able to dev test this plugin:

1. Checkout this project:

    ```console
    $ git clone git@github.com:gatling/frontline-gradle-plugin.git
    ```

2. In a separate directory, create a toy project containing:

    * `settings.gradle`:

      ```
      includeBuild '<path/to>/frontline-gradle-plugin'
      ```

    * `build.gradle`:

      ```groovy
      plugins {
        id 'java-library'
        id 'io.gatling.frontline.gradle'
      }

      repositories {
        mavenCentral()
        jcenter()
      }

      sourceCompatibility = 1.8
      targetCompatibility = 1.8
        
      tasks.withType(ScalaCompile) {
        scalaCompileOptions.forkOptions.with {
          jvmArgs = ['-Xss100M']
        }
      }
      ```
