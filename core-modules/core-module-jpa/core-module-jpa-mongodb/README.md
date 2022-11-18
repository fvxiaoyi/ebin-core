# core-module-jpa-mongodb

Default READ_PREFERENCE is SECONDARY_PREFERRED.

### Example
1. config file:
~~~
mongodb:
  host: 192.168.136.128:30002
  database: demo
  packagesToScan:
    - apps.user.*
~~~