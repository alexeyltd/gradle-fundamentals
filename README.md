<!-- START doctoc generated TOC please keep comment here to allow auto update -->
<!-- DON'T EDIT THIS SECTION, INSTEAD RE-RUN doctoc TO UPDATE -->
**Table of Contents**  *generated with [DocToc](http://doctoc.herokuapp.com/)*

- [Gradle course with Pluralsight](#gradle-course-with-pluralsight)
  - [Introduction](#introduction)
    - [What is Gradle](#what-is-gradle)
    - [Install](#install)
    - [Gradle Builds](#gradle-builds)
      - [Hello Gradle](#hello-gradle)
      - [Task Lifecycle](#task-lifecycle)
      - [Build Java](#build-java)
    - [Gradle Wrapper](#gradle-wrapper)
  - [Basic Gradle Tasks](#basic-gradle-tasks)
    - [What is a Task](#what-is-a-task)
    - [Writing Simple Tasks](#writing-simple-tasks)
    - [Running Tasks](#running-tasks)
    - [Task Phases](#task-phases)
    - [Task Dependencies](#task-dependencies)
    - [Setting Properties on Tasks](#setting-properties-on-tasks)
  - [Task Dependencies](#task-dependencies-1)
    - [Other Dependencies](#other-dependencies)
  - [Typed Tasks](#typed-tasks)
    - [The Copy Task](#the-copy-task)
  - [Building a Java Project](#building-a-java-project)
    - [Introduction to the Java Plugin](#introduction-to-the-java-plugin)
    - [Writing Your First Java Build](#writing-your-first-java-build)
    - [Performance and the Gradle Daemon](#performance-and-the-gradle-daemon)
    - [Multi-project Builds](#multi-project-builds)
  - [Dependencies](#dependencies)
    - [Introduction to Repositories](#introduction-to-repositories)
    - [Repository Dependencies](#repository-dependencies)
    - [Gradle Cache](#gradle-cache)
  - [Testing](#testing)
    - [Running Tests](#running-tests)
    - [Using Filters to Select Tests](#using-filters-to-select-tests)
    - [Adding Other Test Types](#adding-other-test-types)

<!-- END doctoc generated TOC please keep comment here to allow auto update -->

# Gradle course with Pluralsight

## Introduction

### What is Gradle

* Build by convention
* Written in Groovy, it's a Groovy DSL
* Dependency management with Ivy or Maven
* Supports multi-project builds
* Can build things other than Java projects such as JavaScript or C++
* Highly customizable
* Declarative Build Language (expresses intent)
  * We tell Gradle _what_ we would like to happen, not _how_
  * Makes build scripts easier to read than Ant or Maven xml
  * Maintainable

### Install

```shell
$ brew cask install java
$ brew install gradle
```

### Gradle Builds

* Build file is typically named `build.gradle`
* Build file contains _tasks_, and
  * plugins
  * dependencies

#### Hello Gradle

Save as `build.gradle`:

```groovy
task hello {
  doLast {
    println "Hello, Gradle"
  }
}
```

To run this, `cd` to directly where `build.gradle` is located and run

```shell
$ gradle hello
```

#### Task Lifecycle

* Initialization phase
* Configuration phase
* doFirst (method)
* doLast (method)

#### Build Java

```groovy
apply plugin: 'java'
```

The java plugin uses a convention for directory structure

```
├── example
│   ├── build.gradle
│   └── src
│       ├── main
│       │   └── java
│       │       └── learning
│       │           └── App.java
│       └── test
│           └── java
│               └── learning
│                   └── AppTest.java
```

Running `gradle tasks` displays a list of all the tasks that the java plugin has installed.

Run `gradle build` to build the project.

Gradle output will mark a task `UP-TO-DATE` if there's nothing to do. Re-running a build will only rebuild things that have changed.

Gradle output goes to the `build` folder, example compiled classes, libs, jar files, etc.

Simple example can be run `java -cp build/classes/main com.pluralsight.Hello`

### Gradle Wrapper

Recommended way to run gradle to ensure the same version is always used on a project by everyone on the team. In the build file, add a wrapper task to _set_ the gradle version:

```groovy
task wrapper(type: Wrapper) {
  gradleVersion = '2.11'
}
```

Execute the wrapper task:

```shell
$ gradle wrapper
```

This task sets up the gradle wrapper. Generates two new files in project root, `gradlew` (shell script) and `gradlew.bat` (for windows).

Also generates a `gradle` directory in project root, containing a wrapper directory with `gradle-wrapper.jar` and `gradle-wrapper.properties`.

Now instead of running tasks via `gradle`, instead use the wrapper, `gradlew`. For example:

```
./gradlew build
```

This will first download the version of gradle specified in the wrapper, install it in your home `.gradle` directory, then run the task using that gradle version.

The gradle wrapper scripts and jar files can be checked into version control, along with the gradle wrapper task specified in the build file. This ensures that everyone who checks out the project and runs the build will do so with the same gradle version.

## Basic Gradle Tasks

Gradle DSL is written in Groovy, which is a JVM language. See [Groovy Fundamentals Course on Pluralsight](https://app.pluralsight.com/library/courses/groovy-fundamentals/table-of-contents).

### What is a Task

* Code that Gradle will execute
* Has a __lifecycle__ (different parts of the task will run at different times)
  * Initialization phase
  * Configuration phase
  * Execution phase
* Has __properties__
  * Common properties such as description, and group it belongs to
  * Custom to that task such as directory files are being copied to or from
  * Properties are typically configured during the Configuration Phase of the lifecycle
* Has __actions__ (code that executes), divided into two parts:
  * First action - code that needs to run before other code within the task
  * Last action - code that executes as part of the task
* Has __dependencies__: A task may require that another task complete before this one can execute. Gradle will work out the task dependencies and take care that tasks run in the correct order.

### Writing Simple Tasks

Groovy is object oriented. In a Gradle build script, top level object is `project`.
This object is used to define everything within build script.

To create a task:

```groovy
project.task("Task1")
```

To list all tasks

```shell
$ gradle tasks
```

Notice "Task1" is in a group called "Other tasks".

Can also define a task without `project` keyword, because Gradle knows "project" is the top level object and will delegate everything to it:

```groovy
task("Task2")
```

Another way to define a task, don't need brackets:

```groovy
task "Task3"
```

Finally, don't even need the quotes to define a task:

```groovy
task Task4
```

To add a property to a task:

```groovy
Task4.description = "My super duper awesome task"
```

Description will appear in shell when running `gradle tasks`.

### Running Tasks

First need to write a task action. All tasks have a `doLast` method, which is the last thing the task does. It gets passed a _Groovy closure_, which is code that lives between braces.

```groovy
task Task4
Task4.description = "My super duper awesome task"
Task4.doLast { println "This is Task 4" }
```

To run the task:

```shell
$ gradle Task4
```

Another way to write a task action is to use left shift operator `<<` instead of explicit `doLast`.
`<<` is overridden by Groovy to add a closure to `doLast`.

```groovy
task Task3
Task3 << println "This is Task 3"
```

Can declare a task and add action in one line:

```groovy
task Task5 << { println "This is Task 5" }
```

Note you can keep adding clojures, and Gradle will append them:

```groovy
task Task5 << { println "This is Task 5" }
Task5 << { println "Another closure" }
```

Can add properties and actions all at once:

```groovy
task Task6 {
  description "Task 6 is the best task ever"
  doLast {
    println "Task 6 is running"
  }
}
```

### Task Phases

* Initialization phase: Used to configure multi project builds
* Configuration phase: Executes code in the task that's not in the action, for example, setting the "description" property
* Execution phase: Execute the task actions such as `doFirst`, `doLast`

Every task has a `doFirst` method, for example:

```groovy
task Task6 {
  description "Task 6 is the best task ever"
  doFirst {
    "Task 6 first"
  }
  doLast {
    println "Task 6 is running"
  }
}
```

A task can have multiple `doFirst`'s and multiple `doLast`'s. When left shift `<<` operator is used to append a task, it gets added to the `doLast`. To add multiple `doFirst`'s, simply call it multiple times:

```groovy
task Task6 {
  description "Task 6 is the best task ever"
  doFirst {
    println "Task 6 first"
  }
  doLast {
    println "Task 6 is running"
  }
}

Task6.doFirst {
  println "Task 6 another doFirst closure was appended"
}
```

Output from running `gradle Task6`:

```
:Task6
Task 6 another doFirst closure was appended
Task 6 first
Task 6 is running

BUILD SUCCESSFUL
```

### Task Dependencies

Each task has a `dependsOn` method that can be passed the tasks that this task depends on:

```groovy
Task6.dependsOn Task5
```

Now running Task6 will FIRST run Task5, then Task6. Notice that Task6's `doFirst` methods run _after_ Task5's `doLast` methods:

```
Task5
This is Task 5
Another closure
:Task6
Task 6 another doFirst closure was appended
Task 6 first
Task 6 is running
```

Continuing on, if Task5 depends on Task4, then running Task6 will first run 4, 5, then 6.

A task can also have _multiple_ dependencies:

```groovy
Task6.dependsOn Task5
Task6.dependsOn Task3
```

Can also specify multiple dependencies as comma separated list:

```groovy
Task6.dependsOn Task5, Task3
```

Can also specify dependencies inside the task closure:

```groovy
task Task7 {
  description "This is task 7"
  dependsOn Task6
  doFirst {
    println "Task 7 doFirst"
  }
  doLast {
    println "Task 7 doLast"
  }
}
```

Dependencies are built during the configuration phase. When a task is executed, the dependency graph has already been determined, and Gradle can simply walk through the graph, executing tasks.

### Setting Properties on Tasks

Properties can be defined using local variables with `def` keyword.
Then the variable can be used in tasks using string interpolation.

Local variable has scope of the given build file in which its declared. This might not be suitable for a multi-project build if need the variable available in other build files.

Variables can also be defined inside a task, in a closure.

```groovy
def projectVersion = "2.1.0"

Task6 {
  doLast {
    println "This is task 6 - version $projectVersion"
  }
}
```

To have a variable available in a larger scope, across projects, use _extra properties_.

```groovy
project.ext.projectVersion = "2.1.0"
...
doFirst {
  println "Task 6 first - some big project var $project.ext.biggerScopeVariable"
}
```

Because its set on project, don't need to state it explicitly, so can just say:

```groovy
ext.projectVersion = "2.1.0"
...
doFirst {
  println "Task 6 first - some big project var $biggerScopeVariable"
}
```

## Task Dependencies

Note: To suppress gradle's output when running tasks, to only see task output, use `-q` flag for quiet logging:

```shell
$ gradle -q taskA
```

When a task has multiple dependencies, the order is undefined, gradle will pick some order:

```groovy
taskA.dependsOn taskB
taskA.dependsOn taskC, taskD
```

Can't predict in what order B, C, D will run in.

### Other Dependencies

* mustRunAfter
  * If two tasks execute, one _must_ run after the other
  * Circular dependencies will fail the build
* shouldRunAfter
  * If two tasks execute, one _should_ run after the other
  * Ignores circular dependencies
* finalizedBy
  * Inverted dependency

Examples:

```groovy
taskB.mustRunAfter taskC, taskD
taskB.shouldRunAfter taskD
```

`mustRunAfter` and `shouldRunAfter` only kick in when both tasks are run. For example:

```groovy
task task1 << { println "task 1" }
task task2 << { println "task 2" }

task2.mustRunAfter task1
```

Then running task1 by itself will NOT cause task 2 to run:

```shell
$ gradle -q task1
```

However, running both at command line will enforce that task1 always runs first, then task 2.
The following two commands will produce the same output:

```shell
$ gradle -q task1 task2
$ gradle -q task2 task1
```

## Typed Tasks

[Example](typed/build.gradle)

All the tasks created up to now have been _Ad-hock tasks_ such as:

* `task Task1`
* `task Task2 << {}`
* `task Task3 {}`

It would be more useful to be able to define a task once, and then re-use it later, passing in some configuration to control its behaviour. This is where _Typed Tasks_ come in.

For example, to copy files, need more complex code (open file, read file, write file), zipping files.

Gradle comes with some built in typed tasks, [docs](https://docs.gradle.org/current/dsl/) (scroll down to "Task types" in side nav).

For example, to use copy type:

```groovy
task copyImages (type: Copy){
  from 'src'
  into 'dest'
}
```

By specifying `type: Copy`, gradle treats this `copyImages` task as if it was the copy task. The task implementation is then just configuration.

Copy task can get quite complex. Some of the config can be extracted using "copy specification".

```groovy
def contentSpec = copySpec {
  exclude 'file1.txt'
  from 'src'
}

/* Does the same thing as copyImages */
task copyImages2 (type: Copy) {
  with contentSpec
  into 'dest'
}
```

If there are a large number of files to exclude, rather than specifying them one at a time, can execute groovy code in the copy spec. Specify a closure for exclude, which gets an iterator `it`:

```groovy
def contentSpec = copySpec {
  exclude {it.file.name.startsWith('file1')}
  from 'src'
}
```

### The Copy Task

Can also rename files, restructure directories, expand files (text replacement). For example, to replace `resourceRefName` with `jdbc/JacketDB` as file web.xml is being copied from `src` directory into `config` directory:

```groovy
task copyConfig (type: Copy) {
  include 'web.xml'
  from 'src'
  into 'config'
  expand ([
    resourceRefName: 'jdbc/JacketDB'
    ])
}
```

`expand` property takes a dictionary with names of text to replace and what it should be replaced with.

Placeholders start with `$` in file, for example web.xml might have `<res-ref-name>$resourceRefName</res-ref-name>`.   

## Building a Java Project

### Introduction to the Java Plugin

[Docs](https://docs.gradle.org/current/userguide/java_plugin.html)

To start, simply add the java gradle plugin to the build file:

```groovy
apply plugin: 'java'
```

The java plugin adds many tasks to the project including:

* build
* clean
* javadoc
* test

Expects standard (maven) project layout:

* src/main/java
* src/main/resources
* src/test/java
* src/test/resources

If you use a different convention for project layout, can be customized using gradle _SourceSets_, to define where sources are located. For example,

```groovy
sourceSets {
  main {
    java {
      srcDir 'src/java'
    }
    resources {
      srcDir 'src/resources'
    }
  }
}
```

### Writing Your First Java Build

[Example](java/build.gradle)

To build a versioned jar, specify version number in build.gradle, for example:

```groovy
version = '1.0.0.SNAPSHOT'
```

Then running `gradle build` will generate a versioned jar in `build/libs`.

The `build`  task is dependent upon a number of other tasks, as can be seen from the output:

```
:compileJava
:processResources
:classes
:jar
:assemble
:compileTestJava
:processTestResources
:testClasses
:test
:check
:build
```

`compileJava` generates the classes files in `build/lib/classes`.

`classes` task seems to do the same thing as `compileJava`, BUT, it depends on `processResources`.

`processResources` is a copy task, which copies resources from `src/main/resources` into `build/classes`.

Therefore `classes` ties together the compilation `compileJava` and the copying of resources `processResources`.

`jar` is a low level task that creates the jar file in `build/lib`.

`assemble` is the java plugin task that creates all the jar files within the project. This is a _lifecycle_ task in the gradle plugin.

### Performance and the Gradle Daemon

Each time the build is run, gradle has to re-launch the jvm. It would be better if gradle could re-use an existing vm. Gradle can do this using the _daemon_.

To use the daemon via cli:

```shell
$ gradle --daemon clean build
```

The first build run with daemon may still be slow, but subsequent builds will be faster.

Alternatively, daemon can be specified via the GRADLE_OPTS environment variable.

But the best way is to create a `gradle.properties` file in home directory and turn on the daemon:

```
org.gradle.daemon=true
```

### Multi-project Builds

Gradle also supports multi-project builds to link them together. For example when a class in one project depends on a class from another project. This is done in a top level gradle build file to specify the projects that make up the multi-project build.

* Add top level settings.gradle, which lists all the projects
* Add top level build.gradle to configure all the projects
    * defines project wide build functionality
    * specify dependencies between projects

`settings.gradle` includes all the projects, for example:

```groovy
include 'Repository', 'Jacket'
```

`build.gradle` specifies any commonalities across projects (eg: applying java plugin to all projects). Also specify dependencies between projects.

To apply java plugin to all projects, wrap the apply plugin call in the `allprojects` method closure:

```groovy
allprojects {
  apply plugin: 'java'
}
```

Specify which projects are part of the multi-build, `:` specifies relative to current location. Furthermore, specify project configuration if needed, for example, `Jacket` project depends on `Repository`:

```groovy
project(':Repository'){}

project(':Jacket'){
  dependencies {
    compile project(':Repository')
  }
}
```

Now can go to the Jacket project (which depends on Repository), run `gradle build`, and it will step up to Repository project for the dependency and build will succeed.

## Dependencies

Typical project will have dependencies on:

* Other projects
* External libraries (eg: on the web somewhere)
* Internal libraries (eg: on company internal nexus repo)

Dependencies can be satisfied from:

* Other projects
* File system
* Maven repositories
* Ivy repositories (gradle support deprecated?)

__Can Have Many Configurations__

Depenedency will often only be needed for a specific action, eg: junit only needed when running tests, but not for compiling application. Can define custom configurations, and also plugins bring in their own configurations.

For example, Java plugin has these configurations:

* compile
* runtime
* testCompile
* testRuntime

Dependencies don't have to be isolated, for example `runtime` dependency extends the `compile` time dependency. Furthermore, dependencies can be included or excluded from each configuration.

__Transitive Dependencies__

Some dependencies will depend on other libraries, these are called _transitive dependencies_.

To list dependencies for a given project (`-q` is quite):

```shell
$ gradle -q dependencies
```

To list dependencies for a given configuration within a project

```shell
$ gradle -q dependencies --configuration compile
```

### Introduction to Repositories

Maven Remote:

```groovy
repositories {
  mavenCentral()
}
repositories {
  jcenter() // https
}
```

Or to use jcenter over http:

```groovy
repositories {
  url "http://jcenter.bintray.com/"
}
```

To use your local maven installation (i.e. `~/.m2`)

```groovy
repositories {
  mavenLocal()
}
```

To use an internal company repo such as nexus:

```groovy
repositories {
  maven {
    url "http://repo.mycompany.com/maven2"
  }
}
```

Repositories can be configured, for example, if need to specify a username and password to access company repo.

Can also specify multiple repositories. Dependencies will be resolved in _order_ which repos are listed:

```groovy
repositories {
  maven {
    url "http://repo.mycompany.com/maven2"
  }
  ivy {
    url "http://repo.mycompany,com/repo"
  }
}
```

### Repository Dependencies

When using a repository, dependencies must specify group, name, and version. For example:

```groovy
dependencies {
  compile group: 'log4j', name: 'log4j', version: '1.2.17'
}
```

Or can use shorthand syntax:

```groovy
dependencies {
  compile 'log4j:log4j:1.2.17'
}
```

Then when running a build, Gradle will download the jars and pom files (metadata). Next time running the build, will not need to download jar/pom again because it gets cached.

### Gradle Cache

Dependencies are cached when they get downloaded.

* File based
* Metadata and files stored separately
* Repository caches are independent

Names of the files in the cache are SHA1 based. To determine if file needs to be downloaded again, look at repository, create SHA1 of file, check the name in the file system, if SHA1's are different, needs to be re-downloaded.

Dependencies can be refreshed by passing `--refresh-dependencies` flag to build (will run SHA1 check described above).

For force refresh, can safely delete cached files, and on next build, everything will be downloaded again.

Gradle cache is at `~/.gradle/caches/modules-2`

## Testing

Testing is integral to the Java plugin. It defines:

* source set where the test sources are located
* task to compile the Tests
* task to run the tests

By convention, the source set will look for tests in `src/test/java`, but that can be customized.

Test compilation output goes to `build/classes/test`.

When tests are run, reports go to `build/reports/test`.

### Running Tests

```shell
$ gradle test
```

Tests are also run as part of `gradle build`.

A failing test fails the build.

### Using Filters to Select Tests

By default, `gradle test` runs _all_ the tests.

A _filter_ can be used to run only a subset of tests, or a single test, or all tests from a package. Wildcards are supported (otherwise need to specify fully qualified name of the test).

Pass a closure to the test command which is a filter, then pass a closure to the filter to specify tests that should be added:

```groovy
test {
  filter {
    includeTestsMatching 'com.foo.shouldCreateASession'
    includeTestsMatching '*shouldCreateASession'
  }
}
```

Can also define a custom task for testing with filter:

```groovy
task singleTest (type: test) {
  dependsOn testClasses
  filter {
    includeTestsMatching '*shouldCreateASession'
  }
}
```

Filter can also be specified at cli, to override any existing filter configuration:

```shell
$ gradle test --tests *shouldCreateASession
```

### Adding Other Test Types

All of the above notes on testing is for unit testing only. But what about other types of tests such as integration?

Use [gradle-testsets-plugin](https://github.com/unbroken-dome/gradle-testsets-plugin).
