# Serverless Workflow – OpenTelemetry (Quarkus) + Jaeger
This module demonstrates **end-to-end OpenTelemetry tracing** for
**SonataFlow** running on **Quarkus**, with
traces exported to **Jaeger** and validated via **integration tests**.

It covers:
- HTTP entrypoints
- Workflow execution spans
- Subflows
- Error paths
- Persistence + resume via events
- Native image execution (GraalVM)

All traces are asserted by querying Jaeger directly.

---

## What is being tested

The module contains **6 integration tests**, grouped by concern.

### 1. `OpenTelemetryJaegerIT`
**Baseline tracing validation**

Verifies that:
- HTTP requests generate server spans
- Workflow execution produces `sonataflow.process.*` spans
- Mandatory workflow tags are present:
  - `sonataflow.process.id`
  - `sonataflow.process.instance.id`
  - `sonataflow.workflow.state`
- Traces are correctly exported to Jaeger via OTLP

This is the “smoke test” for OpenTelemetry wiring.

---

### 2. `OpenTelemetryJaegerIT` (transaction fallback)
**Transaction propagation logic**

Validates that:
- When an explicit transaction header is present, it is propagated
- When the transaction header is missing, the workflow **falls back to the process instance id**
- The resolved transaction id is visible in workflow spans

This ensures correlation is always available in traces.

---

### 3. `OpenTelemetryJaegerIT` (subflow)
**Subflow tracing and correlation**

Validates that:
- A parent workflow calling a subflow generates **multiple workflow spans**
- Parent and subflow spans share the same:
  - `sonataflow.transaction.id`
  - `sonataflow.process.instance.id`
- Subflow execution is visible in Jaeger as part of the same trace

This confirms **subflow-workflow trace continuity**.

---

### 4. `PersistWait01StartIT`
**Persistent workflow start**

Validates that:
- A workflow configured with persistence starts correctly
- The process instance id is generated and stored
- A trace exists for the “start + persist” phase
- Workflow state before suspension is visible in tracing data

This test proves persistence does not break tracing.

---

### 5. `PersistWait02ResumeIT`
**Resume after persistence via event**

Validates that:
- A persisted workflow is resumed via a CloudEvent (`/resume`)
- The workflow continues execution from the suspended state
- Context (transaction id, instance id, workflow state) is preserved
- Traces after resume belong to the same logical workflow execution

This is the key test proving **persistence + events + tracing** work together.

---

### 6. `OpenTelemetryJaegerIT` (error workflow)
**Error propagation in traces**

Validates that:
- A failing workflow produces error spans
- Error conditions are visible in Jaeger
- Workflow metadata is still attached to errored spans

This ensures observability even when workflows fail.

All tests are executed **both in JVM mode and native mode**.

---

## Debugging Jaeger traces

You can enable verbose trace logging during test execution by passing:

```sh
-Dtest.jaeger.debug=true
```

When this flag is enabled, tests will print:

- All span operation names found in the trace

- Workflow-related tags (sonataflow.*, transaction, tracker)

- Workflow state transitions observed in the trace

The flag is disabled by default to keep CI logs concise.

---

## Native image testing

Note that this requires GRAALVM_HOME to point to a valid GraalVM installation

```sh
mvn clean install -Pnative
```

Run the full integration test suite against the native binary

- GraalVM builds a native image and wire extensions at build time
- Persistence, Flyway, OpenTelemetry, and messaging must all work correctly
- Tracing must survive native compilation

### How native tests are executed
- The application is built as a native binary
- The binary is launched by Quarkus test infrastructure
- Testcontainers provide:
  - Jaeger (OTLP + Query)
  - PostgreSQL (workflow persistence)
- The same tests assert behavior against the native binary


---

## Supporting infrastructure

During tests, the following containers are started automatically:

- **Jaeger all-in-one**
  - OTLP gRPC 
  - Query API used for assertions
- **PostgreSQL**
  - Used by SonataFlow persistence
  - Schema initialized via Flyway

No external services are required.

---

## Summary

This module provides a **reference example** for assurance of:

- OpenTelemetry tracing for SonataFlow workflows
- Jaeger-based trace validation
- Workflow correlation and context propagation
- Persistent workflows with event-based resume
- JVM and native execution parity

It is intended as both:
- A regression quality test
- A blueprint for production-ready observability setups

