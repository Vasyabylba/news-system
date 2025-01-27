-- liquibase formatted sql

-- changeset Vasiliy:create-table-comment
CREATE TABLE comments (
  id UUID NOT NULL,
   created_at TIMESTAMP WITHOUT TIME ZONE,
   last_modified_at TIMESTAMP WITHOUT TIME ZONE,
   text TEXT,
   username VARCHAR NOT NULL,
   news_id UUID NOT NULL,
   CONSTRAINT pk_comment PRIMARY KEY (id)
);
