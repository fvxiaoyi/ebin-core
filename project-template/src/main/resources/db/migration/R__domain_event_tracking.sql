create table if not exists `domain_event_tracking` (
    id                      bigint          not null    auto_increment,
    aggregate_root_class    varchar(255)    not null,
    aggregate_root_id       bigint          not null,
    created_time            datetime(6)     not null,
    payload                 varchar(255),
    primary key (id)
) engine=InnoDB;