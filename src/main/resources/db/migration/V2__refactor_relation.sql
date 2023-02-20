ALTER TABLE attachment
    ADD COLUMN attached_message_id BIGINT REFERENCES message(id);

DROP TABLE jt_attached_messages;