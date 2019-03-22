# Drools Polyglot 
A simple experiment with GraalVM capabilities.

- JS sources are under `src/main/js`. Support files and data models 
are under `src/main/java`

## Running the Examples

### Plain Java

This example defines a rule using the canonical model Java API, 
and creates a session that fires that rule.
 
```
 mvn package -Prunjava
```

### JavaScript

This example defines the same rule and creates a similar session to 
the Java example, but it does all of this using JS as the language.

Ensure that you have GraalVM in your `$PATH`, then:

```
 mvn package -Prunjs
```

The example will run Graal's `node` implementation with JVM interop enabled, 
and Drools on the classpath. You will notice the difference because the node 
engine will emit a diagnostic warning (we are using ES modules):

```
(node:20300) ExperimentalWarning: The ESM module loader is experimental.
```

