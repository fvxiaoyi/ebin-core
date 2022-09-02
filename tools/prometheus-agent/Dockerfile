FROM busybox:1.34.1
COPY /jmx_prometheus_javaagent-0.16.1.jar /tmp/jmx_prometheus_javaagent.jar
COPY /config.yaml /tmp/config.yaml
VOLUME ["/prometheus-javaagent"]
ENTRYPOINT ["cp", "-a" ,"/tmp/." ,"/prometheus-javaagent"]