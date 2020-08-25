# FrontLine Gradle plugin

## Dependency

This plugin apply `io.gatling.gradle`.

When applying this `io.gatling.frontline.gradle`, you will be able to do all the configurations possible with the [`io.gatling.gradle` plugin](https://github.com/gatling/gatling-gradle-plugin)

## Commands

This plugin adds a new command to the gradle build: `frontLineJar`

It creates a package in the format expected by [Gatling FrontLine](https://gatling.io/gatling-frontline/).


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
        mavenLocal()
        jcenter()
      }

      sourceCompatibility = 1.8
      targetCompatibility = 1.8
        
      repositories {
        mavenLocal()
        jcenter()
      }
        
      tasks.withType(ScalaCompile) {
        scalaCompileOptions.forkOptions.with {
          jvmArgs = ['-Xss100M']
        }
      }
      ```
