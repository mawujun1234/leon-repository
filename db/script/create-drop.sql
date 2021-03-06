
    alter table t_city 
       drop 
       foreign key fk_z3zr4xwr;

    drop table if exists t_city;

    drop table if exists t_coplxid1entity;

    drop table if exists t_coplxid2entity;

    create table t_city (
       id varchar(36) not null comment '主键id',
        age integer default 18 comment '年龄',
        create_date datetime(6) comment '创建时间',
        name varchar(30) comment '名称',
        price double precision default 0.0 comment '价格',
        sex varchar(10) default Man comment '创建时间',
        primary key (id)
    ) comment='表注释' engine=InnoDB;

    create table t_coplxid1entity (
       id1 varchar(255) not null comment 'id1注释',
        id2 varchar(255) not null comment 'id2注释',
        name varchar(255),
        primary key (id1, id2)
    ) engine=InnoDB;

    create table t_coplxid2entity (
       id1 varchar(255) not null comment 'id1注释0000',
        id2 varchar(255) not null comment 'id1注释0000',
        name varchar(255),
        primary key (id1, id2)
    ) engine=InnoDB;

    alter table t_city 
       add constraint fk_z3zr4xwr 
       foreign key (name, sex) 
       references t_coplxid1entity (id1, id2);
