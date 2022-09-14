## Introduction

For the open source dashboard (eg: grafana temp), failure causes can be found from each span. 

Change span state to error and add error_message, error_code to attribute.


## Build and add extensions

To build this extension project, run `./gradlew build`. You can find the resulting jar file in `build/libs/`. 

To add the extension to the instrumentation agent:

1. Copy the jar file to a host that is running an application to which you've attached the OpenTelemetry Java instrumentation.
2. Modify the startup command to add the full path to the extension file. For example:

     ```bash
     java -javaagent:path/to/opentelemetry-javaagent.jar \
          -Dotel.javaagent.extensions=build/libs/opentelemetry-java-instrumentation-extension-demo-1.0-all.jar
          -jar myapp.jar
     ```
Note: to load multiple extensions, you can specify a comma-separated list of extension jars or directories (that
contain extension jars) for the `otel.javaagent.extensions` value.

## Embed extensions in the OpenTelemetry Agent

To simplify deployment, you can embed extensions into the OpenTelemetry Java Agent to produce a single jar file. With an integrated extension, you no longer need the `-Dotel.javaagent.extensions` command line option.

For more information, see the `extendedAgent` task in [build.gradle](build.gradle).