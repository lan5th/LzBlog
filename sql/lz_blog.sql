create table blogs
(
    id            bigint                               not null
        primary key,
    blog_name     varchar(50)                          not null,
    location      varchar(100)                         null,
    update_time   timestamp  default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP,
    create_time   timestamp  default CURRENT_TIMESTAMP not null,
    deleted       tinyint(1) default 0                 not null,
    short_content varchar(150)                         null,
    views         int        default 0                 null,
    tag_id        bigint                               not null
);

create index create_time_key
    on blogs (create_time)
    comment '时间排序';

create index tag_id_index
    on blogs (tag_id);

create table comment
(
    id          bigint                               not null
        primary key,
    blog_id     bigint     default -1                null,
    content     varchar(150)                         null,
    create_time timestamp  default CURRENT_TIMESTAMP not null,
    deleted     tinyint(1) default 0                 not null,
    user_id     bigint                               not null,
    reply_to    bigint                               null comment '回复某个评论的id'
);

create index blog_id_index
    on comment (blog_id);

create index create_time_index
    on comment (create_time);

create table config_info
(
    id           bigint auto_increment comment 'id'
        primary key,
    data_id      varchar(255)                           not null comment 'data_id',
    group_id     varchar(255)                           null,
    content      longtext                               not null comment 'content',
    md5          varchar(32)                            null comment 'md5',
    gmt_create   datetime     default CURRENT_TIMESTAMP not null comment '创建时间',
    gmt_modified datetime     default CURRENT_TIMESTAMP not null comment '修改时间',
    src_user     text                                   null comment 'source user',
    src_ip       varchar(50)                            null comment 'source ip',
    app_name     varchar(128)                           null,
    tenant_id    varchar(128) default ''                null comment '租户字段',
    c_desc       varchar(256)                           null,
    c_use        varchar(64)                            null,
    effect       varchar(64)                            null,
    type         varchar(64)                            null,
    c_schema     text                                   null,
    constraint uk_configinfo_datagrouptenant
        unique (data_id, group_id, tenant_id)
)
    comment 'config_info' collate = utf8_bin;

create table links
(
    id          bigint                               not null
        primary key,
    link_name   varchar(50)                          not null,
    url         varchar(200)                         not null,
    create_time timestamp  default CURRENT_TIMESTAMP not null,
    type        int                                  null comment '推荐文章：1
友情链接：2
',
    enabled     tinyint(1) default 1                 null
)
    comment '链接';

create table tags
(
    id          bigint                               not null
        primary key,
    tag_name    varchar(15)                          null,
    create_time timestamp  default CURRENT_TIMESTAMP not null,
    deleted     tinyint(1) default 0                 not null
);

create table user_auth
(
    id       bigint               not null
        primary key,
    password varchar(20)          not null,
    deleted  tinyint(1) default 0 not null
);

create table user_info
(
    id          bigint                               not null
        primary key,
    name        varchar(30)                          not null,
    avatar_url  varchar(200)                         null,
    deleted     tinyint(1) default 0                 not null,
    is_admin    tinyint(1) default 0                 not null,
    create_time timestamp  default CURRENT_TIMESTAMP not null,
    openid      varchar(50)                          null comment 'qq登录获取到的openid'
);

create index openid__index
    on user_info (openid);
