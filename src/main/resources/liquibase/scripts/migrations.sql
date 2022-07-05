-- liquibase formatted sql

-- changeset shkril:1
CREATE TABLE notification_task
(
    id                bigserial PRIMARY KEY,
    chat_id           bigint    NOT NULL,
    text              text      NOT NULL,
    date_time         timestamp NOT NULL
);

