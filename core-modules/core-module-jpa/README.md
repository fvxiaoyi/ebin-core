# core-module-jpa

Implement basic connection pool configuration, DDD aggregation, domain events, and query services.

### Domain event
When the AggregateRoot commits the transaction, the domain event is persisted first, followed by the rise event. Events are divided into PerCommit and AfterCommit. The PerCommit event is executed synchronously before the transaction is committed, and the transaction will be interrupted if an exception occurs. The AfterCommit event is executed after the transaction is committed and can be selected synchronously or asynchronously.

### Query service


[Mysql](core-module-jpa-mysql/README.md)

[MongoDB](core-module-jpa-mongodb/README.md)