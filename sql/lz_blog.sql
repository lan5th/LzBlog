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
    on blogs (create_time);

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
    user_id     bigint                               not null
);

create index blog_id_index
    on comment (blog_id);

create index create_time_index
    on comment (create_time);

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
    qq_id       bigint                               null,
    avatar_url  varchar(50)                          null,
    deleted     tinyint(1) default 0                 not null,
    is_admin    tinyint(1) default 0                 not null,
    create_time timestamp  default CURRENT_TIMESTAMP not null
);

create index qq_id__index
    on user_info (qq_id);

