# Drools Polyglot 
A simple experiment with GraalVM capabilities.

- JS sources are under `src/main/js`. Support files and data models 
are under `src/main/java`
- Running the example: ensure that you have GraalVM in your `$PATH`

    ```
     mvn package -Prun
    ```

- The example will run Graal's `node` implementation with JVM interop enabled, 
  and Drools on the classpath.

