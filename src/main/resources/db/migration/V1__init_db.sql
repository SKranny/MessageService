CREATE SEQUENCE hibernate_sequence START 1;

CREATE TABLE person (
    id                      BIGINT PRIMARY KEY,
    person_id               BIGINT NOT NULL,
    user_name               VARCHAR(255) NOT NULL
);

CREATE INDEX person_id_index on person (person_id);

CREATE TABLE chat (
    id                      BIGINT PRIMARY KEY,
    name                    VARCHAR(255) NOT NULL,
    photo                   VARCHAR(255),
    type                    VARCHAR(255) NOT NULL DEFAULT 'PRIVATE',
    description             VARCHAR(255),
    admin_id                BIGINT REFERENCES person(id),
    create_date_time        TIMESTAMP WITHOUT TIME ZONE
);

CREATE TABLE jt_person_chat (
    person_id               BIGINT REFERENCES person(id),
    chat_id                 BIGINT REFERENCES chat(id)
);

CREATE TABLE message (
    id                      BIGINT PRIMARY KEY,
    text                    VARCHAR(255),
    author_id               BIGINT REFERENCES person(id),
    chat_id                 BIGINT REFERENCES chat(id),
    is_change               BOOLEAN NOT NULL DEFAULT false,
    create_date_time        TIMESTAMP WITHOUT TIME ZONE NOT NULL
);

CREATE TABLE jt_message_like (
    person_id               BIGINT REFERENCES person(id),
    message_id              BIGINT REFERENCES message(id)
);

CREATE TABLE jt_message_delete (
    person_id               BIGINT REFERENCES person(id),
    message_id              BIGINT REFERENCES message(id)
);

CREATE TABLE attachment (
    id                      BIGINT PRIMARY KEY,
    message_id              BIGINT REFERENCES message(id),
    type                    VARCHAR(255) NOT NULL DEFAULT 'MESSAGE',
    content                 VARCHAR(255),
    post_id                 BIGINT
);

CREATE TABLE jt_attached_messages (
    attachment_id           BIGINT REFERENCES attachment(id),
    message_id              BIGINT REFERENCES message(id)
);




