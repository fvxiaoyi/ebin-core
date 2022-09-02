FROM busybox:1.34.1
COPY /opentelemetry-javaagent.jar /opentelemetry-javaagent.jar
VOLUME ["/otel-javaagent"]
CMD ["cp", "/opentelemetry-javaagent.jar", "/otel-javaagent/opentelemetry-javaagent.jar"]