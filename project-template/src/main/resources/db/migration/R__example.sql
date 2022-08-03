create table if not exists `example` (
    id              bigint      not null auto_increment,
    name          varchar(255),
    created_time    datetime(6) not null,
    primary key (id)
) engine=InnoDB;

SELECT COUNT(*) INTO @index FROM information_schema.`COLUMNS` WHERE table_schema=DATABASE()
AND table_name='example'
AND column_name='name';
SET @SQL=IF(@index=0,'alter table example add column name varchar(255)','select \'Exists Column\';');
PREPARE statement FROM @SQL;
EXECUTE statement;
