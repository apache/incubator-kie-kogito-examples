<!--
  Licensed to the Apache Software Foundation (ASF) under one
  or more contributor license agreements.  See the NOTICE file
  distributed with this work for additional information
  regarding copyright ownership.  The ASF licenses this file
  to you under the Apache License, Version 2.0 (the
  "License"); you may not use this file except in compliance
  with the License.  You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

  Unless required by applicable law or agreed to in writing,
  software distributed under the License is distributed on an
  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
  KIND, either express or implied.  See the License for the
  specific language governing permissions and limitations
  under the License.
  -->

# Kogito Gradle Plugin Examples

This modules contains springboot kogito examples built with gradle.
This module, and its children, also contains pom.xml files so that they can be built and tested inside maven-based CI.

To avoid duplication of the same folder, all the concrete examples uses the same gradle executables [gradlew](./gradlew) or [gradlew.bat](./gradlew.bat) and relates to the same  [gradle-wrapper](./gradle) directory.

## CI Integration
This module, and its children, also contains pom.xml files so that they can be built and tested inside maven-based CI.

Maven compilation is disabled, in the actual examples, with this snippet in the pom.xml

```xml
<!-- exclude maven compilation -->
      <plugin>
        <groupId>org.apache.maven.plugins</groupId>
        <artifactId>maven-compiler-plugin</artifactId>
        <configuration>
          <excludes>
            <exclude>**/*.java</exclude>
          </excludes>
          <testExcludes>
            <exclude>**/*.java</exclude>
          </testExcludes>
        </configuration>
      </plugin>
```

while the next one is used to fire gradle test tasks from maven (featuring the "exec-maven-plugin"):

```xml
<!-- execute Gradle command -->
      <plugin>
        <groupId>org.codehaus.mojo</groupId>
        <artifactId>exec-maven-plugin</artifactId>
        <executions>
          <execution>
            <id>gradle</id>
            <phase>test</phase>
            <configuration>
              <executable>${gradle.executable}</executable>
              <arguments>
                <argument>clean</argument>
                <argument>test</argument>
              </arguments>
            </configuration>
            <goals>
              <goal>exec</goal>
            </goals>
          </execution>
        </executions>
      </plugin>
```

The maven-clean-plugin is also configured to include the gradle-specific `build` directory:

```xml
<plugin>
        <artifactId>maven-clean-plugin</artifactId>
        <configuration>
          <filesets>
            <fileset>
              <directory>${project.basedir}/build</directory>
            </fileset>
          </filesets>
        </configuration>
      </plugin>
```

The executed gradle script (`gradlew` vs `gradlew.bat`) is selected at execution time depending on the underlying OS; by default is `gradlew`, but the following snippet adapt it on Windows systems:

```xml
<profile>
      <id>WINDOWS</id>
      <activation>
        <os>
          <family>windows</family>
        </os>
      </activation>
      <properties>
        <gradle.executable>${project.parent.parent.basedir}/gradlew.bat</gradle.executable>
      </properties>
    </profile>
 ```



## GRADLE Profiles
GRADLE does not offer the concept of profile out of the box, as MAVEN.
TO achieve that, the steps to follow are described [here](https://docs.gradle.org/current/userguide/migrating_from_maven.html#migmvn:profiles_and_properties)

1. inside GRADLE, we'll call them `buildProfile`
2. create a `build.gradle` file with all common settings
3. for each `buildProfile`, create a `profile-${buildProfile}.gradle` file, containing only the profile-specific settings
4. add the following snippet in the `build.gradle`, to load the `buildProfile` specific building script
```groovy
if (!hasProperty('buildProfile')) ext.buildProfile = 'prod'
apply from: "profile-${buildProfile}.gradle"
```
(here, we are assuming that, by default, the buildProfile will be `prod`)



