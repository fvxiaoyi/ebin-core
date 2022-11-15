# core-module-jpa-mysql

Use hikari connection pool, still use DataSourceProperties for configuration. The default ISOLATION is READ_COMMITTED.

### Example
1. config file:
~~~
spring:
  datasource:
    url: jdbc:mysql://192.168.136.128:30001/demo?useUnicode=true&characterEncoding=utf-8&useSSL=false&allowPublicKeyRetrieval=true
    username: demo
    password: demo123
  jpa:
    common:
      basePackagePath: apps.example
    mysql:
      packagesToScan:
        - apps.example.*
~~~