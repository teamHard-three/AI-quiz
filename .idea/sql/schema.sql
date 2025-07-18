create table if not exists aiquiz.user
(
    id           bigint                                not null comment 'id'
        primary key,
    userAccount  varchar(255)                          not null comment '用户账号',
    userPassword varchar(255)                          not null comment '用户密码',
    userName     varchar(255)                          null comment '用户昵称',
    userAvatar   varchar(1024)                         null comment '用户头像',
    userProfile  text                                  null comment '用户简介',
    userRole     varchar(50) default 'user'            null comment '用户角色：user/teacher',
    createTime   datetime    default CURRENT_TIMESTAMP null comment '创建时间',
    updateTime   datetime    default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP comment '更新时间',
    isDelete     tinyint(1)  default 0                 null comment '是否删除 0-未删 1-已删'
)
    comment '用户表';

create table if not exists aiquiz.student_course
(
    id         bigint auto_increment comment '选课记录ID'
        primary key,
    studentId  bigint                                                             not null comment '学生用户ID（user.id 且 userRole = student）',
    courseId   bigint                                                             not null comment '课程ID（course.id）',
    status     enum ('pending', 'approved', 'rejected') default 'pending'         null comment '选课状态：pending=待审批，approved=已通过，rejected=已驳回',
    isDelete   tinyint(1)                               default 0                 null comment '逻辑删除 0-未删，1-已删',
    createTime datetime                                 default CURRENT_TIMESTAMP null comment '创建时间',
    updateTime datetime                                 default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP comment '更新时间',
    constraint uniq_student_course
        unique (studentId, courseId),
    constraint fk_enroll_course
        foreign key (courseId) references aiquiz.course (id)
            on delete cascade,
    constraint fk_enroll_student
        foreign key (studentId) references aiquiz.user (id)
            on delete cascade
)
    comment '学生选课关系表（带审批状态）';
create table aiquiz.course_question
(
    id         bigint auto_increment comment '主键ID'
        primary key,
    courseId   bigint                               not null comment '课程ID，关联course表',
    question   longtext                             null,
    createTime datetime   default CURRENT_TIMESTAMP null comment '创建时间',
    updateTime datetime   default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP comment '更新时间',
    isDelete   tinyint(1) default 0                 null comment '是否删除',
    constraint fk_course_question_set_course
        foreign key (courseId) references aiquiz.course (id)
            on delete cascade
);
create table aiquiz.course_content
(
    id         bigint auto_increment comment '主键ID，自增'
        primary key,
    course_id  bigint                               not null comment '课程ID，关联course表的id',
    content    longtext                             null comment 'PDF中解析出的全部文本内容（可能非常长）',
    createTime datetime   default CURRENT_TIMESTAMP null comment '创建时间',
    updateTime datetime   default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP comment '更新时间',
    isDelete   tinyint(1) default 0                 null comment '是否删除：0-未删除，1-已删除',
    constraint fk_pdf_course
        foreign key (course_id) references aiquiz.course (id)
            on delete cascade
);
create table aiquiz.course
(
    id          bigint auto_increment comment '课程ID'
        primary key,
    name        varchar(255)                         not null comment '课程名称',
    description text                                 null comment '课程描述',
    teacherId   bigint                               not null comment '授课教师用户ID，关联user表id',
    createTime  datetime   default CURRENT_TIMESTAMP null comment '创建时间',
    updateTime  datetime   default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP comment '更新时间',
    isDelete    tinyint(1) default 0                 null comment '是否删除 0-未删 1-已删',
    constraint fk_course_teacher
        foreign key (teacherId) references aiquiz.user (id)
            on delete cascade
)
    comment '课程表';